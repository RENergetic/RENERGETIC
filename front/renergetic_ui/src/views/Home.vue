<template>
  <div>
    <DotMenu :model="menuModel" :fixed="true" />
    <Dialog
      v-model:visible="settingsDialog"
      :style="{ width: '50vw' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="true"
    >
      <HomeSettings @update="reloadSettings()"></HomeSettings>
    </Dialog>

    <div class="home-grid-stack grid-stack">
      <div v-if="settings.demandVisibility" :class="'grid-stack-item ren'" v-bind="panelTile">
        <DemandList :class="'grid-stack-item-content'" />
      </div>
      <div v-if="settings.feedbackVisibility" :class="'grid-stack-item ren'" v-bind="feedbackTile">
        <Card :class="'grid-stack-item-content'">
          <Feedback></Feedback>
        </Card>
      </div>

      <div v-if="settings.notificationVisibility" :class="'grid-stack-item ren'" v-bind="notificationTile">
        <Card :class="'grid-stack-item-content'">
          <NotificationList></NotificationList>
        </Card>
      </div>
      <div v-if="settings.selectedPanel" :class="'grid-stack-item ren'" v-bind="informationTile">
        <div :class="'grid-stack-item-content'">
          <InformationPanel
            v-if="loaded"
            ref="panel"
            :panel="settings.selectedPanel"
            :edit-mode="false"
          ></InformationPanel>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import DotMenu from "../components/miscellaneous/DotMenu.vue";
import HomeSettings from "../components/miscellaneous/settings/HomeSettings.vue";
import Feedback from "../components/user/Feedback.vue";
import NotificationList from "../components/dashboard/area/NotificationList.vue";
import InformationPanel from "../components/dashboard/InformationPanel.vue";
import DemandList from "../components/user/demand/DemandList.vue";

import { GridStack } from "gridstack";
// THEN to get HTML5 drag&drop
import "gridstack/dist/h5/gridstack-dd-native";
import "gridstack/dist/gridstack.min.css";

export default {
  name: "Home",
  components: {
    DotMenu,
    DemandList,
    HomeSettings,
    InformationPanel,
    NotificationList,
    Feedback,
  },
  data() {
    return {
      demand: { msg: "increase bla blah blah", icon: "battery", up: true, description: "description" },
      loaded: false,
      panelTile: {
        id: "panelTile",
        "gs-id": "panelTile",
        // "gs-x": 0,
        // "gs-y": 0,
        "gs-w": 4,
        "gs-h": 4,
      },
      notificationTile: {
        id: "notificationTile",
        "gs-id": "notificationTile",
        // "gs-x": this.layout.x,
        // "gs-y": this.layout.y,
        "gs-w": 4,
        "gs-h": 4,
      },
      feedbackTile: {
        id: "feedbackTile",
        "gs-id": "feedbackTile",
        // "gs-x": this.layout.x,
        // "gs-y": this.layout.y,
        "gs-w": 4,
        "gs-h": 4,
      },
      informationTile: {
        id: "informationTile",
        "gs-id": "informationTile",
        "gs-w": 12,
        "gs-h": 3,
      },
      grid: null,
      panel: null,
      locked: true,
      editTile: null,
      editDialog: false,
      notifiationDialog: false,
      manageSensorsDialog: false,
      settingsDialog: false,
      settings: this.$store.getters["settings/home"],
    };
  },
  computed: {
    toggleButton: function () {
      //TODO: if permission
      let label = this.locked ? this.$t("menu.grid_unlock") : this.$t("menu.grid_lock");
      return {
        label: label,
        icon: "pi pi-fw pi-lock",
        command: () => this.toggleLock(),
      };
    },
    saveButton: function () {
      return {
        label: this.$t("menu.save_grid"),
        icon: "pi pi-fw pi-plus-circle",
        command: () => this.saveGrid(),
      };
    },

    settingsButton: function () {
      //TODO: set icon
      return {
        label: this.$t("menu.settings"),
        icon: "pi pi-fw pi-plus-circle",
        command: () => (this.settingsDialog = !this.settingsDialog),
      };
    },

    menuModel() {
      let model = [];
      if (!this.locked) model.push(this.saveButton);
      model.push(this.toggleButton);
      model.push(this.settingsButton);
      return model;
    },
  },

  watch: {},
  async created() {
    // todo: get id from session storage
    this.loaded = false;
    let id = "1";
    console.info(this.$ren.dashboardApi);
    this.$ren.dashboardApi.getInformationPanel(id).then((panel) => {
      this.panel = panel;
    });
    //todo: catch
  },
  async mounted() {
    this.setGrid();
  },
  updated() {
    this.setGrid();
  },
  methods: {
    reloadSettings() {
      this.settings = this.$store.getters["settings/home"];
    },
    setGrid() {
      this.loaded = false;
      if (this.grid != null) this.grid.destroy(false);
      let grid = GridStack.init({ float: true }, ".home-grid-stack");
      if (this.locked) {
        grid.disable();
      } else {
        grid.enable();
      }
      this.grid = grid;
      window.homeGrid = this.grid;
      this.loaded = true;
    },
    gridWidth(tile) {
      return tile.col == null ? 2 : tile.col;
    },
    async toggleLock() {
      this.locked = !this.locked;
    },
    addTile() {
      //TODO: get unique id?
      this.panel.tiles.push({
        // layout: { x: 0, y: 0, h: 3, w: 3 },
        id: this.$ren.utils.uuid(),
        title: null,
        items: [],
      });
    },
    startEdit(tile) {
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
      this.notifiationDialog = true;
    },
  },
};
</script>

<style lang="scss">
.grid-stack-item {
  margin: 10px;
}
.grid-stack-item-content {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #3182ce;
  background-color: #bee3f8;
  font-weight: 600;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}
</style>
