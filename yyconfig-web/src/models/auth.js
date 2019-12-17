import { auth, department } from '@/services/auth';

const AuthModel = {
  namespace: 'auth',
  state: {
    userList: {},
    departmentList: [],
    userListAll: []
  },
  effects: {
    *userList({ payload }, { call, put }) {
      const response = yield call(auth.getUser, payload);
      yield put({
        type: 'setUser',
        payload: response,
      });
    },
    *userListAll({ payload }, { call, put }) {
      const response = yield call(auth.getUserAll, payload);
      yield put({
        type: 'setUserAll',
        payload: response,
      });
    },
    *departmentList({ payload }, { call, put }) {
      const response = yield call(department.getDepartment, payload);
      yield put({
        type: 'setDepartment',
        payload: response,
      });
    },
   
  },
  reducers: {
    setUser(state, { payload = {} }) {
      return {
        ...state,
        userList: payload.data || {},
      };
    },
    setUserAll(state, { payload = {} }) {
      return {
        ...state,
        userListAll: payload.data || [],
      };
    },
    setDepartment(state, { payload = {} }) {
      return {
        ...state,
        departmentList: payload.data || [],
      };
    },
  },
};
export default AuthModel;
