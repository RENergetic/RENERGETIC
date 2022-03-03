<template>
  <div>
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
        ref="panel"
        :locked="locked"
        :panel="panel"
        :edit-mode="editMode"
        :settings="settings"
      ></InformationPanel>
    </div>
  </div>
</template>
<script>
import InformationPanel from "../../components/dashboard/InformationPanel.vue";
import DotMenu from "../../components/miscellaneous/DotMenu.vue";
import PanelSettings from "../../components/miscellaneous/settings/PanelSettings.vue";
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
  async created() {
    this.$ren.dashboardApi.getInformationPanel(this.$route.params.id).then((panel) => {
      this.panel = panel;
    });
    //todo: catch
  },

  methods: {
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
        title: null,
        props: { items: [] },
      });
    },

    saveGrid() {
      this.$refs.panel.saveGrid();
    },
  },
};
</script>

<style lang="scss"></style>
