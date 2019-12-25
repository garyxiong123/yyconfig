import { system } from '@/services/system';

const SystemModel = {
  namespace: 'system',
  state: {
    openNamespaceType: []
  },
  effects: {
    *openNamespaceType({ payload }, { call, put }) {
      const response = yield call(system.getOpenNamespaceType, payload);
      yield put({
        type: 'setOpenNamespaceType',
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
  },
};
export default SystemModel;
