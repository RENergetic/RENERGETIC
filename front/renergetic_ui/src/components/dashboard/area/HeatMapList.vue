<template>
  <div class="grid">
    <!-- {{ heatMapList }} -->
    <div class="col-8">
      <Card>
        <template #title> {{ $t("view.heatmap_list") }} </template>
        <template #content>
          <DataTable :value="heatMapList">
            <Column v-for="h of headers" :key="h" :field="h" :header="$t('model.heatmap.' + h)"></Column>
            <Column field="link">
              <template #body="item">
                <i class="pi pi-chevron-circle-right" style="fontsize: 2rem" @click="view(item.data)" />
              </template>
            </Column>
          </DataTable>
          <!-- 
          <Listbox v-model="selectedArea" :options="heatMapList" option-label="label">
            <template #option="slotProps">
              <div class="flex">
                <div>{{ slotProps.option.label }}</div>
                <i class="pi pi-chevron-circle-right" style="fontsize: 2rem" @click="view(slotProps.option)" />
              </div>
            </template>
          </Listbox> -->
        </template>
      </Card>
    </div>
  </div>
</template>
<script>
// import Listbox from "primevue/listbox";
// import { MapArea } from "../../plugins/model/Area";

import Card from "primevue/card";
export default {
  name: "HeatMapList",
  components: { Card },
  data() {
    return {
      heatMapList: [],
      headers: [],
      selectedArea: null,
    };
  },
  async mounted() {
    await this.$ren.dashboardApi.listHeatMap().then((list) => {
      this.heatMapList = list;
      if (list.length > 0) {
        this.headers = Object.keys(list[0]).filter((it) => it != "areas");
      } else {
        this.headers = [];
      }
    });
  },
  methods: {
    view(selected) {
      let to = `/dashboard/heatmap/view/${selected.id}`;
      this.$router.push(to);
    },
  },
};
</script>

<style lang="scss">
i.pi {
  margin-left: 0.25rem;
}
.flex > div {
  flex-grow: 1;
}
</style>
