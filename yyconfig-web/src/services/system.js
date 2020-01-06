import { requestPost, requestGet, requestPut, requestDelete } from '@/utils/request';
const system = {
  getOpenNamespaceType: async function (params = {}) {
    return requestGet('/openNamespaceType', params);
  },
  openNamespaceTypeAdd: async function (params = {}) {
    return requestPost('/openNamespaceType', params);
  },
  openNamespaceTypeEdit: async function (params = {}) {
    return requestPut(`/openNamespaceType/${params.id}`, params);
  },
  openNamespaceTypeDelete: async function (params = {}) {
    return requestDelete(`/openNamespaceType/${params.id}`);
  },
  //系统参数列表
  serverConfigList: async function (params = {}) {
    return requestGet('/server/config/list', params);
  },
  //创建|修改 系统参数
  serverConfigEdit: async function (params = {}) {
    return requestPost('/server/config', params);
  },
}
export {
  system
}