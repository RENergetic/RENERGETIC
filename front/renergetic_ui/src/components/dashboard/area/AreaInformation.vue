<template>
  <Card>
    <template v-if="measurements != null" #title>
      {{ $t("view.view_area") }}
    </template>
    <template v-else #title>
      {{ $t("view.no_measurements") }}
    </template>
    <template v-if="measurements != null" #content>
      <!-- {{ area.id }} -->
      <div v-for="m in measurements" :key="m.id" class="field grid">
        <label for="heatmapLabel">
          {{ $t("measurement." + m.name) }}
        </label>
        <div>{{ m.value }}</div>
      </div>
    </template>
  </Card>
</template>
<script>
//TODO: area edit modal
import Card from "primevue/card";
export default {
  name: "AreaInformation",
  components: { Card },
  props: {
    area: {
      //Area object
      type: Object,
      default: () => null,
    },
    measurements: {
      //Area object
      type: Array,
      default: () => null,
    },
  },
  data() {
    return {};
  },
  computed: {
    isAdmin: function () {
      // todo:
      return false;
    },
  },
  methods: {},
};
</script>

<style lang="scss"></style>
