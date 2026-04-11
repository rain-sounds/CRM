import http from '@/api/http';

export const getSubmissionPage = (data) => {
  return http.post('/api/submission/page', data);
};

export const getSubmissionDetail = (id) => {
  return http.get(`/api/submission/get/${id}`);
};

export const addSubmission = (data) => {
  return http.post('/api/submission/add', data);
};

export const updateSubmission = (data) => {
  return http.post('/api/submission/update', data);
};

export const batchUpdateSubmission = (data) => {
  return http.post('/api/submission/batch/update', data);
};

export const deleteSubmission = (id) => {
  return http.get(`/api/submission/delete/${id}`);
};

export const batchDeleteSubmission = (ids) => {
  return http.post('/api/submission/batch/delete', ids);
};
