<template>
  <div class="grid">
    <div class="col-8">
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
                    {{ $t("form.heat_map_image") }}
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
    <div v-if="bgImage" class="col-3 ren">
      <AreaDetails
        class="tile"
        :model-value="selectedArea"
        @update:model-value="selectedArea = $event"
        @delete="areaDelete(selectedArea)"
      ></AreaDetails>
      <Card class="tile">
        <template #title> {{ $t("heatmap.areas") }}</template>
        <template #content>
          <Listbox
            v-if="heatmap != null"
            v-model="selectedArea"
            :options="heatmap.areas"
            option-label="label"
            style="width: 15rem"
          />
        </template>
      </Card>
    </div>
  </div>

  <div class="card gf">
    <div class="p-fluid p-formgrid grid ren-submit">
      <div v-if="!addMode && bgImage" class="field md-3 sm-6">
        <Button :label="$t('view.button.add_point')" @click="addPoint" />
      </div>
      <div v-if="addMode" class="field md-3 sm-6">
        <Button :label="$t('view.button.confirm_point')" @click="confirmPoint" />
      </div>
      <div v-if="addMode" class="field md-3 sm-6">
        <Button :label="$t('view.button.cancel')" @click="cancelPoint" />
      </div>
    </div>
    <div class="field grid ren-submit">
      <div class="col">
        <Button :disabled="!hasFiles" :label="$t('view.button.submit')" @click="submit" />
      </div>
      <div class="col">
        <Button :label="$t('view.button.cancel')" @click="cancel" />
      </div>
    </div>

    <ProgressSpinner v-if="spinner" style="width: 10rem; height: 10rem; margin: auto; display: flex" stroke-width="4" />
  </div>
</template>
<script>
import FileUpload from "primevue/fileupload";
import AreaDetails from "../../components/dashboard/area/AreaDetails.vue";
import Listbox from "primevue/listbox";
import { MapArea } from "../../plugins/model/Area";
import { HeatMap } from "../../plugins/model/HeatMap";

import Card from "primevue/card";
import Konva from "konva";

//TODO: update image,todo: spinner?
//initial canvas size
const sceneWidth = 900;
const sceneHeight = 450;
var nextId = 0;
export default {
  name: "HeatMapCreator",
  components: { FileUpload, Card, Listbox, AreaDetails },
  props: {
    id: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      //heat map area id
      stageSize: {
        width: sceneWidth,
        height: sceneHeight,
      },
      heatmap: null,
      hasFiles: false,
      current: null,
      selectedArea: null,
      scale: 1,
      bgImage: null,
      addMode: false,
    };
  },
  watch: {
    selectedArea: function (newValue, oldValue) {
      //toggle colors
      if (oldValue != null) {
        let oldId = oldValue.id;
        let oldShape = this.$refs.stage.getStage().findOne(`#${oldId}`);
        if (oldShape != null) {
          //oldShape  -> after delete
          oldShape.fill("#00D2FFAA");
        }
      }
      if (newValue != null) {
        let newId = newValue.id;
        this.$refs.stage.getStage().findOne(`#${newId}`).fill("#AAAAFFAA");
      }
    },
  },
  created() {
    if (this.id == null) {
      //create new heatmap
      this.heatmap = new HeatMap(`heatmap_${nextId++}`); //TODO: -> temporary id
    } else {
      console.info("todo:");
      //todo: load from API
    }
  },
  mounted() {},
  methods: {
    areaDelete(area) {
      this.heatmap.areas = this.heatmap.areas.filter((f) => f.id != area.id);
      this.deletePoint(area.id);
      if (this.current != null && this.current.id == area.id) {
        this.current = null;
      }
    },
    onClick(evt) {
      // console.info(evt.target.nodeName);
      if (evt.target.nodeName == "CANVAS" && this.addMode) {
        // console.info([evt.layerX / this.scale, evt.layerY / this.scale]);
        // console.info(evt);var evt = e.evt;
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
        // stage.draw();
        // this.deletePoint(this.current.id);
        // this.drawArea(this.current);
      }
    },
    addPoint() {
      this.current = new MapArea(`area_${nextId++}`);
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
      // let area = new MapArea(`area_${nextId++}`, this.current.roi);
      //todo: validate
      this.heatmap.areas.push(this.current);
      this.current = null;
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
        fill: "#00D2FFAA",
        stroke: "black",
        strokeWidth: 1,
        opacity: 0.75,
        listening: true,
        id: area.id,
      };
    },
    cancel() {
      // TOdo: go back
      if (this.$refs.FileUpload != null) this.$refs.FileUpload.clear();
      // this.bgImage = null;
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
          this.heatmap.imgUrl = image.src;
        };
      }
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
    async submit() {
      //todo: validate:
      await this.$ren.dashboardApi.addHeatMap(this.heatmap).then(() => {
        // dashoard.id = id;
        // this.$store.commit("view/dashboardsAdd", dashoard);
        // TODO: router back?
        this.$router.push(this.$route.meta.from);
      });
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
