<template>
  <div id="panel-box">
    <div v-if="panel">
      <DotMenu :model="menuModel" />
      <Dialog
        v-model:visible="settingsDialog"
        :style="{ width: '50vw' }"
        :maximizable="true"
        :modal="true"
        :dismissable-mask="true"
      >
        <PanelSettings @update="reloadSettings()"></PanelSettings>
      </Dialog>
      <InformationPanel
        v-if="panel"
        ref="panel"
        :asset-id="$route.params.asset_id"
        :locked="locked"
        :panel="panel"
        :edit-mode="editMode"
        :settings="settings"
      ></InformationPanel>
    </div>
  </div>
</template>
<script>
import InformationPanel from "@/components/dashboard/informationpanel/InformationPanel.vue";
import DotMenu from "@/components/miscellaneous/DotMenu.vue";
import PanelSettings from "@/components/miscellaneous/settings/PanelSettings.vue";
export default {
  name: "InformationPanelView",
  components: {
    InformationPanel,
    DotMenu,
    PanelSettings,
  },
  data() {
    return {
      panel: null,
      locked: false,
      editMode: false,
      settings: this.$store.getters["settings/panel"],
      settingsDialog: false,
    };
  },
  computed: {
    toggleButton: function () {
      let label = this.locked ? this.$t("menu.panel_grid_unlock") : this.$t("menu.panel_grid_lock");
      return {
        label: label,
        icon: "pi pi-fw pi-lock",
        command: () => this.toggleLock(),
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
    editModelButton: function () {
      let label = this.editMode ? this.$t("menu.panel_grid_edit_on") : this.$t("menu.panel_grid_edit_off");
      return {
        label: label,
        icon: "pi pi-fw pi-lock",
        command: () => this.toggleEditMode(),
      };
    },
    addButton: function () {
      return {
        label: this.$t("menu.panel_grid_add_tile"),
        icon: "pi pi-fw pi-plus",
        command: () => this.addTile(),
      };
    },
    menuModel() {
      let menu = [
        {
          label: this.$t("menu.save_panel_grid"),
          icon: "pi pi-fw pi-save",
          command: () => this.saveGrid(),
        },
      ]; //TODO: if permission
      //todo: add to menu model

      menu.push(this.toggleButton);
      menu.push(this.addButton);
      if (this.locked) menu.push(this.editModelButton);
      menu.push(this.settingsButton);
      return menu;
    },
  },
  watch: {},
  async mounted() {
    await this.loadStructure();
  },
  async updated() {
    await this.loadStructure();
  },

  methods: {
    localPanel(id, assetId) {
      console.error("asset panels templates not supported: " + assetId);
      // if (assetId != null) {
      //   let index = this.$store.getters("view/assetPanelsMap")[id + "_" + assetId];
      //   if (index != null) {
      //     return   let index = this.$store.getters("view/assetPanels")[index];
      //   }
      //   return null;
      // }
      // console.info(this.$store.getters["view/informationPanelsMap"][id]);
      let index = this.$store.getters["view/informationPanelsMap"][id];
      if (index != null) {
        return this.$store.getters["view/informationPanels"][index];
      }
      return null;
    },
    async loadStructure() {
      let informationPanel = this.localPanel(this.$route.params.id, this.$route.params.asset_id);
      // console.info(informationPanel);
      if (informationPanel == null) {
        this.$ren.dashboardApi.getInformationPanel(this.$route.params.id, this.$route.params.asset_id).then((panel) => {
          this.panel = panel;
        });
      } else {
        this.panel = informationPanel;
      }
    },
    reloadSettings() {
      this.settings = this.$store.getters["settings/panel"];
    },
    async toggleLock() {
      this.locked = !this.locked;
    },
    async toggleEditMode() {
      this.editMode = !this.editMode;
    },
    addTile() {
      //TODO: get unique id?
      this.panel.tiles.push({
        // layout: { x: 0, y: 0, h: 3, w: 3 },
        id: this.$ren.utils.uuid(),
        label: null,
        props: { items: [] },
      });
    },

    saveGrid() {
      this.$refs.panel.saveGrid();
    },
  },
};
</script>

<style lang="scss">
#panel-box {
  background: #232526; /* fallback for old browsers */
  background: -webkit-linear-gradient(to right, #232526, #414345); /* Chrome 10-25, Safari 5.1-6 */
  background: linear-gradient(
    to right,
    #232526,
    #414345
  ); /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera 12+, Safari 7+ */
}
</style>
