<template>
  <div id="panel-grid-stack" style="width: 100%; position: absolute; top: 0" class="grid-stack">
    <InformationTile
      v-for="tile in tiles"
      :key="tile.id"
      :tile="tile"
      :edit="editMode"
      :pdata="pdata"
      :settings="settings"
      @edit="onEdit"
      @notification="viewNotification"
    />
  </div>
  <Dialog
    v-model:visible="notificationDialog"
    :style="{ width: '50vw' }"
    :maximizable="true"
    :modal="true"
    :dismissable-mask="true"
  >
    <notification-view :notifications="[]" />
    <p>todo:</p>
  </Dialog>
  <Dialog
    v-model:visible="editDialog"
    :style="{ width: '50vw' }"
    :maximizable="true"
    :modal="true"
    :dismissable-mask="true"
  >
    {{ editTile }}
    <p>todo edit options, list of metricss</p>
    <Button
      :class="' p-button-text '"
      aria-haspopup="true"
      icon="pi pi-bell"
      @click="() => (manageSensorsDialog = !manageSensorsDialog)"
      >add sensor todo:</Button
    >
    <Dialog
      v-model:visible="manageSensorsDialog"
      :style="{ width: '75vw' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="true"
    >
      <ManageSensors></ManageSensors>
    </Dialog>
  </Dialog>
  <!-- <div class="grid-stack">
      <div
        v-for="tile in tiles"
        :key="tile.id"
        :class="'grid-stack-item'"
        :gs-w="gridWidth(tile)"
      >
        <div class="grid-stack-item-content">Item 2 wider</div>
      </div>
    </div> -->
  <!-- {{ tiles }} -->
  <!-- </div> -->
</template>
<script>
import InformationTile from "./informationtile/InformationTile.vue";
import ManageSensors from "./measurements/ManageSensors.vue";

import NotificationView from "../dashboard/area/NotificationList.vue";

import Dialog from "primevue/dialog";
import { GridStack } from "gridstack";
// THEN to get HTML5 drag&drop
import "gridstack/dist/h5/gridstack-dd-native";
import "gridstack/dist/gridstack.min.css";
export default {
  name: "InformationPanel",
  components: {
    InformationTile,
    ManageSensors,
    Dialog,
    NotificationView,
  },
  props: {
    locked: {
      type: Boolean,
      default: true,
    },
    panel: {
      type: Object,
      default: () => null,
    },
    settings: {
      type: Object,
      default: () => {
        return {};
      },
    },
    editMode: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      grid: null,
      notificationDialog: false,
      editDialog: false,
      editTile: null,
      mPanel: this.panel,
      pdata: {},
    };
  },
  computed: {
    tiles: function () {
      // return this.panel != null ? this.panel.tiles : [];
      return this.mPanel != null ? this.mPanel.tiles : [];
    },
    gridItems: function () {
      return this.grid != null ? this.grid.getGridItems() : [];
    },
  },
  watch: {
    panel: {
      handler: function (newValue) {
        this.mPanel = newValue;
        if (this.grid != null) this.grid.destroy(false);
        let grid = GridStack.init({ float: true }, "#panel-grid-stack");
        if (this.locked) {
          grid.disable();
        } else {
          grid.enable();
        }
        this.grid = grid;
        //TODO: remove this reference?
        window.panelGrid = this.grid;
      },
      deep: true,
    },
  },
  async mounted() {
    this.pdata = await this.$ren.dataApi.getPanelData(this.panel.id);
    console.info(JSON.stringify(this.pdata));
    if (this.grid != null) this.grid.destroy(false);
    let grid = GridStack.init({ float: true }, "#panel-grid-stack");
    if (this.locked) {
      grid.disable();
    } else {
      grid.enable();
    }
    this.grid = grid;
    //TODO: remove this reference?

    window.panelGrid = this.grid;
  },
  updated() {
    if (this.grid != null) this.grid.destroy(false);
    let grid = GridStack.init({ float: true }, "#panel-grid-stack");
    if (this.locked) {
      grid.disable();
    } else {
      grid.enable();
    }
    this.grid = grid;
    //TODO: remove this reference?
    window.panelGrid = this.grid;
  },
  methods: {
    gridWidth(tile) {
      return tile.col == null ? 2 : tile.col;
    },
    onEdit(tile) {
      //todo
      this.editTile = tile;
      this.editDialog = true;
      // console.info(tile);
    },

    saveGrid() {
      //TODO: save
      let nodes = this.gridItems;
      let tiles = this.tiles;
      nodes.forEach((node) => {
        let gridstackNode = node.gridstackNode;
        const tile = tiles.find((t) => t.id === gridstackNode.id);
        if (tile) {
          tile.layout = {
            x: gridstackNode.x,
            y: gridstackNode.y,
            w: gridstackNode.w,
            h: gridstackNode.h,
          };
        }
      });
      this.tiles = tiles;
    },
    viewNotification() {
      //TODO: load here notifications for tile
      this.notificationDialog = true;
    },
  },
};
</script>

<style lang="scss"></style>
