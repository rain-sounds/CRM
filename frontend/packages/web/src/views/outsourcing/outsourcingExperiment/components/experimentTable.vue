<template>
  <CrmTable
    ref="crmTableRef"
    v-model:checked-row-keys="checkedRowKeys"
    v-bind="propsRes"
    :fullscreen-target-ref="props.fullscreenTargetRef"
    :class="`crm-outsourcing-experiment-table-${props.formKey}`"
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
        <n-button v-permission="['OUTSOURCING:ADD']" :loading="createLoading" type="primary" @click="handleNewClick">
          {{ t('outsourcing.newOutsourcing') }}
        </n-button>
        <n-button
          v-permission="['OUTSOURCING:EXPORT']"
          type="primary"
          ghost
          class="n-btn-outline-primary"
          :disabled="propsRes.data.length === 0"
          @click="handleExportAllClick"
        >
          {{ t('common.exportAll') }}
        </n-button>
      </div>
    </template>
    <template #actionRight>
      <CrmAdvanceFilter
        ref="tableAdvanceFilterRef"
        v-model:keyword="keyword"
        :custom-fields-config-list="customFieldsFilterConfig"
        :filter-config-list="filterConfigList"
        @adv-search="handleAdvSearch"
        @keyword-search="searchData"
      />
    </template>
    <template #view>
      <CrmViewSelect
        v-model:active-tab="activeTab"
        :type="FormDesignKeyEnum.OUTSOURCING"
        :custom-fields-config-list="customFieldsFilterConfig"
        :filter-config-list="filterConfigList"
        :advanced-original-form="advancedOriginalForm"
        :route-name="OutsourcingRouteEnum.OUTSOURCING_EXPERIMENT"
        @refresh-table-data="searchData"
      />
    </template>
  </CrmTable>

  <CrmFormCreateDrawer
    v-model:visible="formCreateDrawerVisible"
    :form-key="activeFormKey"
    :source-id="activeSourceId"
    :need-init-detail="needInitDetail"
    @saved="handleFormCreateSaved"
  />
  <CrmTableExportModal
    v-model:show="showExportModal"
    :params="exportParams"
    :export-columns="exportColumns"
    :is-export-all="isExportAll"
    type="outsourcing"
    @create-success="handleExportCreateSuccess"
  />

  <DetailDrawer
    v-model:visible="showDetailDrawer"
    :sourceId="activeSourceId"
    @refresh="searchData(undefined, activeSourceId)"
    @delete="removeItemFromList(activeSourceId)"
  />
</template>

<script setup lang="ts">
  import { useRoute } from 'vue-router';
  import { DataTableRowKey, NButton, useMessage } from 'naive-ui';

  import { FormDesignKeyEnum } from '@lib/shared/enums/formDesignEnum';
  import { useI18n } from '@lib/shared/hooks/useI18n';
  import useLocale from '@lib/shared/locale/useLocale';
  import { ExportTableColumnItem } from '@lib/shared/models/common';

  import CrmAdvanceFilter from '@/components/pure/crm-advance-filter/index.vue';
  import { FilterForm, FilterFormItem, FilterResult } from '@/components/pure/crm-advance-filter/type';
  import type { ActionsItem } from '@/components/pure/crm-more-action/type';
  import CrmTable from '@/components/pure/crm-table/index.vue';
  import { BatchActionConfig } from '@/components/pure/crm-table/type';
  import CrmTableButton from '@/components/pure/crm-table-button/index.vue';
  import CrmFormCreateDrawer from '@/components/business/crm-form-create-drawer/index.vue';
  import CrmOperationButton from '@/components/business/crm-operation-button/index.vue';
  import CrmTableExportModal from '@/components/business/crm-table-export-modal/index.vue';
  import CrmViewSelect from '@/components/business/crm-view-select/index.vue';
  import DetailDrawer from './detail.vue';

  import { deleteOutsourcing } from '@/api/modules';
  import { baseFilterConfigList } from '@/config/clue';
  import useFormCreateTable from '@/hooks/useFormCreateTable';
  import useModal from '@/hooks/useModal';
  import { getExportColumns } from '@/utils/export';

  import { OutsourcingRouteEnum } from '@/enums/routeEnum';

  const route = useRoute();

  const { t } = useI18n();
  const Message = useMessage();
  const { currentLocale } = useLocale(Message.loading);
  const { openModal } = useModal();

  const props = defineProps<{
    fullscreenTargetRef?: HTMLElement | null;
    formKey: FormDesignKeyEnum.OUTSOURCING;
  }>();

  const activeTab = ref();
  const keyword = ref('');

  const checkedRowKeys = ref<DataTableRowKey[]>([]);

  const formCreateDrawerVisible = ref(false);
  const activeSourceId = ref('');
  const needInitDetail = ref(false);
  const activeFormKey = ref(FormDesignKeyEnum.OUTSOURCING);

  const createLoading = ref(false);

  async function handleNewClick() {
    try {
      createLoading.value = true;
      activeSourceId.value = '';
      needInitDetail.value = false;
      activeFormKey.value = FormDesignKeyEnum.OUTSOURCING;
      formCreateDrawerVisible.value = true;
    } catch (error) {
      // eslint-disable-next-line no-console
      console.error(error);
    } finally {
      createLoading.value = false;
    }
  }

  const showExportModal = ref<boolean>(false);
  const isExportAll = ref(false);

  function handleExportAllClick() {
    isExportAll.value = true;
    showExportModal.value = true;
  }
  function handleExportCreateSuccess() {
    checkedRowKeys.value = [];
  }

  const actionConfig: BatchActionConfig = {
    baseAction: [
      {
        label: t('common.exportChecked'),
        key: 'exportChecked',
        permission: ['OUTSOURCING:EXPORT'],
      },
    ],
  };

  function handleBatchAction(item: ActionsItem) {
    switch (item.key) {
      case 'exportChecked':
        isExportAll.value = false;
        showExportModal.value = true;
        break;
      default:
        break;
    }
  }

  const filterConfigList = computed<FilterFormItem[]>(() => [...baseFilterConfigList]);

  const operationGroupList = computed<ActionsItem[]>(() => {
    return [
      {
        label: t('common.edit'),
        key: 'edit',
        permission: ['OUTSOURCING:UPDATE'],
      },
      {
        label: t('common.delete'),
        key: 'delete',
        permission: ['OUTSOURCING:DELETE'],
      },
    ];
  });

  const tableRefreshId = ref(0);
  const tableRemoveRefreshId = ref('');
  const tableItemRefreshId = ref('');
  const showDetailDrawer = ref(false);

  function handleDelete(row: any) {
    openModal({
      type: 'error',
      title: t('system.personal.confirmDelete'),
      content: t('common.deleteConfirmContent'),
      positiveText: t('common.confirmDelete'),
      negativeText: t('common.cancel'),
      onPositiveClick: async () => {
        try {
          await deleteOutsourcing(row.id);
          Message.success(t('common.deleteSuccess'));
          tableRemoveRefreshId.value = row.id;
        } catch (error) {
          // eslint-disable-next-line no-console
          console.error(error);
        }
      },
    });
  }

  function handleEdit(id: string) {
    activeFormKey.value = FormDesignKeyEnum.OUTSOURCING;
    activeSourceId.value = id;
    needInitDetail.value = true;
    formCreateDrawerVisible.value = true;
  }

  function showDetail(id: string) {
    activeSourceId.value = id;
    showDetailDrawer.value = true;
  }

  async function handleActionSelect(row: any, actionKey: string) {
    switch (actionKey) {
      case 'edit':
        handleEdit(row.id);
        break;
      case 'delete':
        handleDelete(row);
        break;
      default:
        break;
    }
  }

  const { useTableRes, customFieldsFilterConfig } = await useFormCreateTable({
    formKey: props.formKey,
    operationColumn: {
      key: 'operation',
      width: currentLocale.value === 'en-US' ? 180 : 150,
      fixed: 'right',
      render: (row: any) =>
        h(CrmOperationButton, {
          groupList: operationGroupList.value,
          onSelect: (key: string) => handleActionSelect(row, key),
        }),
    },
    specialRender: {
      internalProjectNo: (row: any) => {
        return h(
          CrmTableButton,
          {
            onClick: () => {
              showDetail(row.id);
            },
          },
          { default: () => row.internalProjectNo, trigger: () => row.internalProjectNo }
        );
      },
    },
    permission: ['OUTSOURCING:EXPORT'],
    containerClass: `.crm-outsourcing-experiment-table-${props.formKey}`,
  });
  const { propsRes, propsEvent, tableQueryParams, loadList, setLoadListParams, setAdvanceFilter } = useTableRes;

  const exportColumns = computed<ExportTableColumnItem[]>(() =>
    getExportColumns(propsRes.value.columns, customFieldsFilterConfig.value as FilterFormItem[], [], true)
  );

  const exportParams = computed(() => {
    return {
      ...tableQueryParams.value,
      ids: checkedRowKeys.value,
    };
  });

  const crmTableRef = ref<InstanceType<typeof CrmTable>>();
  const tableAdvanceFilterRef = ref<InstanceType<typeof CrmAdvanceFilter>>();

  const isAdvancedSearchMode = ref(false);
  const advancedOriginalForm = ref<FilterForm | undefined>();
  function handleAdvSearch(filter: FilterResult, isAdvancedMode: boolean, originalForm?: FilterForm) {
    keyword.value = '';
    advancedOriginalForm.value = originalForm;
    isAdvancedSearchMode.value = isAdvancedMode;
    setAdvanceFilter(filter);
    loadList();
    crmTableRef.value?.scrollTo({ top: 0 });
  }

  function searchData(val?: string, refreshId?: string) {
    setLoadListParams({ keyword: val ?? keyword.value, viewId: activeTab.value });
    loadList(false, refreshId);
    if (!refreshId) {
      crmTableRef.value?.scrollTo({ top: 0 });
    }
  }

  watch(
    () => tableRefreshId.value,
    () => {
      checkedRowKeys.value = [];
      searchData();
    }
  );

  function handleFormCreateSaved(res: any) {
    if (needInitDetail.value) {
      searchData(undefined, res.id);
    } else {
      searchData();
    }
  }

  function removeItemFromList(id: string) {
    propsRes.value.data = propsRes.value.data.filter((item: any) => item.id !== id);
    propsRes.value.crmPagination = {
      ...propsRes.value.crmPagination,
      itemCount: (propsRes.value.crmPagination?.itemCount ?? 1) - 1,
    };
  }

  watch(
    () => tableRemoveRefreshId.value,
    (val) => {
      if (val) {
        removeItemFromList(val);
      }
    }
  );

  watch(
    () => tableItemRefreshId.value,
    (val) => {
      if (val) {
        searchData(undefined, val);
        tableItemRefreshId.value = '';
      }
    }
  );

  watch(
    () => activeTab.value,
    (val) => {
      if (val) {
        checkedRowKeys.value = [];
        setLoadListParams({ keyword: keyword.value, viewId: activeTab.value });
        crmTableRef.value?.setColumnSort(val);
      }
    }
  );

  onMounted(async () => {
    if (route.query.id) {
      activeSourceId.value = route.query.id as string;
      showDetailDrawer.value = true;
    }
  });
</script>
