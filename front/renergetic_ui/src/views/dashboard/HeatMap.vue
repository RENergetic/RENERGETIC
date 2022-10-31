<template>
  <div>
    <HeatMapView v-if="heatmap" :heatmap="heatmap" :heatmap-state="heatmapState"></HeatMapView>
  </div>
</template>
<script>
import HeatMapView from "../../components/dashboard/area/HeatMapView.vue";

//initial canvas size
// const sceneWidth = 900;
// const sceneHeight = 450;

export default {
  name: "HeatMap",
  components: { HeatMapView },
  data() {
    return { heatmap: null, heatmapState: null, state: false };
  },
  async created() {
    if (!this.state) {
      this.$router.push(`/`);
      return;
    }
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
  mounted() {},
  methods: {},
};
</script>

<style lang="scss"></style>
