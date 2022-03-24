<template>
  <DotMenu :model="menuModel" />
  <div class="grid">
    <div class="col-9">
      <Card>
        <template #title> HeatMap editor </template>
        <template #content>
          <!-- file upload-->
          <!-- {{ areas }} {{ scale }} -->
          <div v-if="bgImage == null" class="field grid">
            <div class="col">
              <FileUpload
                ref="FileUpload"
                with-credentials="true,"
                name="images[]"
                :custom-upload="true"
                :multiple="false"
                accept="image/*"
                :max-file-size="21000000"
                :show-upload-button="false"
                :disabled="false"
                :cancel-label="$t('view.button.clear')"
                :choose-label="$t('view.button.choose')"
                @uploader="customUpload"
                @upload="onUpload"
                @select="onSelect"
              >
                <template #empty>
                  <p>
                    {{ $t("view.select_heatmap_image") }}
                  </p>
                </template>
              </FileUpload>
            </div>
          </div>
          <div v-show="bgImage != null" id="heatmapContainer">
            <v-stage id="heatmap" ref="stage" :config="stageSize" @click="onClick">
              <v-layer>
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
    </div>
    <div v-if="bgImage && !addMode" class="col-3 ren">
      <AreaDetails
        :edit="true"
        class="tile"
        :model-value="selectedArea"
        @update:model-value="selectedArea = $event"
        @delete="areaDelete(selectedArea)"
      ></AreaDetails>
      <Card class="tile">
        <template #title> {{ $t("model.heatmap.areas") }}</template>
        <template #content>
          <Listbox
            v-if="mHeatmap != null"
            v-model="selectedArea"
            :options="mHeatmap.areas"
            option-label="label"
            style="width: 15rem"
          />
        </template>
      </Card>
    </div>
  </div>

  <div class="card gf">
    <div class="field grid">
      <div v-if="!addMode && bgImage" class="col">
        <Button :label="$t('view.button.add_area')" @click="addPoint" />
      </div>
      <div v-if="!addMode && selectedArea" class="col">
        <Button :label="$t('view.button.edit_area')" @click="editPoint" />
      </div>
      <div v-if="addMode" class="col">
        <Button :label="$t('view.button.confirm_area')" @click="confirmPoint" />
        <!-- <Button v-if="selectedArea" :label="$t('view.button.add_area')" @click="confirmEditPoint" /> -->
      </div>
      <div v-if="addMode" class="col">
        <Button :label="$t('view.button.cancel')" @click="cancelPoint" />
      </div>
    </div>
  </div>
</template>
<script>
import DotMenu from "../../miscellaneous/DotMenu.vue";
import { Colors } from "@/plugins/model/Enums";
import AreaDetails from "./AreaDetails.vue";
import { MapArea } from "@/plugins/model/Area";

import Konva from "konva";
const TRANSPARENCY = "77";

const sceneWidth = 0.7 * window.innerWidth;
const sceneHeight = (sceneWidth * 9) / 16;
export default {
  name: "HeatMapEdit",
  components: { AreaDetails, DotMenu },
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
      stageSize: {
        width: sceneWidth,
        height: sceneHeight,
      },
      mHeatmap: this.modelValue,
      hasFiles: false,
      current: null,
      selectedArea: null,
      scale: 1,
      bgImage: null,
      addMode: false,
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
    selectedArea: function (newValue, oldValue) {
      //toggle colors
      if (oldValue != null) {
        let oldId = oldValue.id;
        let oldShape = this.$refs.stage.getStage().findOne(`#${oldId}`);
        if (oldShape != null) {
          //oldShape  -> after delete
          oldShape.fill(this.getColor(false));
        }
      }
      if (newValue != null) {
        let newId = newValue.id;
        this.$refs.stage.getStage().findOne(`#${newId}`).fill(this.getColor(true));
      }
    },
  },
  async created() {
    if (this.mHeatmap != null && this.mHeatmap.imgUrl != null) {
      const image = new window.Image();
      image.src = this.mHeatmap.imgUrl;
      image.onload = () => {
        // set image only when it is loaded
        this.bgImage = image;
        let stage = this.$refs.stage.getStage();
        this.scaleHeatMap(stage, image);
        this.mHeatmap.areas.forEach((area, idx) => this.drawArea(area, idx));
      };
    }
  },
  updated() {
    this.mHeatmap = this.modelValue;
  },
  methods: {
    getColor(selected = false) {
      let transparency = TRANSPARENCY;
      if (selected) {
        transparency = "CC";
      }
      return Colors.DEFAULT + transparency;
    },
    areaDelete(area) {
      this.mHeatmap.areas = this.mHeatmap.areas.filter((f) => f.id != area.id);
      this.deletePoint(area.id);
      if (this.selectedArea != null && this.selectedArea.id == area.id) {
        this.selectedArea = null;
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
    addPoint() {
      this.selectedArea = null;
      this.current = new MapArea(this.$ren.utils.uuid());
      this.current.roi = []; // new MapArea(`area_${nextId++}`);
      this.addMode = true;
      this.drawArea(this.current);
    },
    editPoint() {
      this.current = new MapArea(`area_temp`);
      this.current.roi = []; // new MapArea(`area_${nextId++}`);
      this.addMode = true;
      this.drawArea(this.current);
    },

    cancelPoint() {
      this.deletePoint(this.current.id);
      this.current = null;
      this.addMode = false;
    },

    deletePoint(id) {
      let layer = this.$refs.stage.getStage().findOne(`#${id}`);
      if (layer != null) layer.destroy();
      //else: TODO:
    },
    drawArea(area) {
      let layer = new Konva.Layer();
      let stage = this.$refs.stage.getStage();
      let item = new Konva.Shape(this.getConfig(area));
      layer.add(item);
      stage.add(layer);
      stage.draw();
    },
    confirmPoint() {
      //todo: validate
      if (this.selectedArea != null) {
        this.deletePoint(this.selectedArea.id);
        this.selectedArea.roi = this.current.roi;
        this.cancelPoint();
        this.drawArea(this.selectedArea);
      } else {
        this.mHeatmap.areas.push(this.current);
        this.current = null;
      }
      this.addMode = false;
    },
    getConfig(area) {
      return {
        sceneFunc: function (context, shape) {
          context.beginPath();
          area.roi.forEach((pnt) => {
            context.lineTo(pnt[0], pnt[1]);
          });
          context.closePath();
          // special Konva.js method
          context.fillStrokeShape(shape);
        },
        fill: this.getColor(false),
        stroke: "black",
        strokeWidth: 1,
        opacity: 0.75,
        listening: true,
        id: area.id,
      };
    },
    onSelect() {
      if (this.$refs.FileUpload !== undefined) this.hasFiles = this.$refs.FileUpload.files.length > 0;
      else this.hasFiles = false;
      if (this.hasFiles) {
        const image = new window.Image();
        image.src = this.$refs.FileUpload.files[0].objectURL;
        image.onload = () => {
          // set image only when it is loaded
          let stage = this.$refs.stage.getStage();
          this.bgImage = image;
          this.scaleHeatMap(stage, image);
          this.mHeatmap.imgUrl = image.src;
        };
      }
    },
    scaleHeatMap(stage, bgImage) {
      //TODO: validate image ratio
      if (bgImage != null) {
        var scale = sceneWidth / bgImage.width;
        this.scale = scale;
        // console.info(scale);
        // console.info(bgImage);
        stage.width(bgImage.width * scale);
        stage.height(bgImage.height * scale);
        stage.scale({ x: scale, y: scale });
      }
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
