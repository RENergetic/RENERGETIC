<template>
  <Dialog
    v-model:visible="heatmapDialog"
    :style="{ width: '50vw' }"
    :maximizable="true"
    :modal="true"
    :dismissable-mask="false"
  >
    <HeatMapForm @save="onHeatmapInit" @cancel="onCancel"></HeatMapForm>
  </Dialog>
  <HeatMapEdit v-if="heatmap != null" v-model="heatmap" @update="onSave" />
</template>
<script>
import HeatMapEdit from "@/components/dashboard/area/HeatMapEdit.vue";
import HeatMapForm from "@/components/dashboard/area/HeatMapForm.vue";

import { HeatMap } from "@/plugins/model/HeatMap";

export default {
  name: "HeatMapCreator",
  components: { HeatMapEdit, HeatMapForm },
  props: {
    id: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      heatmap: null,
      heatmapDialog: false,
    };
  },
  created() {},
  mounted() {
    this.heatmap = new HeatMap();
    this.heatmapDialog = true;
  },
  methods: {
    async onSave() {
      //todo: validate:
      await this.$ren.dashboardApi.addHeatMap(this.heatmap).then(() => {
        // dashoard.id = id;
        // this.$store.commit("view/dashboardsAdd", dashoard);
        // TODO: router back?
        this.$router.push(this.$route.meta.from);
      });
    },
    onHeatmapInit(heatmap) {
      this.heatmap = heatmap;
      console.info(this.heatmap);
      this.heatmapDialog = false;
    },
    onCancel() {
      this.$router.push(this.$route.meta.from);
    },
  },
};
</script>

<style lang="scss"></style>
