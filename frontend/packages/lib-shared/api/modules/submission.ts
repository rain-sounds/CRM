import type { CordysAxios } from '@lib/shared/api/http/Axios';
import {
  AddSubmissionUrl,
  BatchDeleteSubmissionUrl,
  BatchUpdateSubmissionUrl,
  DeleteSubmissionUrl,
  GetSubmissionDetailUrl,
  GetSubmissionPageUrl,
  UpdateSubmissionUrl,
  DragSortSubmissionUrl,
  GetSubmissionFormConfigUrl,
} from '@lib/shared/api/requrls/submission';
import type { TableDraggedParams } from '@lib/shared/models/common';
import type { FormDesignConfigDetailParams } from '@lib/shared/models/system/module';

export default function useSubmissionApi(CDR: CordysAxios) {
  function getSubmissionPage(data: any) {
    return CDR.post({ url: GetSubmissionPageUrl, data });
  }

  function getSubmissionDetail(id: string) {
    return CDR.get({ url: `${GetSubmissionDetailUrl}/${id}` });
  }

  function getSubmissionFormConfig() {
    return CDR.get<FormDesignConfigDetailParams>({ url: GetSubmissionFormConfigUrl });
  }

  function addSubmission(data: any) {
    return CDR.post({ url: AddSubmissionUrl, data });
  }

  function updateSubmission(data: any) {
    return CDR.post({ url: UpdateSubmissionUrl, data });
  }

  function batchUpdateSubmission(data: any) {
    return CDR.post({ url: BatchUpdateSubmissionUrl, data });
  }

  function deleteSubmission(id: string) {
    return CDR.get({ url: `${DeleteSubmissionUrl}/${id}` });
  }

  function batchDeleteSubmission(ids: string[]) {
    return CDR.post({ url: BatchDeleteSubmissionUrl, data: ids });
  }

  function dragSortSubmission(data: TableDraggedParams) {
    return CDR.post({ url: DragSortSubmissionUrl, data });
  }

  return {
    getSubmissionPage,
    getSubmissionDetail,
    getSubmissionFormConfig,
    addSubmission,
    updateSubmission,
    batchUpdateSubmission,
    deleteSubmission,
    batchDeleteSubmission,
    dragSortSubmission,
  };
}