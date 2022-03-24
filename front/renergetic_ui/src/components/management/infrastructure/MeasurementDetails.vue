<template>
  <!-- {{ schema }} -->
  <Settings :schema="schema" :settings="mModel"></Settings>
</template>

<script>
import Settings from "@/components/miscellaneous/settings/Settings.vue";
var types = {
  color: "Color",
};
export default {
  name: "MeasurementDetails",
  components: {
    Settings,
  },
  props: { model: { type: Object, default: () => ({}) } },
  emits: ["update"],
  data() {
    return {
      mModel: this.model,
      keys: [],
      schema: {},
    };
  },
  computed: {},
  watch: {
    mModel: {
      handler: function (newVal) {
        this.$emit("update", newVal);
      },
      deep: true,
    },
  },
  async mounted() {
    this.keys = Object.keys(this.mModel);
    this.schema = this.getSchema();
  },

  methods: {
    getType(key) {
      if (types[key]) {
        return types[key];
      }
      return String;
    },
    getSetting(key) {
      let mt = this.getType(key);
      var ext = {};
      if (mt == Boolean) {
        ext = {
          true: this.$t("settings.visible"),
          false: this.$t("settings.hidden"),
        };
      }
      return {
        label: this.$t("settings.measurement_details." + key),
        ext: ext,
        type: this.getType(key),
        key: key,
      };
    },
    getSchema() {
      var schema = this.keys.map((k) => this.getSetting(k));
      return schema;
    },
  },
};
</script>
<style scoped lang="scss"></style>
