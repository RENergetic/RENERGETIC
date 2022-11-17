<template>
  <DotMenu :model="menuModel" />
  <div class="grid">
    <div class="col-11" style="margin: auto">
      <!-- {{ mHeatmap }} -->
      <Card>
        <template #title> HeatMap editor </template>
        <template #content>
          <heat-map-default-edit
            v-if="mHeatmap.type == HeatMapType.DEFAULT"
            :heatmap="mHeatmap"
            @update:heatmap="onHeatmapUpdate"
            @area-details="showAreaDetails"
            @click="onAreaClick"
          />

          <HeatMapOSMEdit
            v-else-if="mHeatmap.type == HeatMapType.OSM"
            :heatmap="mHeatmap"
            @area-details="showAreaDetails"
            @click="onAreaClick"
          />
        </template>
      </Card>
    </div>
    <Dialog
      v-model:visible="areaDetailsDialog"
      :style="{ width: '50vw' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="false"
    >
      <AreaDetails
        :edit="true"
        class="tile"
        :model-value="selectedArea"
        @update:model-value="selectedArea = $event"
        @delete="areaDelete(selectedArea)"
      ></AreaDetails>
    </Dialog>
    <div v-if="initialized && !addMode" class="col-3 ren"></div>
  </div>
</template>
<script>
import DotMenu from "../../miscellaneous/DotMenu.vue";
// import { Colors } from "@/plugins/model/Enums";
import { HeatMapType } from "../../../plugins/model/Enums";
import AreaDetails from "./AreaDetails.vue";
import HeatMapDefaultEdit from "./HeatMapDefaultEdit.vue";
import HeatMapOSMEdit from "./HeatMapOSMEdit.vue";

// const sceneWidth = 0.7 * window.innerWidth;
// const sceneHeight = (sceneWidth * 9) / 16;
export default {
  name: "HeatMapEdit",
  components: { AreaDetails, DotMenu, HeatMapDefaultEdit, HeatMapOSMEdit },
  props: {
    modelValue: {
      type: Object,
      required: true,
    },
  },
  emits: ["cancel", "update:modelValue", "update"],
  data() {
    return {
      //heat map area id
      // stageSize: {
      //   width: sceneWidth,
      //   height: sceneHeight,
      // },
      HeatMapType: HeatMapType,
      mHeatmap: this.modelValue,
      hasFiles: false,
      current: null,
      selectedArea: null,
      scale: 1,
      bgImage: null,
      addMode: false,
      areaDetailsDialog: false,
    };
  },
  computed: {
    menuModel: function () {
      return [
        {
          label: this.$t("menu.save"),
          icon: "pi pi-fw pi-eye",
          disabled: this.bgImage == null || this.mHeatmap == null,
          command: () => {
            this.$confirm.require({
              message: this.$t("view.dashboard_save_confirm", {
                label: this.label,
              }),
              header: this.$t("view.dashboard_save"),
              icon: "pi pi-exclamation-triangle",
              accept: () => {
                this.$emit("update", this.mHeatmap);
                this.$emit("update:modelValue", this.mHeatmap);
              },
              reject: () => {
                this.$confirm.close();
              },
            });
          },
        },
        {
          label: this.$t("menu.cancel"),
          icon: "pi pi-fw pi-eye",
          command: () => {
            this.$confirm.require({
              message: this.$t("view.dashboard_save_cancel_confirm", {
                label: this.label,
              }),
              header: this.$t("view.dashboard_save_cancel"),
              icon: "pi pi-exclamation-triangle",
              accept: () => {
                this.$emit("cancel");
              },
              reject: () => {
                this.$confirm.close();
              },
            });
          },
        },
      ];
    },
  },
  watch: {
    // mHeatmap: function () {
    // },
  },
  async created() {
    // if (this.mHeatmap != null && this.mHeatmap.imgUrl != null) {
    //   const image = new window.Image();
    //   image.src = this.mHeatmap.imgUrl;
    //   image.onload = () => {
    //     // set image only when it is loaded
    //     this.bgImage = image;
    //     let stage = this.$refs.stage.getStage();
    //     this.scaleHeatMap(stage, image);
    //     this.mHeatmap.areas.forEach((area, idx) => this.drawArea(area, idx));
    //   };
    // }
  },
  updated() {
    this.mHeatmap = this.modelValue;
  },
  methods: {
    onHeatmapUpdate(heatmap) {
      this.mHeatmap = heatmap;
    },
    showAreaDetails(evt) {
      this.selectedArea = evt.area;
      this.areaDetailsDialog = true;
    },
  },
};
</script>

<style lang="scss">
#heatmap {
  max-width: 75%;
  max-height: 75vh;
}
#heatmapContainer {
  padding: 0.5rem;
}
</style>
