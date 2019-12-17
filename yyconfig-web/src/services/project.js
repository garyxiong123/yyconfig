import { requestPost, requestGet, requestPut, requestDelete } from '@/utils/request';
const project = {
  getProject: async function (params = {}) {
    return requestGet('/apps/search', params);
  },
  projectAdd: async function (params = {}) {
    return requestPost('/apps', params);
  },
  projectEdit: async function (params = {}) {
    return requestPut(`/apps/${params.appId}`, params);
  },
}
export {
  project
}
