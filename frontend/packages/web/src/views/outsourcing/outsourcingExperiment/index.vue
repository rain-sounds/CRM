<template>
  <div ref="outsourcingExperimentCardRef" class="h-full">
    <CrmCard no-content-padding hide-footer>
      <div class="h-full px-[16px] pt-[16px]">
        <ExperimentTable
          :form-key="FormDesignKeyEnum.OUTSOURCING"
          :fullscreen-target-ref="outsourcingExperimentCardRef"
        />
      </div>
    </CrmCard>
  </div>
</template>

<script setup lang="ts">
  import { FormDesignKeyEnum } from '@lib/shared/enums/formDesignEnum';

  import CrmCard from '@/components/pure/crm-card/index.vue';
  import ExperimentTable from './components/experimentTable.vue';

  const outsourcingExperimentCardRef = ref<HTMLElement | null>(null);
</script>
