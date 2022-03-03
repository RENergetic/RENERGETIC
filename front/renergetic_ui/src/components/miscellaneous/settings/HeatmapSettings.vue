<template>
  <!-- {{ model }} -->
  <Settings :schema="schema" :model="model"></Settings>
</template>

<script>
import Settings from "./Settings.vue";
export default {
  name: "HeatmapSettings",
  components: {
    Settings,
  },
  props: {},
  emits: ["update"],
  data() {
    return {
      model: this.$store.getters["settings/heatmap"],
      panels: [],
      schema: {},
    };
  },
  computed: {},
  watch: {
    model: {
      handler: function (newVal) {
        this.$store.commit("settings/heatmap", newVal);
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
          label: this.$t("settings.heatmap"),
          ext: {
            true: this.$t("settings.visible"),
            false: this.$t("settings.hidden"),
          },
          type: Boolean,
          key: "heatmapVisibility",
        },
        {
          label: this.$t("settings.chart"),
          ext: {
            true: this.$t("settings.visible"),
            false: this.$t("settings.hidden"),
          },
          type: Boolean,
          key: "chartVisibility",
        },
        {
          label: this.$t("settings.recommendations"),
          ext: {
            true: this.$t("settings.visible"),
            false: this.$t("settings.hidden"),
          },
          type: Boolean,
          key: "recommendationVisibility",
        },
        {
          label: this.$t("settings.measurements"),
          ext: {
            true: this.$t("settings.visible"),
            false: this.$t("settings.hidden"),
          },
          type: Boolean,
          key: "measurementsVisibility",
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
      ];

      return schema;
    },
    toggle(event) {
      this.$refs.menu.toggle(event);
    },
  },
};
</script>
<style scoped lang="scss"></style>
