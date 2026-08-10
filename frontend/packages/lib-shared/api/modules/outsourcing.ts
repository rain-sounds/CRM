import type { CordysAxios } from '@lib/shared/api/http/Axios';
import type { FormDesignConfigDetailParams } from '@lib/shared/models/system/module';
import type { TableQueryParams } from '@lib/shared/models/common';

import {
  OutsourcingPageUrl,
  OutsourcingAddUrl,
  OutsourcingUpdateUrl,
  OutsourcingDeleteUrl,
  GetOutsourcingDetailUrl,
  GetOutsourcingFormConfigUrl,
  GetOutsourcingTabUrl,
  ExportOutsourcingAllUrl,
  ExportOutsourcingSelectedUrl,
  AddOutsourcingViewUrl,
  UpdateOutsourcingViewUrl,
  GetOutsourcingViewListUrl,
  GetOutsourcingViewDetailUrl,
  FixedOutsourcingViewUrl,
  EnableOutsourcingViewUrl,
  DeleteOutsourcingViewUrl,
  DragOutsourcingViewUrl,
} from '@lib/shared/api/requrls/outsourcing';

export default function useOutsourcingApi(CDR: CordysAxios) {
  return {
    // 外包列表
    getOutsourcingList: (data: TableQueryParams) => CDR.post({ url: OutsourcingPageUrl, data }),
    addOutsourcing: (data: any) => CDR.post({ url: OutsourcingAddUrl, data }),
    updateOutsourcing: (data: any) => CDR.post({ url: OutsourcingUpdateUrl, data }),
    deleteOutsourcing: (id: string) => CDR.get({ url: `${OutsourcingDeleteUrl}/${id}` }),
    getOutsourcingDetail: (id: string) => CDR.get({ url: `${GetOutsourcingDetailUrl}/${id}` }),
    getOutsourcingFormConfig: () => CDR.get<FormDesignConfigDetailParams>({ url: GetOutsourcingFormConfigUrl }),
    getOutsourcingTab: () => CDR.get({ url: GetOutsourcingTabUrl }),

    // 外包导出
    exportOutsourcingAll: (data: any) => CDR.post({ url: ExportOutsourcingAllUrl, data }),
    exportOutsourcingSelected: (data: any) => CDR.post({ url: ExportOutsourcingSelectedUrl, data }),

    // 外包视图
    addOutsourcingView: (data: any) => CDR.post({ url: AddOutsourcingViewUrl, data }),
    updateOutsourcingView: (data: any) => CDR.post({ url: UpdateOutsourcingViewUrl, data }),
    getOutsourcingViewList: (data: any) => CDR.post({ url: GetOutsourcingViewListUrl, data }),
    getOutsourcingViewDetail: (id: string) => CDR.get({ url: `${GetOutsourcingViewDetailUrl}/${id}` }),
    fixedOutsourcingView: (data: any) => CDR.post({ url: FixedOutsourcingViewUrl, data }),
    enableOutsourcingView: (data: any) => CDR.post({ url: EnableOutsourcingViewUrl, data }),
    deleteOutsourcingView: (id: string) => CDR.get({ url: `${DeleteOutsourcingViewUrl}/${id}` }),
    dragOutsourcingView: (data: any) => CDR.post({ url: DragOutsourcingViewUrl, data }),
  };
}
