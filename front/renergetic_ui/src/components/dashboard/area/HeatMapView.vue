<template>
  <DotMenu :model="menuModel()" />
  <div class="ren-toolbar">
    <i class="pi pi-image" @click="toggle('heatmapVisibility')" />
    <i class="pi pi-chart-line" @click="toggle('chartVisibility')" />
  </div>
  <div class="grid">
    <div class="col-9">
      <Card v-show="settings.heatmapVisibility">
        <template #title> {{ heatmap.label }} </template>
        <template #content>
          <div id="heatmapContainer">
            <heat-map-default
              v-if="heatmap.type == HeatMapType.DEFAULT"
              :heatmap="heatmap"
              :selected="selectedAreas"
              :heatmap-state="heatmapState"
              @update:selected="onAreasSelect"
              @click="onAreaClick"
            />

            <HeatMapOSM
              v-else-if="heatmap.type == HeatMapType.OSM"
              :heatmap="heatmap"
              :selected="selectedAreas"
              :heatmap-state="heatmapState"
              @update:selected="onAreasSelect"
              @click="onAreaClick"
            />
            <div v-else :heatmap="heatmap">UNKNOW_HEATMAP_TYPE</div>
          </div>
        </template>
      </Card>
      <MeasurementChart
        v-if="settings.chartVisibility && selectedAreas && Object.keys(selectedAreas).length > 0 && measurmenentState"
        :objects="Object.keys(selectedAreas)"
      ></MeasurementChart>
    </div>
    <div v-if="heatmap" class="col-3 ren">
      <Accordion class="tile" :active-index="selectedArea == null ? -1 : 0">
        <AccordionTab :disabled="selectedArea == null">
          <template #header> {{ $t("view.selected_area") }}</template>

          <AreaDetails class="tile" :model-value="selectedArea" @delete="areaDelete(selectedArea)"></AreaDetails>
        </AccordionTab>
      </Accordion>
      <Accordion class="tile" :active-index="0">
        <AccordionTab>
          <template #header> {{ $t("model.heatmap.areas") }}</template>
          <Listbox
            v-if="heatmap != null"
            v-model="selectedArea"
            :options="heatmap.areas"
            option-label="label"
            style="width: 15rem"
          />
        </AccordionTab>
      </Accordion>
      <Accordion v-if="attributes" class="tile" :active-index="0">
        <AccordionTab>
          <template #header> {{ $t("model.heatmap.attributes") }}</template>
          <Tree v-model:selection-keys="selectedAttributes" :value="attributes" selection-mode="checkbox"></Tree>
        </AccordionTab>
      </Accordion>
      <Accordion v-if="settings.recommendationVisibility && selectedAreas" class="tile" :active-index="-1">
        <AccordionTab>
          <template #header> {{ $t("model.heatmap.recommendations") }}</template>
          <recommendation-view :objects="selectedAreas"></recommendation-view>
        </AccordionTab>
      </Accordion>
      <Accordion v-if="settings.notificationVisibility && selectedAreas" class="tile" :active-index="0">
        <AccordionTab>
          <template #header> {{ $t("model.heatmap.notifications") }}</template>
          <notification-list :object-id="heatmap.id" :context="context"></notification-list>
        </AccordionTab>
      </Accordion>
      <Accordion v-if="settings.measurementsVisibility && selectedAreas" class="tile" :active-index="0">
        <AccordionTab>
          <template #header> {{ $t("view.measurements") }}</template>
          <measurements-view :objects="selectedAreas"></measurements-view>
        </AccordionTab>
      </Accordion>
    </div>
  </div>
  <Dialog
    v-model:visible="settingsDialog"
    :style="{ width: '50vw' }"
    :maximizable="true"
    :modal="true"
    :dismissable-mask="true"
  >
    <HeatMapSettings @update="reloadSettings()"></HeatMapSettings>
  </Dialog>
</template>
<script>
import DotMenu from "@/components/miscellaneous/DotMenu.vue";
import AreaDetails from "./AreaDetails.vue";
import RecommendationView from "@/components/management/RecommendationView.vue";
import NotificationList from "../../management/notification/NotificationList.vue";
import { NotificationContext, HeatMapType } from "../../../plugins/model/Enums";
import HeatMapSettings from "../../miscellaneous/settings/HeatmapSettings.vue";
import MeasurementChart from "../measurements/MeasurementChart.vue";
import MeasurementsView from "../measurements/MeasurementsView.vue";
import HeatMapDefault from "./HeatMapDefault.vue";
import HeatMapOSM from "./HeatMapOSM.vue";

export default {
  name: "HeatMapView",
  components: {
    HeatMapDefault,
    HeatMapOSM,
    AreaDetails,
    HeatMapSettings,
    RecommendationView,
    DotMenu,
    NotificationList,
    MeasurementChart,
    MeasurementsView,
  },
  props: {
    heatmap: {
      type: Object,
      default: () => null,
    },
    heatmapState: {
      type: Object,
      default: () => null,
    },
  },
  data() {
    return {
      HeatMapType: HeatMapType,
      selectedArea: null,
      bgImage: null,
      // areaState: [],
      selectedAreas: {},
      attributes: null,
      selectedAttributes: [],
      recommendations: null,
      recommendationPanelState: -1,
      measurmenentState: true,
      settingsDialog: false,
      settings: this.$store.getters["settings/heatmap"],
      context: NotificationContext.AREA,
    };
  },
  watch: {
    selectedAreas: {
      handler: async function (newValue) {
        if (newValue && Object.keys(newValue).length > 0) await this.loadAttributes();
        else {
          this.attributes = null;
        }
      },
      deep: true,
    },
    heatmap: function () {
      if (this.heatmap != null) {
        // this.areaState = Array(this.heatmap.areas.length).fill(false);
        // console.info(this.heatmap.areas.length);
      } else console.info("todo: null");
    },
  },

  mounted() {},
  methods: {
    reloadSettings() {
      this.settings = this.$store.getters["settings/heatmap"];
    },
    toggle(key) {
      this.$store.commit("settings/toggle", { section: "heatmap", key: key });
    },
    onAreasSelect(areas) {
      this.selectedAreas = areas;
    },

    async loadAttributes() {
      await this.$ren.dataApi.attributes(Object.keys(this.selectedAreas)).then((attributes) => {
        this.attributes = attributes;
      });
    },

    menuModel() {
      return [
        {
          label: this.$t("menu.settings"),
          icon: "pi pi-fw pi-eye",
          command: () => {
            this.settingsDialog = !this.settingsDialog;
          },
        },

        {
          label: this.$t("menu.edit"),
          icon: "pi pi-fw pi-pencil",
          command: () => {
            let to = `/dashboard/heatmap/edit/${this.heatmap.id}`;
            this.$router.push(to);
          },
        },
      ];
    },
  },
};
</script>

<style lang="scss">
#heatmap {
  max-width: 90%;
  max-height: 80vh;
}
#heatmapContainer {
  padding: 0.5rem;
}
</style>
