import { FormSchema } from '/@/components/Table';
import { isRoleExist } from './role.api';
import { defHttp } from '/@/utils/http/axios';

export const columns = [
  {
    title: '角色名称',
    dataIndex: 'roleName',
    width: 100,
  },
  {
    title: '角色编码',
    dataIndex: 'roleCode',
    width: 100,
  },
    //租户名称使用api回显tenantId的name
  {
    title: '所属租户',
    dataIndex: 'tenantId_dictText',
    width: 100,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 100,
  },
];
/**
 * 角色用户Columns
 */
export const userColumns = [
  {
    title: '用户账号',
    dataIndex: 'username',
  },
  {
    title: '用户姓名',
    dataIndex: 'realname',
  },
  {
    title: '状态',
    dataIndex: 'status_dictText',
    width: 80,
  },
];
export const searchFormSchema: FormSchema[] = [
  {
    field: 'roleName',
    label: '角色名称',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'roleCode',
    label: '角色编码',
    component: 'Input',
    colProps: { span: 6 },
  },
  {
    field: 'tenantId',
    label: '所属租户', 
    component: 'ApiSelect',
    componentProps: {
      //multiple: 多选；不填写为单选
     // mode: 'multiple',
      //请求api,返回结果{ result: { records:[{'id':'1',name:'scott'},{'id':'2',name:'小张'}] }}
      api: () => defHttp.get({ url: '/sys/tenant/list' }),
      //数值转成String
      numberToString: false,
      //标题字段
      labelField: 'name',
      //值字段
      valueField: 'id',
      //请求参数
      params: {},
      //返回结果字段
      resultField: 'records',
    },
  },
  {
    field: 'createTime',
    label: '创建时间',
    component: 'DatePicker',
    componentProps: {
      format: 'YYYY-MM-DD HH:mm:ss',
      showTime: true,
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
    colProps: { span: 6 },
  }
];
/**
 * 角色用户搜索form
 */
export const searchUserFormSchema: FormSchema[] = [
  {
    field: 'username',
    label: '用户账号',
    component: 'Input',
    colProps: { span: 12 },
    labelWidth: 74,
  },
];

export const formSchema: FormSchema[] = [
  {
    field: 'id',
    label: '',
    component: 'Input',
    show: false,
  },
  {
    field: 'roleName',
    label: '角色名称',
    required: true,
    component: 'Input',
  },
  {
    field: 'roleCode',
    label: '角色编码',
    required: true,
    component: 'Input',
    dynamicDisabled: ({ values }) => {
      return !!values.id;
    },
    dynamicRules: ({ values, model }) => {
      console.log('values:', values);
      return [
        {
          required: true,
          validator: (_, value) => {
            if (!value) {
              return Promise.reject('请输入角色编码');
            }
            if (values) {
              return new Promise((resolve, reject) => {
                isRoleExist({ id: model.id, roleCode: value,tenantId: model.tenantId })
                  .then((res) => {
                    res.success ? resolve() : reject(res.message || '校验失败');
                  })
                  .catch((err) => {
                    reject(err.message || '验证失败');
                  });
              });
            }
            return Promise.resolve();
          },
        },
      ];
    },
  },
  //此处下拉选择租户管理中的租户，使用api查询 2025年5月6日09:42:10
  {
    label: '所属租户',
    field: 'tenantId',
    component: 'ApiSelect',
    componentProps: {
      //multiple: 多选；不填写为单选
     // mode: 'multiple',
      //请求api,返回结果{ result: { records:[{'id':'1',name:'scott'},{'id':'2',name:'小张'}] }}
      api: () => defHttp.get({ url: '/sys/tenant/list' }),
      //数值转成String
      numberToString: false,
      //标题字段
      labelField: 'name',
      //值字段
      valueField: 'id',
      //请求参数
      params: {},
      //返回结果字段
      resultField: 'records',
    },
  },
  {
    label: '备注',
    field: 'description',
    component: 'InputTextArea',
  },
];

export const formDescSchema = [
  {
    field: 'roleName',
    label: '角色名称',
  },
  {
    field: 'roleCode',
    label: '角色编码',
  },
  {
    label: '备注',
    field: 'description',
  },
];

export const roleIndexFormSchema: FormSchema[] = [
  {
    field: 'id',
    label: '',
    component: 'Input',
    show: false,
  },
  {
    label: '角色编码',
    field: 'roleCode',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '首页路由',
    field: 'url',
    component: 'Input',
    required: true,
    helpMessage: '首页路由的访问地址',
  },
  {
    label: '组件地址',
    field: 'component',
    component: 'Input',
    helpMessage: '首页路由的组件地址',
    componentProps: {
      placeholder: '请输入前端组件',
    },
    required: true,
  },
  {
    field: 'route',
    label: '是否路由菜单',
    helpMessage: '非路由菜单设置成首页，需开启',
    component: 'Switch',
    defaultValue: true
  },
  {
    label: '优先级',
    field: 'priority',
    component: 'InputNumber',
  },
  {
    label: '是否开启',
    field: 'status',
    component: 'JSwitch',
    componentProps: {
      options: ['1', '0'],
    },
  },
];
