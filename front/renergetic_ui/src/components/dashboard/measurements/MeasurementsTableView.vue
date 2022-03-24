<template>
  <DataTable :value="objectsId">
    <Column field="name" :header="$t('model.measurements.label')">
      <template #body="item">
        {{ objects[item.data] }}
      </template>
    </Column>
    <Column v-for="k of ['m1', 'm2']" :key="k" :field="k" :header="$t('model.measurements' + k)">
      <template #body="item">
        {{ pData[item.data][k] }}
      </template>
    </Column>
  </DataTable>
</template>

<script>
export default {
  name: "MeasurementsTableView",
  components: {},
  props: {
    objects: {
      type: Object,
      default: () => ({}),
    },
  },

  data() {
    return { pData: {}, objectsId: [] };
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
      // todo:

      let pData = {};
      for (let k of this.objectsId) {
        pData[k] = { m1: 0.0, m2: 10.0 };
      }
      this.pData = pData;
      // await this.$ren.measurementApi.getCurrentMeasurements(Object.keys(this.objectsId)).then((measurements) => {
      //   this.measurements = measurements;
      // });
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
