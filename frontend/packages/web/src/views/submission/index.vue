<template>
  <CrmCard hide-footer no-content-bottom-padding>
    <div :key="tableRefreshIdKey" class="h-full">
      <CrmTable
        ref="crmTableRef"
        v-model:checked-row-keys="checkedRowKeys"
        v-bind="propsRes"
        class="crm-submission-table"
        :action-config="actionConfig"
        :draggable="hasAnyPermission(['submission:update'])"
        @page-change="propsEvent.pageChange"
        @page-size-change="propsEvent.pageSizeChange"
        @sorter-change="propsEvent.sorterChange"
        @filter-change="propsEvent.filterChange"
        @batch-action="handleBatchAction"
        @drag="dragHandler"
        @refresh="searchData"
      >
        <template #actionLeft>
          <div class="flex items-center gap-[12px]">
            <n-button
              v-permission="['submission:add']"
              type="primary"
              @click="
                {
                  activeSubmissionId = '';
                  formCreateDrawerVisible = true;
                }
              "
            >
              {{ t('common.add') }}
            </n-button>
          </div>
        </template>
        <template #actionRight>
          <CrmSearchInput v-model:value="keyword" class="!w-[240px]" @search="searchData" />
        </template>
      </CrmTable>
    </div>
  </CrmCard>

  <CrmFormCreateDrawer
    v-model:visible="formCreateDrawerVisible"
    :form-key="FormDesignKeyEnum.SUBMISSION"
    :source-id="activeSubmissionId"
    :need-init-detail="!!activeSubmissionId"
    @saved="handleRefresh"
  />

  <CrmBatchEditModal
    v-model:visible="showEditModal"
    v-model:field-list="fieldList"
    :ids="checkedRowKeys"
    :form-key="FormDesignKeyEnum.SUBMISSION"
    @refresh="handleRefresh"
  />

  <detailDrawer
    v-model:visible="detailDrawerVisible"
    :source-id="activeSubmissionId"
    :refresh-id="tableRefreshId"
    @edit="handleEdit"
  />
</template>

<script lang="ts" setup>
  import { h, onMounted, ref, watch } from 'vue';
  import { useRouter } from 'vue-router';
  import { DataTableRowKey, NButton, useMessage } from 'naive-ui';

  import { FormDesignKeyEnum } from '@lib/shared/enums/formDesignEnum';
  import { useI18n } from '@lib/shared/hooks/useI18n';
  import { characterLimit } from '@lib/shared/method';
  import type { TableDraggedParams } from '@lib/shared/models/common';

  import CrmCard from '@/components/pure/crm-card/index.vue';
  import type { ActionsItem } from '@/components/pure/crm-more-action/type';
  import CrmSearchInput from '@/components/pure/crm-search-input/index.vue';
  import CrmTable from '@/components/pure/crm-table/index.vue';
  import { BatchActionConfig } from '@/components/pure/crm-table/type';
  import CrmTableButton from '@/components/pure/crm-table-button/index.vue';
  import CrmBatchEditModal from '@/components/business/crm-batch-edit-modal/index.vue';
  import CrmFormCreateDrawer from '@/components/business/crm-form-create-drawer/index.vue';
  import CrmOperationButton from '@/components/business/crm-operation-button/index.vue';
  import detailDrawer from './detail.vue';

  import { batchDeleteSubmission, deleteSubmission, dragSortSubmission } from '@/api/modules';
  import useFormCreateTable from '@/hooks/useFormCreateTable';
  import useModal from '@/hooks/useModal';
  import { hasAnyPermission } from '@/utils/permission';

  const router = useRouter();
  const { openModal } = useModal();
  const { t } = useI18n();
  const Message = useMessage();

  const checkedRowKeys = ref<DataTableRowKey[]>([]);
  const keyword = ref('');
  const formCreateDrawerVisible = ref(false);
  const activeSubmissionId = ref('');
  const tableRefreshId = ref(0);
  const tableRefreshIdKey = ref(0);
  const showEditModal = ref(false);
  const detailDrawerVisible = ref(false);

  const actionConfig: BatchActionConfig = {
    baseAction: [
      {
        label: t('common.batchEdit'),
        key: 'batchEdit',
        permission: ['submission:update'],
      },
      {
        label: t('common.batchDelete'),
        key: 'batchDelete',
        permission: ['submission:delete'],
      },
    ],
  };

  // 批量删除
  function handleBatchDelete() {
    openModal({
      type: 'error',
      title: t('common.deleteConfirmTitle', { name: `${checkedRowKeys.value.length}条数据` }),
      content: t('common.deleteConfirmContent'),
      positiveText: t('common.confirmDelete'),
      negativeText: t('common.cancel'),
      onPositiveClick: async () => {
        try {
          await batchDeleteSubmission(checkedRowKeys.value as string[]);
          checkedRowKeys.value = [];
          tableRefreshId.value += 1;
          Message.success(t('common.deleteSuccess'));
        } catch (error) {
          console.error(error);
        }
      },
    });
  }

  function handleBatchEdit() {
    showEditModal.value = true;
  }

  function handleBatchAction(item: ActionsItem) {
    switch (item.key) {
      case 'batchEdit':
        handleBatchEdit();
        break;
      case 'batchDelete':
        handleBatchDelete();
        break;
      default:
        break;
    }
  }

  // 删除单条
  function handleDelete(row: any) {
    openModal({
      type: 'error',
      title: t('common.deleteConfirmTitle', { name: characterLimit(row.name || row.id) }),
      content: t('common.deleteConfirmContent'),
      positiveText: t('common.confirmDelete'),
      negativeText: t('common.cancel'),
      onPositiveClick: async () => {
        try {
          await deleteSubmission(row.id);
          Message.success(t('common.deleteSuccess'));
          tableRefreshId.value += 1;
        } catch (error) {
          console.error(error);
        }
      },
    });
  }

  // 编辑
  function handleEdit(id: string) {
    activeSubmissionId.value = id;
    formCreateDrawerVisible.value = true;
  }

  // 详情
  function handleDetail(id: string) {
    activeSubmissionId.value = id;
    detailDrawerVisible.value = true;
  }

  // 拖拽排序
  async function dragHandler(params: TableDraggedParams) {
    try {
      await dragSortSubmission(params);
      Message.success(t('common.operationSuccess'));
      tableRefreshIdKey.value += 1;
      tableRefreshId.value += 1;
    } catch (error) {
      console.error(error);
    }
  }

  function handleActionSelect(row: any, actionKey: string) {
    switch (actionKey) {
      case 'edit':
        handleEdit(row.id);
        break;
      case 'delete':
        handleDelete(row);
        break;
      case 'detail':
        handleDetail(row.id);
        break;
      default:
        break;
    }
  }

  const operationGroupList: ActionsItem[] = [
    {
      label: t('common.detail'),
      key: 'detail',
      permission: ['submission:read'],
    },
    {
      label: t('common.edit'),
      key: 'edit',
      permission: ['submission:update'],
    },
    {
      label: t('common.delete'),
      key: 'delete',
      permission: ['submission:delete'],
    },
  ];

  const { useTableRes, fieldList } = await useFormCreateTable({
    formKey: FormDesignKeyEnum.SUBMISSION,
    containerClass: '.crm-submission-table',
    operationColumn: {
      key: 'operation',
      width: 150,
      fixed: 'right',
      render: (row: any) =>
        h(CrmOperationButton, {
          groupList: operationGroupList,
          onSelect: (key: string) => handleActionSelect(row, key),
        }),
    },
    specialRender: {
      name: (row: any) => {
        return h(
          CrmTableButton,
          {
            onClick: () => handleDetail(row.id),
          },
          { default: () => row.name, trigger: () => row.name }
        );
      },
    },
    permission: ['submission:update', 'submission:delete'],
  });

  const { propsRes, propsEvent, loadList, setLoadListParams } = useTableRes;

  const crmTableRef = ref<InstanceType<typeof CrmTable>>();

  function searchData(val?: string) {
    setLoadListParams({ keyword: val ?? keyword.value });
    loadList();
    crmTableRef.value?.scrollTo({ top: 0 });
  }

  function handleRefresh() {
    checkedRowKeys.value = [];
    tableRefreshId.value += 1;
  }

  watch(
    () => tableRefreshId.value,
    () => {
      crmTableRef.value?.clearCheckedRowKeys();
      searchData();
    }
  );

  onMounted(() => {
    searchData();
  });
</script>

<style lang="less" scoped></style>
