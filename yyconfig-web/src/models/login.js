import { routerRedux } from 'dva/router';
import { stringify } from 'querystring';
import { userLogin, userLogout } from '@/services/login';
import { setAuthority } from '@/utils/authority';
import { getPageQuery } from '@/utils/utils';
const Model = {
  namespace: 'login',
  state: {
    status: undefined,
  },
  effects: {
    *login({ payload }, { call, put }) {
      const response = yield call(userLogin, payload);
      yield put({
        type: 'changeLoginStatus',
        payload: response,     
      }); // Login successfully

      if (response && response.code == '1') {
        //获取菜单
        // const res = yield call(auth.getUserPermissions);
        let userInfo = response.data;
        localStorage.setItem('user', JSON.stringify(userInfo));
        yield put(routerRedux.replace('/project'));
        // const urlParams = new URL(window.location.href);
        // const params = getPageQuery();
        // let { redirect } = params;

        // if (redirect) {
        //   const redirectUrlParams = new URL(redirect);

        //   if (redirectUrlParams.origin === urlParams.origin) {
        //     redirect = redirect.substr(urlParams.origin.length);

        //     if (redirect.match(/^\/.*#/)) {
        //       redirect = redirect.substr(redirect.indexOf('#') + 1);
        //     }
        //   } else {
        //     window.location.href = '/';
        //     return;
        //   }
        // }

        // yield put(routerRedux.replace(redirect || '/'));
      }
    },

    *getCaptcha({ payload }, { call }) {
      yield call(getFakeCaptcha, payload);
    },

    *logout(_, { put }) {
      const response = yield call(userLogout, payload);
      yield put({
        type: 'changeLoginStatus',
        payload: {},
      });

      if (response && response.code == '1') {
        localStorage.clear();
        yield put(routerRedux.replace('/user/login'));
      }
      // const { redirect } = getPageQuery(); // redirect

      // if (window.location.pathname !== '/user/login' && !redirect) {
      //   yield put(
      //     routerRedux.replace({
      //       pathname: '/user/login',
      //       search: stringify({
      //         redirect: window.location.href,
      //       }),
      //     }),
      //   );
      // }
    },
  },
  reducers: {
    changeLoginStatus(state, { payload }) {
      // setAuthority(payload.currentAuthority);
      return { ...state, type: 'account' };
    },
  },
};
export default Model;
