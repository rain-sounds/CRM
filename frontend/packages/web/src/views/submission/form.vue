<template>
  <div class="submission-form">
    <h2>{{ isEdit ? '编辑投稿' : '新增投稿' }}</h2>
    <el-form ref="form" :model="formData" :rules="rules" label-width="120px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入名称" />
      </el-form-item>
      <el-form-item label="产品" prop="productId">
        <el-input v-model="formData.productId" placeholder="请输入产品" />
      </el-form-item>
      <el-form-item label="商机" prop="opportunityId">
        <el-input v-model="formData.opportunityId" placeholder="请输入商机" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" placeholder="请选择状态">
          <el-option label="待处理" value="1" />
          <el-option label="已处理" value="2" />
        </el-select>
      </el-form-item>

      <!-- 动态表单组件占位 -->
      <dynamic-form-component v-model="formData.moduleFields" :fields="moduleFields" />

      <el-form-item>
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import { addSubmission, getSubmissionDetail, updateSubmission } from '@/api/submission';

  export default {
    name: 'SubmissionForm',
    data() {
      return {
        isEdit: false,
        moduleFields: [], // 动态表单字段
        formData: {
          name: '',
          productId: '',
          opportunityId: '',
          status: '',
          moduleFields: [],
        },
        rules: {
          name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
          productId: [{ required: true, message: '请选择产品', trigger: 'blur' }],
          opportunityId: [{ required: true, message: '请选择商机', trigger: 'blur' }],
        },
      };
    },
    created() {
      const { id } = this.$route.query;
      if (id) {
        this.isEdit = true;
        this.fetchData(id);
      }
    },
    methods: {
      fetchData(id) {
        getSubmissionDetail(id).then((response) => {
          this.formData = { ...this.formData, ...response.data };
        });
      },
      submitForm() {
        this.$refs.form.validate((valid) => {
          if (valid) {
            const action = this.isEdit ? updateSubmission : addSubmission;
            action(this.formData).then(() => {
              this.$message.success('保存成功');
              this.$router.back();
            });
          }
        });
      },
    },
  };
</script>
