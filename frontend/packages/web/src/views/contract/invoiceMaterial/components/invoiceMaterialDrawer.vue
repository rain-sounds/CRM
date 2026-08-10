<template>
  <CrmDrawer
    v-model:show="visible"
    width="800"
    :show-continue="!form.id"
    :title="form.id ? t('contract.invoiceMaterial.update') : t('contract.invoiceMaterial.add')"
    :ok-text="form.id ? t('common.update') : t('common.add')"
    :loading="loading"
    @confirm="handleConfirm(false)"
    @continue="handleConfirm(true)"
    @cancel="cancelHandler"
  >
    <n-scrollbar>
      <n-form ref="formRef" :model="form" label-placement="left" label-width="100">
        <n-form-item
          path="hospitalName"
          :label="t('contract.invoiceMaterial.hospitalName')"
          :rule="[
            {
              required: true,
              message: t('common.notNull', { value: t('contract.invoiceMaterial.hospitalName') }),
              trigger: ['input', 'blur'],
            },
          ]"
        >
          <n-input
            v-model:value="form.hospitalName"
            allow-clear
            :maxlength="255"
            :placeholder="t('common.pleaseInput')"
          />
        </n-form-item>
        <div class="grid grid-cols-2 gap-x-[16px]">
          <n-form-item :label="t('contract.invoiceMaterial.invoice')" path="invoice">
            <n-select
              v-model:value="form.invoice"
              :options="yesNoOptions"
              :placeholder="t('common.pleaseSelect')"
              clearable
            />
          </n-form-item>
          <n-form-item :label="t('contract.invoiceMaterial.verificationProof')" path="verificationProof">
            <n-select
              v-model:value="form.verificationProof"
              :options="yesNoOptions"
              :placeholder="t('common.pleaseSelect')"
              clearable
            />
          </n-form-item>
          <n-form-item :label="t('contract.invoiceMaterial.sampleMailing')" path="sampleMailing">
            <n-select
              v-model:value="form.sampleMailing"
              :options="yesNoOptions"
              :placeholder="t('common.pleaseSelect')"
              clearable
            />
          </n-form-item>
          <n-form-item :label="t('contract.invoiceMaterial.samplePhoto')" path="samplePhoto">
            <n-select
              v-model:value="form.samplePhoto"
              :options="yesNoOptions"
              :placeholder="t('common.pleaseSelect')"
              clearable
            />
          </n-form-item>
          <n-form-item :label="t('contract.invoiceMaterial.report')" path="report">
            <n-select
              v-model:value="form.report"
              :options="yesNoOptions"
              :placeholder="t('common.pleaseSelect')"
              clearable
            />
          </n-form-item>
          <n-form-item :label="t('contract.invoiceMaterial.outboundOrder')" path="outboundOrder">
            <n-select
              v-model:value="form.outboundOrder"
              :options="yesNoOptions"
              :placeholder="t('common.pleaseSelect')"
              clearable
            />
          </n-form-item>
          <n-form-item :label="t('contract.invoiceMaterial.contract')" path="contract">
            <n-select
              v-model:value="form.contract"
              :options="yesNoOptions"
              :placeholder="t('common.pleaseSelect')"
              clearable
            />
          </n-form-item>
          <n-form-item :label="t('contract.invoiceMaterial.platform')" path="platform">
            <n-select
              v-model:value="form.platform"
              :options="platformOptions"
              :placeholder="t('common.pleaseSelect')"
              clearable
            />
          </n-form-item>
        </div>
        <n-form-item :label="t('contract.invoiceMaterial.otherMaterials')" path="otherMaterials">
          <n-input
            v-model:value="form.otherMaterials"
            type="textarea"
            allow-clear
            :maxlength="500"
            :placeholder="t('common.pleaseInput')"
          />
        </n-form-item>
      </n-form>
    </n-scrollbar>
  </CrmDrawer>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import { FormInst, NForm, NFormItem, NInput, NScrollbar, NSelect, useMessage } from 'naive-ui';

  import { useI18n } from '@lib/shared/hooks/useI18n';
  import { SaveInvoiceMaterialParams } from '@lib/shared/models/contract';

  import CrmDrawer from '@/components/pure/crm-drawer/index.vue';

  import { addInvoiceMaterial, getInvoiceMaterialDetail, updateInvoiceMaterial } from '@/api/modules';

  import { initInvoiceMaterialForm, platformOptions, yesNoOptions } from '../config';

  const { t } = useI18n();
  const Message = useMessage();

  const props = defineProps<{
    sourceId: string;
  }>();

  const emit = defineEmits<{
    (e: 'load'): void;
    (e: 'cancel'): void;
  }>();

  const visible = defineModel<boolean>('visible', {
    required: true,
  });

  const form = ref<SaveInvoiceMaterialParams>({
    ...initInvoiceMaterialForm,
  });

  const formRef = ref<FormInst | null>(null);

  function cancelHandler() {
    form.value = { ...initInvoiceMaterialForm };
    emit('cancel');
    visible.value = false;
  }

  const loading = ref<boolean>(false);
  async function handleSave(isContinue: boolean) {
    try {
      loading.value = true;
      if (form.value.id) {
        await updateInvoiceMaterial(form.value);
        Message.success(t('common.updateSuccess'));
      } else {
        await addInvoiceMaterial(form.value);
        Message.success(t('common.addSuccess'));
      }
      if (isContinue) {
        form.value = { ...initInvoiceMaterialForm };
      } else {
        cancelHandler();
      }
      emit('load');
    } catch (e) {
      // eslint-disable-next-line no-console
      console.log(e);
    } finally {
      loading.value = false;
    }
  }

  function handleConfirm(isContinue: boolean) {
    formRef.value?.validate(async (error) => {
      if (!error) {
        handleSave(isContinue);
      }
    });
  }

  async function initDetail() {
    if (!props.sourceId) return;
    try {
      const result = await getInvoiceMaterialDetail(props.sourceId);
      form.value = { ...result };
    } catch (error) {
      // eslint-disable-next-line no-console
      console.log(error);
    }
  }

  watch(
    () => visible.value,
    (newVal) => {
      if (newVal) {
        initDetail();
      }
    }
  );
</script>

<style scoped></style>
