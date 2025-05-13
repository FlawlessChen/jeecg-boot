<template>
  <BasicDrawer v-bind="$attrs" :zIndex="2000"  @register="registerDrawer" width="650px"
  destroyOnClose showFooter>
  <!-- zindex为优先级，其中model默认值为1000，将此页面的drawer设置为2000，实现优先展示 -->
    <template #title>
      套餐权限配置
      <a-dropdown>
        <Icon icon="ant-design:more-outlined" class="more-icon" />
        <template #overlay>
          <a-menu @click="treeMenuClick">
            <a-menu-item key="checkAll">选择全部</a-menu-item>
            <a-menu-item key="cancelCheck">取消选择</a-menu-item>
            <div class="line"></div>
            <a-menu-item key="openAll">展开全部</a-menu-item>
            <a-menu-item key="closeAll">折叠全部</a-menu-item>
            <div class="line"></div>
            <a-menu-item key="relation">层级关联</a-menu-item>
            <a-menu-item key="standAlone">层级独立</a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </template>
    <BasicTree
      ref="treeRef"
      checkable
      :treeData="treeData"
      :checkedKeys="checkedKeys"
      :expandedKeys="expandedKeys"
      :selectedKeys="selectedKeys"
      :clickRowToExpand="false"
      :checkStrictly="true"
      title="所拥有的的权限"
      @check="onCheck"
      @select="onTreeNodeSelect"
    >
      <template #title="{ slotTitle, ruleFlag }">
        {{ slotTitle }}
        <Icon v-if="ruleFlag" icon="ant-design:align-left-outlined" style="margin-left: 5px; color: red"></Icon>
      </template>
    </BasicTree>
    <!--右下角按钮-->
    <template #footer>
      <!-- <PopConfirmButton title="确定放弃编辑？" @confirm="closeDrawer" okText="确定" cancelText="取消"></PopConfirmButton> -->
      <a-button @click="closeDrawer">取消</a-button>
      <a-button @click="addToList()" type="primary" :loading="loading">添加到套餐</a-button>
    </template>
    <RoleDataRuleDrawer @register="registerDrawer1" />
  </BasicDrawer>
</template>
<script lang="ts" setup>
  import { ref, computed, unref, onMounted ,watch} from 'vue';
  import { BasicDrawer, useDrawer, useDrawerInner } from '/@/components/Drawer';
  import { BasicTree, TreeItem } from '/@/components/Tree';
  import { PopConfirmButton } from '/@/components/Button';
  import RoleDataRuleDrawer from '/@/views/system/role/components/RoleDataRuleDrawer.vue';
  import { queryTreeListForRoleByTenant,queryTreeListForRole, queryRolePermission, saveRolePermission,queryTreeList } from '/@/views/system/role/role.api';
  import { useI18n } from "/@/hooks/web/useI18n";
  import { ROLE_AUTH_CONFIG_KEY } from '/@/enums/cacheEnum';
  import { addPackPermission, editPackPermission } from '../tenant.api';
  
  const emit = defineEmits(['register','getDetailInfo']);
  //树的信息
  const treeData = ref<TreeItem[]>([]);
  //树的全部节点信息
  const allTreeKeys = ref([]);
  //树的选择节点信息
  const checkedKeys = ref<any>([]);
  const defaultCheckedKeys = ref([]);
  //树的选中的节点信息
  const selectedKeys = ref([]);
  const roleId = ref('');
  const tenantId = ref('');
  //树的实例
  const treeRef = ref(null);
  const loading = ref(false);
  const detailInfo = ref<any>({});
  //展开折叠的key
  const expandedKeys = ref<any>([]);
  //父子节点选中状态是否关联 true不关联，false关联
  const checkStrictly = ref<boolean>(false);
  const [registerDrawer1, { openDrawer: openDataRuleDrawer }] = useDrawer();
  const [registerDrawer, { setDrawerProps, closeDrawer }] = useDrawerInner(async (data) => {
    detailInfo.value = data.records;
    //debugger
    await reset();
    setDrawerProps({ confirmLoading: false, loading: true });
    roleId.value = data.roleId;
    tenantId.value = data.tenantId;
    //初始化数据--此数据为树的所有数据
    //const roleResult = await queryTreeListForRoleByTenant({ roleId: unref(roleId), tenantId: unref(tenantId) });
    const roleResult = await queryTreeListForRole();
    // update-begin--author:liaozhiyang---date:20240228---for：【QQYUN-8355】角色权限配置的菜单翻译
    treeData.value = translateTitle(roleResult.treeList);
    // update-end--author:liaozhiyang---date:20240228---for：【QQYUN-8355】角色权限配置的菜单翻译
    allTreeKeys.value = roleResult.ids;
    console.log('>>>>>>>>>>>>>>>>>>>>.allTreeKeys', allTreeKeys.value);
    // update-begin--author:liaozhiyang---date:20240531---for：【TV360X-590】角色授权弹窗操作缓存
    const localData = localStorage.getItem(ROLE_AUTH_CONFIG_KEY);
    if (localData) {
      const obj = JSON.parse(localData);
      obj.level && treeMenuClick({ key: obj.level });
      obj.expand && treeMenuClick({ key: obj.expand });
    } else {
      expandedKeys.value = roleResult.ids;
    }
    // update-end--author:liaozhiyang---date:20240531---for：【TV360X-590】角色授权弹窗操作缓存
    //初始化角色菜单数据--此数据是选中的数据
    //const permResult = await queryRolePermission({ roleId: unref(roleId), tenantId: unref(tenantId) });
    const permResult = data.records.permissionIds?.split(',').map(item => item.trim());;//todo这边测试一下需要的格式  2025年5月13日09:57:39
    //把data.permissionId的格式["1","2","3"]改为arrays格式
    checkedKeys.value = permResult;
    console.log('>>>>>>>>>>>>>>>>>>>>.checkedKeys', checkedKeys.value);
    defaultCheckedKeys.value = permResult;
    setDrawerProps({ loading: false });
  });
  /**
  * 2024-02-28
  * liaozhiyang
  * 翻译菜单名称
   */
  function translateTitle(data) {
    if (data?.length) {
      data.forEach((item) => {
        if (item.slotTitle) {
          const { t } = useI18n();
          if (item.slotTitle.includes("t('") && t) {
            item.slotTitle = new Function('t', `return ${item.slotTitle}`)(t);
          }
        }
        if (item.children?.length) {
          translateTitle(item.children);
        }
      });
    }
    return data;
  }
  /**
   * 点击选中
   * 2024-04-26
   * liaozhiyang
   */
  function onCheck(o, e) {
    // checkStrictly: true=>层级独立，false=>层级关联.
    if (checkStrictly.value) {
      checkedKeys.value = o.checked ? o.checked : o;
    } else {
      const keys = getNodeAllKey(e.node, 'children', 'key');
      if (e.checked) {
        // 反复操作下可能会有重复的keys，得用new Set去重下
        checkedKeys.value = [...new Set([...checkedKeys.value, ...keys])];
      } else {
        const result = removeMatchingItems(checkedKeys.value, keys);
        checkedKeys.value = result;
      }
    }
  }
  /**
   * 2024-04-26
   * liaozhiyang
   * 删除相匹配数组的项
   */
  function removeMatchingItems(arr1, arr2) {
    // 使用哈希表记录 arr2 中的元素
    const hashTable = {};
    for (const item of arr2) {
      hashTable[item] = true;
    }
    // 使用 filter 方法遍历第一个数组，过滤出不在哈希表中存在的项
    return arr1.filter((item) => !hashTable[item]);
  }
  /**
   * 2024-04-26
   * liaozhiyang
   * 获取当前节点及以下所有子孙级的key
   */
  function getNodeAllKey(node: any, children: any, key: string) {
    const result: any = [];
    result.push(node[key]);
    const recursion = (data) => {
      data.forEach((item: any) => {
        result.push(item[key]);
        if (item[children]?.length) {
          recursion(item[children]);
        }
      });
    };
    node[children]?.length && recursion(node[children]);
    return result;
  }

  /**
   * 选中节点，打开数据权限抽屉
   */
  function onTreeNodeSelect(key) {
    if (key && key.length > 0) {
      selectedKeys.value = key;
    }
    openDataRuleDrawer(true, { functionId: unref(selectedKeys)[0], roleId: unref(roleId) });
  }
  /**
   * 数据重置
   */
  function reset() {
    treeData.value = [];
    allTreeKeys.value = [];
    checkedKeys.value = [];
    defaultCheckedKeys.value = [];
    selectedKeys.value = [];
    roleId.value = '';
  }
  /**
   * 获取tree实例
   */
  function getTree() {
    const tree = unref(treeRef);
    if (!tree) {
      throw new Error('tree is null!');
    }
    return tree;
  }

  
  /**
   * 添加到列表
   */
  async function addToList() {
    if(detailInfo.value.packType === 'custom'){
      detailInfo.value.tenantId = unref(tenantId);
    }else{
      detailInfo.value.tenantId = 0;
    }
    detailInfo.value.permissionIds = checkedKeys.value
    closeDrawer();
  }

  watch(detailInfo, (newVal) => {
    emit('getDetailInfo', newVal);
  }, { deep: true });

  /**
   * 树菜单选择
   * @param key
   */
  function treeMenuClick({ key }) {
    if (key === 'checkAll') {
      checkedKeys.value = allTreeKeys.value;
    } else if (key === 'cancelCheck') {
      checkedKeys.value = [];
    } else if (key === 'openAll') {
      expandedKeys.value = allTreeKeys.value;
      saveLocalOperation('expand', 'openAll');
    } else if (key === 'closeAll') {
      expandedKeys.value = [];
      saveLocalOperation('expand', 'closeAll');
    } else if (key === 'relation') {
      checkStrictly.value = false;
      saveLocalOperation('level', 'relation');
    } else {
      checkStrictly.value = true;
      saveLocalOperation('level', 'standAlone');
    }
  }
  /**
   * 2024-05-31
   * liaozhiyang
   * 【TV360X-590】角色授权弹窗操作缓存
   * */
  const saveLocalOperation = (key, value) => {
    const localData = localStorage.getItem(ROLE_AUTH_CONFIG_KEY);
    const obj = localData ? JSON.parse(localData) : {};
    obj[key] = value;
    localStorage.setItem(ROLE_AUTH_CONFIG_KEY, JSON.stringify(obj))
  };
</script>

<style lang="less" scoped>
  /** 固定操作按钮 */
  .jeecg-basic-tree {
    position: absolute;
    width: 618px;
  }
  //update-begin---author:wangshuai ---date:20230202  for：抽屉弹窗标题图标下拉样式------------
  .line {
    height: 1px;
    width: 100%;
    border-bottom: 1px solid #f0f0f0;
  }
  .more-icon {
    font-size: 20px !important;
    color: black;
    display: inline-flex;
    float: right;
    margin-right: 2px;
    cursor: pointer;
  }
  :deep(.jeecg-tree-header) {
    border-bottom: none;
  }
  //update-end---author:wangshuai ---date:20230202  for：抽屉弹窗标题图标下拉样式------------
</style>
