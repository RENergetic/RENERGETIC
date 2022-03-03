<template>
  <div>
    <Dialog
      v-model:visible="heatMapDialog"
      :style="{ width: '75vw' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="true"
    >
      <Card>
        <template #title> {{ $t("view.heatmap_list") }} </template>
        <template #content>
          <Listbox v-if="heatMapList" v-model="selectedArea" :options="heatMapList" option-label="label">
            <template #option="slotProps">
              <!-- <div class="flex"> -->
              <div>{{ slotProps.option.label }}</div>
              <!-- <i
                  class="pi pi-chevron-circle-right"
                  style="fontsize: 2rem"
                  @click="$emit('select', slotProps.option)"
                /> -->
              <!-- </div> -->
            </template>
          </Listbox>
          <div v-if="selectedArea" class="field grid">
            <div class="col">
              <Button :label="$t('view.button.submit')" @click="submit" />
            </div>
            <div class="col">
              <Button :label="$t('view.button.cancel')" @click="cancel" />
            </div>
          </div>
        </template>
      </Card>
    </Dialog>
  </div>
</template>
<script>
import Listbox from "primevue/listbox";
// import { MapArea } from "../../plugins/model/Area";

import Card from "primevue/card";
export default {
  name: "HeatMapSelect",
  components: { Card, Listbox },
  props: { current: { type: Object, default: () => ({}) } },
  emits: { change: null },
  data() {
    return {
      heatMapList: [],
      selectedArea: null,
      heatMapDialog: false,
    };
  },
  async mounted() {},
  methods: {
    // onChange(option) {},
    submit() {
      if (this.selectedArea) {
        this.$emit("change", this.selectedArea);
      }
    },
    async open() {
      await this.$ren.dashboardApi.listHeatMap().then((heatMapList) => {
        this.heatMapDialog = true;
        if (this.current) {
          if (heatMapList.find((it) => it.id == this.current.id) == null) {
            this.heatMapList = [this.current] + heatMapList;
          }
          this.selectedArea = this.current;
        }
        this.heatMapList = heatMapList;
      });
    },
    cancel() {
      this.heatMapDialog = false;
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
