<template>
  <div id="panel-grid-stack" style="width: 100%; position: absolute; top: 0" class="grid-stack">
    <InformationTile
      v-for="(tile, index) in tiles"
      :key="tile.id"
      :slot-props="{ tile: tile, index: index }"
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
    <notification-list v-if="selectedItem" :context="notificationContext" :object-id="selectedItem.tile.id" />
  </Dialog>
  <Dialog
    v-model:visible="editDialog"
    :style="{ width: '50vw' }"
    :maximizable="true"
    :modal="true"
    :dismissable-mask="true"
  >
    <!-- {{ selectedTile }} -->
    <div class="field grid">
      <label for="assetType" class="col-fixed" style="width: 5rem">
        {{ $t("model.information_tile.type") }}
      </label>
      <div class="col">
        <Dropdown
          id="assetType"
          v-model="selectedItem.tile.type"
          :options="tileTypes"
          option-label="label"
          option-value="value"
          :placeholder="$t('view.select_tile_type')"
        />
      </div>
    </div>
    <div class="field grid">
      <Button
        :label="$t('view.button.manage_measurement')"
        icon="pi pi-plus"
        @click="() => (manageSensorsDialog = !manageSensorsDialog)"
      />
    </div>
    <div class="field grid">
      <Button :label="$t('view.button.submit')" icon="pi pi-plus" @click="saveGrid" />
    </div>
    <Dialog
      v-model:visible="manageSensorsDialog"
      :style="{ width: '75vw' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="true"
    >
      <!-- {{ selectedTile.tile.measurements }} -->
      <ManageSensors v-model="selectedItem.tile.measurements"></ManageSensors>
    </Dialog>
  </Dialog>
</template>
<script>
import InformationTile from "./informationtile/InformationTile.vue";
import ManageSensors from "./measurements/ManageSensors.vue";

import NotificationList from "../management/notification/NotificationList.vue";

import Dialog from "primevue/dialog";
import { GridStack } from "gridstack";
import { TileTypes, NotificationContext } from "@/plugins/model/Enums.js";
// THEN to get HTML5 drag&drop
import "gridstack/dist/h5/gridstack-dd-native";
import "gridstack/dist/gridstack.min.css";
export default {
  name: "InformationPanel",
  components: {
    InformationTile,

    ManageSensors,
    Dialog,
    NotificationList,
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
  emits: ["update"],
  data() {
    return {
      grid: null,
      notificationDialog: false,
      editDialog: false,
      selectedItem: null,
      mPanel: this.panel,
      manageSensorsDialog: false,
      pdata: {},
      tileTypes: Object.entries(TileTypes).map((k) => {
        return { value: k[1], label: this.$t("enums.tile_type." + k[1]) };
      }),
      notificationContext: NotificationContext.TILE,
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
    manageSensorsDialog: function (newValue) {
      if (!newValue) {
        this.mPanel.tiles[this.selectedItem.index] = this.selectedItem.tile;
      }
    },
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
    await this.loadData();
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
    async loadData() {
      if (this.panel.id != null) this.pdata = await this.$ren.dataApi.getPanelData(this.panel.id);
    },
    gridWidth(tile) {
      return tile.col == null ? 2 : tile.col;
    },
    onEdit(evt) {
      //todo
      this.selectedItem = evt;
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
      this.mPanel.tiles = tiles;
      this.$emit("update", this.mPanel);
    },

    viewNotification(evt) {
      //TODO: load here notifications for tile
      this.selectedItem = evt;
      this.notificationDialog = true;
    },
  },
};
</script>

<style lang="scss"></style>
