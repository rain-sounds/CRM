import { SubmissionRouteEnum } from '@/enums/routeEnum';

import { DEFAULT_LAYOUT } from '../base';
import type { AppRouteRecordRaw } from '../types';

const submission: AppRouteRecordRaw = {
  path: '/submission',
  name: SubmissionRouteEnum.SUBMISSION,
  redirect: '/submission/index',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'menu.submission',
    permissions: ['submission:read'],
    icon: 'iconicon_target', // Using an existing icon or a generic one
    hideChildrenInMenu: true,
    collapsedLocale: 'menu.submission',
  },
  children: [
    {
      path: 'index',
      name: SubmissionRouteEnum.SUBMISSION_INDEX,
      component: () => import('@/views/submission/index.vue'),
      meta: {
        locale: 'menu.submission',
        permissions: ['submission:read'],
        isTopMenu: true,
      },
    },
  ],
};

export default submission;
