<template>
  <div class="submission-detail">
    <h2>投稿详情</h2>
    <el-descriptions border :column="2">
      <el-descriptions-item label="名称">{{ detail.name }}</el-descriptions-item>
      <el-descriptions-item label="产品">{{ detail.productId }}</el-descriptions-item>
      <el-descriptions-item label="商机">{{ detail.opportunityId }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
      <!-- 动态字段展示 -->
      <template v-for="field in detail.moduleFields">
        <el-descriptions-item :key="field.id" :label="field.name">{{ field.value }}</el-descriptions-item>
      </template>
    </el-descriptions>
    <div style="margin-top: 20px">
      <el-button @click="$router.back()">返回</el-button>
    </div>
  </div>
</template>

<script>
  import { getSubmissionDetail } from '@/api/submission';

  export default {
    name: 'SubmissionDetail',
    data() {
      return {
        detail: {},
      };
    },
    created() {
      const { id } = this.$route.query;
      if (id) {
        this.fetchData(id);
      }
    },
    methods: {
      fetchData(id) {
        getSubmissionDetail(id).then((response) => {
          this.detail = response.data;
        });
      },
    },
  };
</script>
