const routes = [
  {
    path: '/user',
    component: '../layouts/UserLayout',
    routes: [
      {
        name: 'login',
        path: '/user/login',
        component: './user/login',
      },
    ],
  },
  {
    path: '/',
    component: '../layouts/SecurityLayout',
    routes: [
      {
        path: '/',
        component: '../layouts/BasicLayout',
        authority: ['admin', 'user'],
        routes: [
          {
            path: '/',
            redirect: '/project',
          },
          {
            path: '/project',
            name: 'project',
            component: './project',
          },
          {
            path: '/project-create',
            name: 'projectCreate',
            component: './project/create/index',
          },
          {
            path: '/project/details/:id',
            name: 'projectDetails',
            component: './project/details/index',
          },
          {
            path: '/auth',
            name: 'auth',
            component: './auth',
          },
          {
            path: '/system',
            name: 'system',
            component: './system',
          },
          // {
          //   path: '/welcome',
          //   name: 'welcome',
          //   icon: 'smile',
          //   component: './Welcome',
          // },
          {
            path: '/admin',
            name: 'admin',
            icon: 'crown',
            component: './Admin',
            authority: ['admin'],
          },
          {
            component: './404',
          },
        ],
      },
      {
        component: './404',
      },
    ],
  },
  {
    component: './404',
  },
];
export default routes;