<template>
  <HeatMapEdit v-if="heatmap != null" v-model="heatmap" @update="onSave" />
  <!-- todo: v else heatmap not found -->
</template>
<script>
import HeatMapEdit from "@/components/dashboard/area/HeatMapEdit.vue";

export default {
  name: "HeatMapEditView",
  components: { HeatMapEdit },
  props: {
    id: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      heatmap: null,
    };
  },

  async created() {
    this.$ren.dashboardApi
      .getHeatMap(this.$route.params.id)
      .then((heatmap) => {
        this.heatmap = heatmap;
        return this.$ren.dataApi.getHeatMapState(heatmap.id);
      })
      .then((state) => {
        console.info(JSON.stringify(state));
        this.heatmapState = state;
      })
      .catch((e) => {
        this.$toast.add({
          severity: "error",
          summary: this.$t("view.toast.get_heatmap", {
            label: this.label,
            error: e,
          }),
          life: 3000,
        });
      });
  },
  methods: {
    async onSave() {
      //todo: validate:
      await this.$ren.dashboardApi.updateHeatMap(this.heatmap).then(() => {
        // dashoard.id = id;
        // this.$store.commit("view/dashboardsAdd", dashoard);
        // TODO: router back?
        this.$router.push(this.$route.meta.from);
      });
    },
  },
};
</script>

<style lang="scss"></style>
