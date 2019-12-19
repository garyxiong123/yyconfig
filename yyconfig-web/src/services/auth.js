import { requestPost, requestGet, requestPut, requestDelete } from '@/utils/request';
const auth = {
  modifyPassword: async function (params = {}) {
    return requestPost('/user/modifyPassword', params);
  },
  getUser: async function (params = {}) {
    return requestPost('/user/getPage', params);
  },
  getUserAll: async function (params = {}) {
    return requestPost('/user/getList', params);
  },
  userAdd: async function (params = {}) {
    return requestPost('/user/add', params);
  },
  userEdit: async function (params = {}) {
    return requestPost('/user/edit', params);
  },
  userDelete: async function (params = {}) {
    return requestPost('/user/delete', params);
  },

}
const department = {
  getDepartment: async function (params = {}) {
    return requestGet('/department', params);
  },
  departmentAdd: async function (params = {}) {
    return requestPost('/department', params);
  },
  departmentEdit: async function (params = {}) {
    return requestPut(`/department/${params.departmentId}`, params);
  },
  departmentDelete: async function (params = {}) {
    return requestDelete(`/department/${params.departmentId}`, params);
  },
}
export {
  auth,
  department
}