import { requestPost } from '@/utils/request';
export async function userLogin(payload) {
  return requestPost('/user/login',payload);
}
export async function userLogout(payload) {
  return requestPost('/user/logout',payload);
}