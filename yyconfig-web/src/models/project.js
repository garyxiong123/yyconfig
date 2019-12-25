import { project } from '@/services/project';

const ProjectModel = {
  namespace: 'project',
  state: {
    appList: {},
    appDetail: {},
    envList: [],
    currentEnv: {},
    nameSpaceList: [],
    publicNamespaceList: [],
    releasesActiveInfo: [],
    releasesCompare: [],
    nameSpaceListWithApp: []
  },
  effects: {
    *appList({ payload }, { call, put }) {
      const response = yield call(project.getProject, payload);
      yield put({
        type: 'setProject',
        payload: response,
      });
    },
    *appDetail({ payload }, { call, put }) {
      const response = yield call(project.projectDetail, payload);
      yield put({
        type: 'setProjectDetail',
        payload: response,
      });
    },
    *envList({ payload }, { call, put }) {
      const response = yield call(project.envList, payload);
      yield put({
        type: 'setEnvList',
        payload: response,
      });
    },
    *nameSpaceList({ payload }, { call, put }) {
      const response = yield call(project.nameSpaceList, payload);
      yield put({
        type: 'setNameSpaceList',
        payload: response,
      });
    },
    *publicNamespaceList({ payload }, { call, put }) {
      const response = yield call(project.publicNamespaceList, payload);
      yield put({
        type: 'setPublicNamespaceList',
        payload: response,
      });
    },
    *releasesActiveInfo({ payload }, { call, put }) {
      const response = yield call(project.releasesActive, payload);
      yield put({
        type: 'setReleasesActive',
        payload: response,
      });
    },
    *releasesCompare({ payload }, { call, put }) {
      const response = yield call(project.releasesCompare, payload);
      yield put({
        type: 'setReleasesCompare',
        payload: response,
      });
    },
    *nameSpaceListWithApp({ payload }, { call, put }) {
      const response = yield call(project.nameSpaceListWithApp, payload);
      yield put({
        type: 'setNameSpaceListWithApp',
        payload: response,
      });
    },
  },
  reducers: {
    setProject(state, { payload = {} }) {
      let data = payload.data || {};
      let rows = data.pageNum === 1 ? [] : state.appList.rows ? state.appList.rows : [];
      return {
        ...state,
        appList: {
          ...data,
          rows: [
            ...rows,
            ...data.rows
          ]
        },
      };
    },
    setProjectDetail(state, { payload = {} }) {
      return {
        ...state,
        appDetail: payload.data || {}
      }
    },
    setEnvList(state, { payload = {} }) {
      return {
        ...state,
        envList: payload.data || []
      }
    },
    setCurrentEnv(state, { payload = {} }) {
      return {
        ...state,
        currentEnv: payload
      }
    },
    setNameSpaceList(state, { payload = {} }) {
      return {
        ...state,
        nameSpaceList: payload.data || []
      }
    },
    setPublicNamespaceList(state, { payload = {} }) {
      return {
        ...state,
        publicNamespaceList: payload.data || []
      }
    },
    setReleasesActive(state, { payload = {} }) {
      return {
        ...state,
        releasesActiveInfo: payload.data || []
      }
    },
    setReleasesCompare(state, { payload = {} }) {
      return {
        ...state,
        releasesCompare: payload.data || []
      }
    },
    setNameSpaceListWithApp(state, { payload = {} }) {
      return {
        ...state,
        nameSpaceListWithApp: payload.data || []
      }
    },
    clearData(state, { payload = {} }) {
      return {
        ...state,
        ...payload
      }
    },
  },
};
export default ProjectModel;
