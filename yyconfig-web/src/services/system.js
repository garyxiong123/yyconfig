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
}
export {
  system
}