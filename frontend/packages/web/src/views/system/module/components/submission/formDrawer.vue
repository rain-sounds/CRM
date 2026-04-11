<template>
  <CrmFormDrawer v-model:visible="visible" :title="title" :form-key="FormDesignKeyEnum.SUBMISSION"> </CrmFormDrawer>
</template>

<script setup lang="ts">
  import { ref } from 'vue';

  import { FormDesignKeyEnum } from '@lib/shared/enums/formDesignEnum';
  import { useI18n } from '@lib/shared/hooks/useI18n';

  import CrmFormDrawer from '@/components/business/crm-form-design-drawer/index.vue';

  const { t } = useI18n();

  const visible = defineModel<boolean>('visible', {
    required: true,
  });

  const title = ref(t('module.submissionFormSetting'));
</script>

<style lang="less" scoped></style>
