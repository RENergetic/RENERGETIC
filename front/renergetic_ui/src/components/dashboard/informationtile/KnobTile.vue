<template>
  <Knob v-model="value" :min="0" :max="100.0" />
  <div style="text-align: center">
    <!-- <div key="" class="flex flex-grow-1">
      {{ $t("model.heatmap.heatmap") }}
    </div> -->

    <div>{{ measurement.label }}</div>
  </div>
</template>
<script>
import Knob from "primevue/knob";
export default {
  name: "InformationTile",
  components: { Knob },
  props: {
    pdata: { type: Object, default: () => ({}) },
    tile: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    let measurement = null;
    if (this.tile.measurements) {
      measurement = this.tile.measurements[0];
    }
    return {
      measurement: measurement,
    };
  },
  computed: {
    value: function () {
      if (this.measurement) {
        let id = this.measurement.id;
        return this.pdata ? this.pdata[id] : null;
      }
      return null;
    },
  },

  mounted() {},
  methods: {},
};
</script>

<style lang="scss"></style>
