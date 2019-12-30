/**
 * request 网络请求工具
 * 更详细的 api 文档: https://github.com/umijs/umi-request
 */
import { extend } from 'umi-request';
import { notification, message } from 'antd';
import router from 'umi/router';
import debounce from 'lodash/debounce';
const codeMessage = {
  200: '服务器成功返回请求的数据。',
  201: '新建或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
  401: '用户没有权限（令牌、用户名、密码错误）。',
  403: '用户得到授权，但是访问是被禁止的。',
  404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};
/**
 * 异常处理程序
 */
const errorHandler = error => {
  const { response = {} } = error;
  const errortext = codeMessage[response.status] || response.statusText;
  const { status, url } = response;
  // console.log("response===>", error)
  // notification.error({
  //   message: `请求错误 ${status}: ${url}`,
  //   description: errortext,
  // });
};
/**
 * 配置request请求时的默认参数
 */
let SERVER_HOME = '.';
// const SERVER_HOME = 'http://test.lb.gs.youyuwo.com:60008';

if (process.env.NODE_ENV === 'development') {
  SERVER_HOME = 'http://localhost:8080';
}

const request = extend({
  errorHandler,
  // 默认错误处理
  credentials: 'include', // 默认请求是否带上cookie
  headers: {},
  responseType: 'json',
});
const ERROR_CODE = {
  loginOverDue: ['01030008', '01030011'],
};
request.interceptors.response.use(async response => {
  if (response.status !== 200) {
    const errortext = codeMessage[response.status] || '未知错误';
    message.error(errortext);
    return false;
  }
  const data = await response.clone().json();
  if (ERROR_CODE.loginOverDue.indexOf(data.code) > -1) {
    // debounce(()=>{
    //   message.error("登录信息过期，请重新登录")
    // }, 1000);
    router.replace('/user/login');
    localStorage.clear();
    return false;
  }
  if (data && data.code !== '1') {
    message.error(data.desc);
  }
  return response;
});

function fetchData(url, params) {
  let currentUrl = `${SERVER_HOME}${url}`;
  return request(currentUrl, params);
}
const requestGet = (url, params) => fetchData(url, { method: 'GET', params });
const requestPost = (url, data) => fetchData(url, { method: 'POST', data });
const requestPut = (url, data) => fetchData(url, { method: 'PUT', data });
const requestDelete = (url, data) => fetchData(url, { method: 'DELETE', data });
export { requestGet, requestPost, requestPut, requestDelete };
