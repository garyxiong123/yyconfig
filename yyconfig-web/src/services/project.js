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
  projectDetail: async function (params = {}) {
    return requestGet(`/apps/${params.appId}`, params);
  },
  envList: async function (params = {}) {
    return requestGet(`/apps/${params.appId}/navtree`);
  },
  publicNamespaceList: async function (params = {}) {
    return requestGet(`/app/namespaces/public`);
  },
  nameSpaceList: async function (params = {}) {
    return requestGet(`/apps/${params.appCode}/envs/${params.env}/clusters/${params.clusterName}/namespaces`);
  },
  //创建项目私有命名空间
  nameSpacePrivateAdd: async function (params = {}) {
    return requestPost(`/apps/${params.appId}/namespaces/private`, params);
  },
  //创建项目受保护命名空间
  nameSpaceProtectAdd: async function (params = {}) {
    return requestPost(`/apps/${params.appId}/namespaces/protect`, params);
  },
  //创建项目公开命名空间
  nameSpacePublicAdd: async function (params = {}) {
    return requestPost(`/apps/${params.appId}/namespaces/public`, params);
  },
  configAdd: async function (params = {}) {
    return requestPost(`/item/createItem`, params);
  },
  configUpdate: async function (params = {}) {
    return requestPost(`/item/updateItem`, params);
  },
  
};
const cluster = {
  clusterAdd: async function (params = {}) {
    return requestPost(`/apps/${params.appId}/envs/${params.env}/clusters/${params.clusterName}`);
  }
}
export {
  project,
  cluster
}
