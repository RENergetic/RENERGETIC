<template>
  <div>
    <!-- <DotMenu :model="menuModel" /> -->
    <DotMenu :model="menuModel" />
    <InformationPanelForm v-if="panel == null" v-model="panel" @save="onSave" />
    <InformationPanel
      v-if="panel != null"
      ref="panel"
      :locked="locked"
      :panel="panel"
      :edit-mode="true"
      :settings="settings"
      @update="onUpdate"
    ></InformationPanel>
    <!-- panel: {{ panel }} -->
  </div>
</template>
<script>
import InformationPanel from "@/components/dashboard/informationpanel/InformationPanel.vue";
import DotMenu from "@/components/miscellaneous/DotMenu.vue";
import InformationPanelForm from "@/components/dashboard/informationpanel/InformationPanelForm.vue";
// import DotMenu from "../../components/miscellaneous/DotMenu.vue";

export default {
  name: "InformationPanelCreator",
  components: {
    InformationPanel,
    DotMenu,
    InformationPanelForm,
    // InformationPanel,
    // DotMenu,
  },
  data() {
    return {
      initialized: false,
      panel: null,
      locked: false,
      editMode: false,
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
  watch: {
    // panel: function () {
    // },
  },

  methods: {
    addTile() {
      //TODO: get unique id?
      this.panel.tiles.push({
        // layout: { x: 0, y: 0, h: 3, w: 3 },
        id: this.$ren.utils.uuid(),
        title: null,
        type: null,
        measurements: [],
        props: { items: [] },
      });
    },
    async onSave() {
      this.panel.id = await this.$ren.dashboardApi.addInformationPanel(this.panel);
    },
    async onUpdate() {
      this.$ren.dashboardApi.updateInformationPanel(this.panel).then(() => this.$refs.panel.loadData());
    },
    saveGrid() {
      this.$refs.panel.saveGrid();
    },
  },
};
</script>

<style lang="scss"></style>
