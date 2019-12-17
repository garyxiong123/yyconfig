import { project } from '@/services/project';

const ProjectModel = {
  namespace: 'project',
  state: {
    appList: {},
  },
  effects: {
    *appList({ payload }, { call, put }) {
      const response = yield call(project.getProject, payload);
      yield put({
        type: 'setProject',
        payload: response,
      });
    },
  },
  reducers: {
    setProject(state, { payload = {} }) {
      return {
        ...state,
        appList: payload.data || {},
      };
    },
  },
};
export default ProjectModel;
