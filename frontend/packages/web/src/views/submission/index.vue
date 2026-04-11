<template>
  <div class="submission-container">
    <h2>投稿列表</h2>
    <div class="filter-container">
      <el-input
        v-model="listQuery.keyword"
        placeholder="关键字"
        style="width: 200px"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-select v-model="listQuery.status" placeholder="状态" clearable style="width: 90px" class="filter-item">
        <el-option label="全部" value="" />
        <el-option label="待处理" value="1" />
        <el-option label="已处理" value="2" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
      <el-button
        v-permission="['submission:add']"
        class="filter-item"
        type="primary"
        icon="el-icon-edit"
        @click="handleCreate"
        >新增</el-button
      >
      <el-button
        v-permission="['submission:delete']"
        class="filter-item"
        type="danger"
        icon="el-icon-delete"
        @click="handleBatchDelete"
        >批量删除</el-button
      >
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="名称" prop="name" align="center" />
      <el-table-column label="产品" prop="productId" align="center" />
      <el-table-column label="商机" prop="opportunityId" align="center" />
      <el-table-column label="状态" prop="status" align="center" />
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="{ row }">
          <el-button v-permission="['submission:update']" type="primary" size="mini" @click="handleUpdate(row)"
            >编辑</el-button
          >
          <el-button v-permission="['submission:read']" type="info" size="mini" @click="handleDetail(row)"
            >详情</el-button
          >
          <el-button v-permission="['submission:delete']" size="mini" type="danger" @click="handleDelete(row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.current"
      :limit.sync="listQuery.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
  import { batchDeleteSubmission, deleteSubmission, getSubmissionPage } from '@/api/submission';

  export default {
    name: 'Submission',
    data() {
      return {
        list: null,
        total: 0,
        listLoading: true,
        listQuery: {
          current: 1,
          pageSize: 20,
          keyword: undefined,
          status: undefined,
        },
      };
    },
    created() {
      this.getList();
    },
    methods: {
      getList() {
        this.listLoading = true;
        getSubmissionPage(this.listQuery).then((response) => {
          this.list = response.data.list || response.data.records || response.data;
          this.total = response.data.total || 0;
          this.listLoading = false;
        });
      },
      handleFilter() {
        this.listQuery.current = 1;
        this.getList();
      },
      handleCreate() {
        this.$router.push('/submission/form');
      },
      handleUpdate(row) {
        this.$router.push(`/submission/form?id=${row.id}`);
      },
      handleDetail(row) {
        this.$router.push(`/submission/detail?id=${row.id}`);
      },
      handleDelete(row) {
        this.$confirm('确认删除?', '提示', { type: 'warning' }).then(() => {
          deleteSubmission(row.id).then(() => {
            this.$message.success('删除成功');
            this.getList();
          });
        });
      },
      handleBatchDelete() {
        // Implementation for batch delete
      },
    },
  };
</script>
