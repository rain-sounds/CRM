<template>
  <CrmTable
    ref="crmTableRef"
    v-model:checked-row-keys="checkedRowKeys"
    v-bind="propsRes"
    class="crm-invoice-material-list-table"
    :not-show-table-filter="isAdvancedSearchMode"
    :action-config="actionConfig"
    @page-change="propsEvent.pageChange"
    @page-size-change="propsEvent.pageSizeChange"
    @sorter-change="propsEvent.sorterChange"
    @filter-change="propsEvent.filterChange"
    @batch-action="handleBatchAction"
    @refresh="searchData"
  >
    <template #actionLeft>
      <div class="flex items-center gap-[12px]">
        <n-button v-permission="['CONTRACT_INVOICE_MATERIAL:ADD']" type="primary" @click="handleNewClick">
          {{ t('contract.invoiceMaterial.add') }}
        </n-button>
      </div>
    </template>
    <template #actionRight>
      <CrmAdvanceFilter
        ref="tableAdvanceFilterRef"
        v-model:keyword="keyword"
        :filter-config-list="filterConfigList"
        @adv-search="handleAdvSearch"
        @keyword-search="searchData"
      />
    </template>
  </CrmTable>
  <invoiceMaterialDrawer
    v-model:visible="drawerVisible"
    :source-id="activeSourceId"
    @load="() => searchData()"
    @cancel="handleCancel"
  />
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import { DataTableRowKey, NButton, useMessage } from 'naive-ui';

  import { FieldTypeEnum } from '@lib/shared/enums/formDesignEnum';
  import { TableKeyEnum } from '@lib/shared/enums/tableEnum';
  import { useI18n } from '@lib/shared/hooks/useI18n';
  import { characterLimit } from '@lib/shared/method';
  import type { InvoiceMaterialItem } from '@lib/shared/models/contract';

  import CrmAdvanceFilter from '@/components/pure/crm-advance-filter/index.vue';
  import { FilterForm, FilterFormItem, FilterResult } from '@/components/pure/crm-advance-filter/type';
  import type { ActionsItem } from '@/components/pure/crm-more-action/type';
  import CrmNameTooltip from '@/components/pure/crm-name-tooltip/index.vue';
  import CrmTable from '@/components/pure/crm-table/index.vue';
  import { BatchActionConfig, CrmDataTableColumn } from '@/components/pure/crm-table/type';
  import useTable from '@/components/pure/crm-table/useTable';
  import CrmTableButton from '@/components/pure/crm-table-button/index.vue';
  import CrmOperationButton from '@/components/business/crm-operation-button/index.vue';
  import invoiceMaterialDrawer from './invoiceMaterialDrawer.vue';

  import { deleteInvoiceMaterial, getInvoiceMaterialList } from '@/api/modules';
  import { baseFilterConfigList } from '@/config/clue';
  import useModal from '@/hooks/useModal';

  const { t } = useI18n();
  const Message = useMessage();

  const keyword = ref('');
  const checkedRowKeys = ref<DataTableRowKey[]>([]);
  const activeSourceId = ref('');
  const { openModal } = useModal();
  const tableRefreshId = ref(0);

  const drawerVisible = ref(false);
  function handleNewClick() {
    activeSourceId.value = '';
    drawerVisible.value = true;
  }
  function handleEdit(id: string) {
    activeSourceId.value = id;
    drawerVisible.value = true;
  }

  async function deleteHandler(row: InvoiceMaterialItem) {
    openModal({
      type: 'error',
      title: t('common.deleteConfirmTitle', { name: characterLimit(row.hospitalName) }),
      content: t('contract.invoiceMaterial.deleteContent'),
      positiveText: t('common.confirmDelete'),
      negativeText: t('common.cancel'),
      onPositiveClick: async () => {
        try {
          await deleteInvoiceMaterial(row.id);
          Message.success(t('common.deleteSuccess'));
          tableRefreshId.value += 1;
        } catch (error) {
          // eslint-disable-next-line no-console
          console.error(error);
        }
      },
    });
  }

  function handleCancel() {
    activeSourceId.value = '';
  }

  function handleActionSelect(row: InvoiceMaterialItem, actionKey: string) {
    switch (actionKey) {
      case 'edit':
        handleEdit(row.id);
        break;
      case 'delete':
        deleteHandler(row);
        break;
      default:
        break;
    }
  }

  const columns: CrmDataTableColumn[] = [
    {
      type: 'selection',
      fixed: 'left',
      width: 46,
    },
    {
      fixed: 'left',
      title: t('contract.invoiceMaterial.sequence'),
      width: 70,
      key: 'sequence',
      sortOrder: false,
      sorter: true,
      columnSelectorDisabled: true,
    },
    {
      title: t('contract.invoiceMaterial.hospitalName'),
      key: 'hospitalName',
      sortOrder: false,
      sorter: true,
      width: 200,
      fixed: 'left',
      columnSelectorDisabled: true,
      render: (row: InvoiceMaterialItem) => {
        return h(CrmTableButton, {
          class: '!max-w-[calc(100%-24px)]',
          onClick: () => handleEdit(row.id),
        }, {
          default: () => row.hospitalName,
          trigger: () => row.hospitalName,
        });
      },
    },
    {
      title: t('contract.invoiceMaterial.invoice'),
      key: 'invoice',
      sortOrder: false,
      sorter: true,
      width: 100,
    },
    {
      title: t('contract.invoiceMaterial.verificationProof'),
      key: 'verificationProof',
      sortOrder: false,
      sorter: true,
      width: 100,
    },
    {
      title: t('contract.invoiceMaterial.sampleMailing'),
      key: 'sampleMailing',
      sortOrder: false,
      sorter: true,
      width: 100,
    },
    {
      title: t('contract.invoiceMaterial.samplePhoto'),
      key: 'samplePhoto',
      sortOrder: false,
      sorter: true,
      width: 100,
    },
    {
      title: t('contract.invoiceMaterial.report'),
      key: 'report',
      sortOrder: false,
      sorter: true,
      width: 100,
    },
    {
      title: t('contract.invoiceMaterial.outboundOrder'),
      key: 'outboundOrder',
      sortOrder: false,
      sorter: true,
      width: 100,
    },
    {
      title: t('contract.invoiceMaterial.contract'),
      key: 'contract',
      sortOrder: false,
      sorter: true,
      width: 100,
    },
    {
      title: t('contract.invoiceMaterial.platform'),
      key: 'platform',
      sortOrder: false,
      sorter: true,
      width: 120,
    },
    {
      title: t('contract.invoiceMaterial.otherMaterials'),
      key: 'otherMaterials',
      sortOrder: false,
      sorter: true,
      ellipsis: {
        tooltip: true,
      },
      width: 200,
    },
    {
      title: t('common.createTime'),
      key: 'createTime',
      width: 180,
      sortOrder: false,
      sorter: true,
      ellipsis: {
        tooltip: true,
      },
    },
    {
      title: t('common.creator'),
      key: 'createUser',
      sortOrder: false,
      sorter: true,
      width: 120,
      render: (row: InvoiceMaterialItem) => {
        return h(CrmNameTooltip, { text: row.createUserName });
      },
    },
    {
      title: t('common.updateTime'),
      key: 'updateTime',
      width: 180,
      ellipsis: {
        tooltip: true,
      },
      sortOrder: false,
      sorter: true,
    },
    {
      title: t('common.updateUserName'),
      key: 'updateUser',
      width: 120,
      sortOrder: false,
      sorter: true,
      render: (row: InvoiceMaterialItem) => {
        return h(CrmNameTooltip, { text: row.updateUserName });
      },
    },
    {
      key: 'operation',
      width: 100,
      fixed: 'right',
      render: (row: InvoiceMaterialItem) =>
        h(CrmOperationButton, {
          groupList: [
            { label: t('common.edit'), key: 'edit', permission: ['CONTRACT_INVOICE_MATERIAL:UPDATE'] },
            { label: t('common.delete'), key: 'delete', permission: ['CONTRACT_INVOICE_MATERIAL:DELETE'] },
          ],
          onSelect: (key: string) => handleActionSelect(row, key),
        }),
    },
  ];

  const actionConfig: BatchActionConfig = {
    baseAction: [],
  };

  function handleBatchAction(_item: ActionsItem) {
    // No batch actions for now
  }

  const { propsRes, propsEvent, tableQueryParams, loadList, setLoadListParams, setAdvanceFilter } = useTable(
    getInvoiceMaterialList,
    {
      tableKey: TableKeyEnum.CONTRACT_INVOICE_MATERIAL,
      showSetting: true,
      columns,
      containerClass: '.crm-invoice-material-list-table',
      permission: ['CONTRACT_INVOICE_MATERIAL:READ'],
    }
  );

  const crmTableRef = ref<InstanceType<typeof CrmTable>>();
  const isAdvancedSearchMode = ref(false);

  function handleAdvSearch(filter: FilterResult, isAdvancedMode: boolean, _originalForm?: FilterForm) {
    keyword.value = '';
    isAdvancedSearchMode.value = isAdvancedMode;
    setAdvanceFilter(filter);
    loadList();
    crmTableRef.value?.scrollTo({ top: 0 });
  }

  function searchData(val?: string) {
    setLoadListParams({ keyword: val ?? keyword.value });
    loadList();
    crmTableRef.value?.scrollTo({ top: 0 });
  }

  const filterConfigList = computed<FilterFormItem[]>(() => [
    {
      title: t('contract.invoiceMaterial.hospitalName'),
      dataIndex: 'hospitalName',
      type: FieldTypeEnum.INPUT,
    },
    {
      title: t('contract.invoiceMaterial.invoice'),
      dataIndex: 'invoice',
      type: FieldTypeEnum.SELECT,
    },
    {
      title: t('contract.invoiceMaterial.verificationProof'),
      dataIndex: 'verificationProof',
      type: FieldTypeEnum.SELECT,
    },
    {
      title: t('contract.invoiceMaterial.sampleMailing'),
      dataIndex: 'sampleMailing',
      type: FieldTypeEnum.SELECT,
    },
    {
      title: t('contract.invoiceMaterial.samplePhoto'),
      dataIndex: 'samplePhoto',
      type: FieldTypeEnum.SELECT,
    },
    {
      title: t('contract.invoiceMaterial.report'),
      dataIndex: 'report',
      type: FieldTypeEnum.SELECT,
    },
    {
      title: t('contract.invoiceMaterial.outboundOrder'),
      dataIndex: 'outboundOrder',
      type: FieldTypeEnum.SELECT,
    },
    {
      title: t('contract.invoiceMaterial.contract'),
      dataIndex: 'contract',
      type: FieldTypeEnum.SELECT,
    },
    {
      title: t('contract.invoiceMaterial.platform'),
      dataIndex: 'platform',
      type: FieldTypeEnum.SELECT,
    },
    ...baseFilterConfigList,
  ]);

  watch(
    () => tableRefreshId.value,
    () => {
      checkedRowKeys.value = [];
      searchData();
    }
  );

  onBeforeMount(() => {
    searchData();
  });
</script>

<style scoped lang="less"></style>
