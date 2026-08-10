import { OutsourcingRouteEnum } from '@/enums/routeEnum';

import { DEFAULT_LAYOUT } from '../base';
import type { AppRouteRecordRaw } from '../types';

const outsourcing: AppRouteRecordRaw = {
  path: '/outsourcing',
  name: OutsourcingRouteEnum.OUTSOURCING,
  redirect: '/outsourcing/outsourcingExperiment',
  component: DEFAULT_LAYOUT,
  meta: {
    locale: 'module.outsourcing',
    permissions: ['OUTSOURCING:READ'],
    icon: 'iconicon_contract',
    hideChildrenInMenu: true,
    collapsedLocale: 'module.outsourcing',
  },
  children: [
    {
      path: 'outsourcingExperiment',
      name: OutsourcingRouteEnum.OUTSOURCING_EXPERIMENT,
      component: () => import('@/views/outsourcing/outsourcingExperiment/index.vue'),
      meta: {
        locale: 'module.outsourcingExperiment',
        isTopMenu: true,
        permissions: ['OUTSOURCING:READ'],
      },
    },
  ],
};

export default outsourcing;
