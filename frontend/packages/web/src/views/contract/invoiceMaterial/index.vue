<template>
  <div ref="invoiceMaterialCardRef" class="h-full">
    <CrmCard no-content-padding hide-footer>
      <div class="h-full px-[16px] pt-[16px]">
        <invoiceMaterialTable />
      </div>
    </CrmCard>
  </div>
</template>

<script setup lang="ts">
  import CrmCard from '@/components/pure/crm-card/index.vue';
  import invoiceMaterialTable from './components/invoiceMaterialTable.vue';
</script>

<style scoped></style>
