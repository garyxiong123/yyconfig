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
        localStorage.setItem('YYuser', JSON.stringify(userInfo));
        yield put(routerRedux.replace('/project'));
      }
    },

    *getCaptcha({ payload }, { call }) {
      yield call(getFakeCaptcha, payload);
    },

    *logout({ payload }, { call, put }) {
      const response = yield call(userLogout, payload);
      yield put({
        type: 'changeLoginStatus',
        payload: {},
      });

      if (response && response.code == '1') {
        localStorage.clear();
        yield put(routerRedux.replace('/user/login'));
      }
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
