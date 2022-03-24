<template>
  <Settings :schema="schema" :settings="settings"></Settings>
</template>

<script>
import Settings from "./Settings.vue";
export default {
  name: "HomeSettings",
  components: {
    Settings,
  },
  props: {},
  emits: ["update"],
  data() {
    return {
      settings: this.$store.getters["settings/home"],
      panels: [],
      schema: {},
    };
  },
  computed: {},
  watch: {
    settings: {
      handler: function (newVal) {
        this.$store.commit("settings/home", newVal);
        this.$emit("update");
      },
      deep: true,
    },
  },
  async mounted() {
    this.$ren.dashboardApi
      .listInformationPanel()
      .then((panels) => {
        this.panels = panels;
      })
      .then(() => {
        this.schema = this.getSchema();
      });
  },

  async created() {
    this.schema = this.getSchema();
  },
  methods: {
    getSchema() {
      var schema = [
        {
          label: this.$t("settings.feedback"),
          ext: {
            true: this.$t("settings.visible"),
            false: this.$t("settings.hidden"),
          },
          type: Boolean,
          key: "feedbackVisibility",
        },
        {
          label: this.$t("settings.notification"),
          ext: {
            true: this.$t("settings.visible"),
            false: this.$t("settings.hidden"),
          },
          type: Boolean,
          key: "notificationVisibility",
        },
        {
          label: this.$t("settings.request_demand"),
          ext: {
            true: this.$t("settings.visible"),
            false: this.$t("settings.hidden"),
          },
          type: Boolean,
          key: "demandVisibility",
        },
      ];
      //TODO: get panel list from store
      if (this.panels.length > 0) {
        schema.push({
          label: this.$t("settings.selected_panel"),
          ext: {
            options: this.panels,
            optionLabel: "label",
            optionValue: "id",
          },
          type: Array,
          key: "selectedPanel",
        });
      }
      return schema;
    },
    toggle(event) {
      this.$refs.menu.toggle(event);
    },
  },
};
</script>
<style scoped lang="scss"></style>
