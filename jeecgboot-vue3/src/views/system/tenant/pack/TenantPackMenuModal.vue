<template>
  <BasicModal v-bind="$attrs" :zIndex="1000" @register="registerModal" :title="title" @ok="handleSubmit" width="800px" destroyOnClose>
    <BasicForm @register="registerForm" >
      <template #permissionSlot="{ }">
        <a-button type="primary" @click="openPermissionDrawer()">设置权限</a-button>
      </template>
    </BasicForm>
  </BasicModal>
  <TenantPackUserMenu @register="rolePermissionDrawer"  @getDetailInfo="getDetailPermission"/>
</template>
<script lang="ts" setup name="tenant-pack-menu-modal">
  import { ref, computed, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { packMenuFormSchema } from '../tenant.data';
  import { addPackPermission, editPackPermission } from '../tenant.api';
  //import RolePermissionDrawer from '/@/views/system/role/components/RolePermissionDrawer.vue';
  import TenantPackUserMenu from './TenantPackUserMenu.vue';
  import { useDrawer } from '/@/components/Drawer';

  const [rolePermissionDrawer, { openDrawer: openRolePermissionDrawer }] = useDrawer();

  const isUpdate = ref<boolean>(false);
  // Emits声明
  const emit = defineEmits(['register', 'success']);
  const detailFromMenu = ref({});

  function getDetailPermission(val) {
    const cloneVal = { ...val };
    if (Array.isArray(cloneVal.permissionIds)) {
      cloneVal.permissionIds = cloneVal.permissionIds.join(',');
    }
    detailFromMenu.value = cloneVal;
    setFieldsValue({ ...cloneVal });
  }

  //表单配置
  const [registerForm, { resetFields, setFieldsValue, validate, setProps, getFieldsValue }] = useForm({
  schemas: packMenuFormSchema,
  showActionButtonGroup: false,
});

  //租户
  const tenantId = ref<number>();
  //产品包类型
  const packType = ref<number>();
  const drawerVisible = ref(false);  //  控制抽屉的可见性
  let currentRecord = [];  //  当前点击的记录
  //表单赋值
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    //重置表单
    await resetFields();
    isUpdate.value = !!data?.isUpdate;
    if(data.tenantId){
      tenantId.value = data.tenantId;
    }
    packType.value = data.packType;
    if (unref(isUpdate)) {
      //表单赋值
      console.log(">>>>>>>>>"+data.record)
      
      await setFieldsValue({ ...data.record });
    }
    //update-begin---author:wangshuai ---date:20230705  for：【QQYUN-5685】2 产品包增加一个查看：添加底部有没有按钮及表单禁用------------
    setModalProps({ confirmLoading: false, showCancelBtn:!!data?.showFooter, showOkBtn:!!data?.showFooter });
    // 隐藏底部时禁用整个表单
    setProps({ disabled: !data?.showFooter })
    //update-end---author:wangshuai ---date:20230705  for：【QQYUN-5685】2 产品包增加一个查看：添加底部有没有按钮及表单禁用------------
  });
  //设置标题
  const title = computed(() => (unref(isUpdate) ? '编辑租户产品包' : '新增租户产品包'));
    //  打开权限抽屉的函数
  const openPermissionDrawer = () => {
    const formValues = getFieldsValue(); // 获取表单所有字段的值
    //formvalues添加一个type属性
    formValues.type = isUpdate.value;
    formValues.permissionIds = formValues.permissionIds || "";
    // 打开抽屉，并传入参数
    openRolePermissionDrawer(true, {
      records: formValues
    });
  };
  //表单提交事件
  async function handleSubmit(v) {
    const values = await validate();
    setModalProps({ confirmLoading: true });
    // 合并子组件传来的值
    //Object.assign(values, detailFromMenu.value);
    values.packType = unref(packType);
    if(values.packType === 'custom'){
      values.tenantId = unref(tenantId);
    }else{
      values.tenantId = 0;
    }
    if (!unref(isUpdate)) {
      await addPackPermission(values);
    } else {
      await editPackPermission(values);
    }
    emit('success');
    setModalProps({ confirmLoading: false });
    closeModal();
  }
</script>
