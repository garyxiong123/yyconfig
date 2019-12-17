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
      let rows = state.appList && state.appList.rows ? state.appList.rows : [];
      let data = payload.data || {};
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
  },
};
export default ProjectModel;
