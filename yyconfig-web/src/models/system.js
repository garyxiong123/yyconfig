import { system } from '@/services/system';

const SystemModel = {
  namespace: 'system',
  state: {
    openNamespaceType: [],
    serverConfigList: [],

  },
  effects: {
    *openNamespaceType({ payload }, { call, put }) {
      const response = yield call(system.getOpenNamespaceType, payload);
      yield put({
        type: 'setOpenNamespaceType',
        payload: response,
      });
    },
    *serverConfigList({ payload }, { call, put }) {
      const response = yield call(system.serverConfigList, payload);
      yield put({
        type: 'setServerConfigList',
        payload: response,
      });
    },

  },
  reducers: {
    setOpenNamespaceType(state, { payload = {} }) {
      return {
        ...state,
        openNamespaceType: payload.data || [],
      };
    },
    setServerConfigList(state, { payload = {} }) {
      return {
        ...state,
        serverConfigList: payload.data || [],
      };
    },
  },
};
export default SystemModel;
