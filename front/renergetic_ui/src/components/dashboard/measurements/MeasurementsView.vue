<template>
  <div class="p-fluid">
    TODO: measurements view
    {{ objects }} {{ measurements.length }}dddddd
    {{ measurements }}
    <div v-for="s in measurements" :key="s" :class="'field grid'">
      <label :for="s.key" class="col-12">{{ $t("model.measurements" + s.key) }}</label>

      <div class="col-12">
        <span :id="s.key">{{ s.value }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MeasurementsView",
  components: {},
  props: {
    objects: {
      type: Object,
      default: () => ({}),
    },
  },

  data() {
    return { measurements: [], objectsId: [] };
  },
  watch: {
    objects: {
      handler: function (newVal) {
        this.objectsId = Object.keys(newVal);
        this.refresh();
      },
      deep: true,
    },
  },
  created() {
    this.objectsId = Object.keys(this.objects);
    this.refresh();
  },
  methods: {
    async refresh() {
      await this.$ren.measurementApi.getCurrentMeasurements(Object.keys(this.objectsId)).then((measurements) => {
        this.measurements = measurements;
      });
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
