import { requestPost } from '@/utils/request';
const auth = {
  modifyPassword: async function(params = {}) {
    return requestPost('/user/modifyPassword', params);
  },
}
export {
  auth
}