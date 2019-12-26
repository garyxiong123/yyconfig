import { requestPost, requestGet, requestPut, requestDelete } from '@/utils/request';
const project = {
  getProject: async function (params = {}) {
    return requestGet('/apps/search', params);
  },
  getProjectAll: async function (params = {}) {
    return requestGet('/apps', params);
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
  //新增配置
  configAdd: async function (params = {}) {
    return requestPost(`/item/createItem`, params);
  },
  //更新配置
  configUpdate: async function (params = {}) {
    return requestPost(`/item/updateItem`, params);
  },
  //删除配置
  configDelete: async function (params = {}) {
    return requestPost(`/item/deleteItem`, params);
  },
  createRelease: async function (params = {}) {
    return requestPost(`/createRelease`, params);
  },
  //获取回滚信息
  releasesActive: async function (params = {}) {
    return requestGet(`/namespaceId/${params.namespaceId}/releases/active`);
  },
  //获取回滚历史对比
  releasesCompare: async function (params = {}) {
    return requestGet(`/releases/compare`, params);
  },
  //回滚操作
  rollBack: async function (params = {}) {
    return requestPut(`/releases/${params.releaseId}/rollback`);
  },
  //
  nameSpaceListWithApp: async function (params = {}) {
    return requestPost(`/namespaceList`, params);
  },
  //文本编辑
  modifyItemsByTexts: async function (params = {}) {
    return requestPost(`/item/modifyItemsByTexts`, params);
  },
  //更改历史
  commitFind: async function (params = {}) {
    return requestGet(`/commit/find`, params);
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
