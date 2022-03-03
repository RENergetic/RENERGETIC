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
            <v-stage id="heatmap" ref="stage" :config="stageSize" @click="onClick">
              <v-layer v-show="bgImage != null">
                <v-image
                  :config="{
                    image: bgImage,
                  }"
                />
              </v-layer>
            </v-stage>
          </div>
        </template>
      </Card>
      <MeasurementChart
        v-if="settings.chartVisibility && selectedAreas && Object.keys(selectedAreas).length > 0 && measurmenentState"
        :objects="Object.keys(selectedAreas)"
      ></MeasurementChart>
    </div>
    <div v-if="bgImage" class="col-3 ren">
      <Accordion class="tile" :active-index="selectedArea == null ? -1 : 0">
        <AccordionTab :disabled="selectedArea == null">
          <template #header> {{ $t("view.selected_area") }}</template>

          <AreaDetails
            class="tile"
            :model-value="selectedArea"
            @update:model-value="onAreaSelect($event)"
            @delete="areaDelete(selectedArea)"
          ></AreaDetails>
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
          <notification-view :objects="selectedAreas"></notification-view>
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
import Tree from "primevue/tree";
import DotMenu from "../../miscellaneous/DotMenu.vue";
import AreaDetails from "./AreaDetails.vue";
import RecommendationView from "../../management/RecommendationView.vue";
import Listbox from "primevue/listbox";
import Accordion from "primevue/accordion";
import AccordionTab from "primevue/accordiontab";
import Card from "primevue/card";
import Konva from "konva";
import NotificationView from "./NotificationList.vue";
import { Colors } from "../../../plugins/model/Enums";
import HeatMapSettings from "../../miscellaneous/settings/HeatmapSettings.vue";
import MeasurementChart from "../measurements/MeasurementChart.vue";
import MeasurementsView from "../measurements/MeasurementsView.vue";
//todo: config
const TRANSPARENCY = "77";
const sceneWidth = 1200;
const sceneHeight = 600;
export default {
  name: "HeatMapView",
  components: {
    Card,
    Listbox,
    AreaDetails,
    Accordion,
    AccordionTab,
    Tree,
    HeatMapSettings,
    RecommendationView,
    DotMenu,
    NotificationView,
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
      stageSize: {
        width: sceneWidth,
        height: sceneHeight,
      },
      selectedArea: null,
      scale: 1,
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
        const image = new window.Image();
        image.src = this.heatmap.imgUrl;
        image.onload = () => {
          // set image only when it is loaded
          this.bgImage = image;
          let stage = this.$refs.stage.getStage();
          this.scaleHeatMap(stage, image);
          this.heatmap.areas.forEach((area, idx) => this.drawArea(area, idx));
        };
      } else console.info("todo: null");
    },
  },

  mounted() {
    if (this.heatmap != null) {
      const image = new window.Image();
      image.src = this.heatmap.imgUrl;
      image.onload = () => {
        // set image only when it is loaded
        this.bgImage = image;
        let stage = this.$refs.stage.getStage();
        this.scaleHeatMap(stage, image);

        this.heatmap.areas.forEach((it, idx) => this.drawArea(it, idx));
      };
      // this.areaState = Array(this.heatmap.areas.length).fill(false);
    }
  },
  methods: {
    reloadSettings() {
      this.settings = this.$store.getters["settings/heatmap"];
    },
    toggle(key) {
      this.$store.commit("settings/toggle", { section: "heatmap", key: key });
    },
    onAreaSelect(area) {
      if (this.selectedArea != null) {
        let oldId = this.selectedArea.id;
        this.toggleArea(oldId, false);
      }
      if (area != null) {
        Object.keys(this.selectedAreas).forEach((id) => {
          this.toggleArea(id, false);
        });
        let selectedAreas = {};
        selectedAreas[area.id] = 1;
        this.selectedAreas = selectedAreas;
        let newId = area.id;
        this.toggleArea(newId, true);
      }
    },
    getColor(id, selected = false) {
      let state = this.heatmapState != null ? this.heatmapState[id] : null;
      let transparency = TRANSPARENCY;
      if (selected) {
        transparency = "CC";
        // return Colors.SELECTED + transparency;
      }
      switch (Colors[state]) {
        case Colors.OK:
          return Colors.OK + transparency;
        case Colors.WARNING:
          return Colors.WARNING + transparency;
        case Colors.ERROR:
          return Colors.ERROR + transparency;
        default:
          return Colors.DEFAULT + transparency;
      }
    },
    toggleArea(id, selected) {
      let shape = this.$refs.stage.getStage().findOne(`#${id}`);
      if (shape != null) {
        shape.fill(this.getColor(id, selected));
      }
    },
    onClick(evt) {
      // console.info(evt.target.nodeName);
      if (evt.target.nodeName == "CANVAS" && this.addMode) {
        let stage = this.$refs.stage.getStage();
        var shape = stage.findOne(`#${this.current.id}`);
        if (shape != null) {
          this.current.roi.push([evt.layerX / this.scale, evt.layerY / this.scale]);
          let pnts = this.current.roi;
          let f = (context, shape) => {
            context.beginPath();
            pnts.forEach((pnt) => {
              context.lineTo(pnt[0], pnt[1]);
            });
            context.closePath();
            // special Konva.js method
            context.fillStrokeShape(shape);
          };
          shape.setSceneFunc(f);
        }
      }
    },

    drawArea(area, idx) {
      let layer = new Konva.Layer();
      let stage = this.$refs.stage.getStage();
      let item = new Konva.Shape(this.getConfig(area));
      let areaId = area.id;
      item.on("click", () => {
        this.selectedArea = this.heatmap.areas[idx];
        if (this.selectedAreas[areaId] == 1) {
          if (this.selectedAreas.length == 1) {
            this.selectedAreas = {};
          } else delete this.selectedAreas[areaId];
          this.toggleArea(area.id, false);
        } else {
          this.selectedAreas[areaId] = 1;
          this.toggleArea(area.id, true);
        }

        let keys = Object.keys(this.selectedAreas);
        if (keys.length != 1) {
          this.selectedArea = null;
        } else {
          let k = Object.keys(this.selectedAreas);
          this.heatmap.areas.forEach((area) => {
            if (area.id == k[0]) this.selectedArea = area;
          });
        }
      });
      layer.add(item);
      stage.add(layer);
      stage.draw();
    },

    getConfig(area) {
      return {
        sceneFunc: function (context, shape) {
          context.beginPath();
          console.info(area);
          area.roi.forEach((pnt) => {
            context.lineTo(pnt[0], pnt[1]);
          });
          context.closePath();
          // special Konva.js method
          context.fillStrokeShape(shape);
        },
        fill: this.getColor(area.id),
        stroke: "black",
        strokeWidth: 1,
        opacity: 0.75,
        listening: true,
        id: area.id,
      };
    },

    async loadAttributes() {
      await this.$ren.dataApi.attributes(Object.keys(this.selectedAreas)).then((attributes) => {
        this.attributes = attributes;
      });
    },
    scaleHeatMap(stage, bgImage) {
      if (bgImage != null) {
        var scale = sceneWidth / bgImage.width;
        this.scale = scale;
        stage.width(bgImage.width);
        stage.height(bgImage.height);
        stage.scale({ x: scale, y: scale });
      }
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
