<template>
  <div class="p-fluid">
    <div v-for="k in objectsId" :key="k" :class="'flex-row'">
      label: {{ objects[k] }}
      <div v-for="p in pData[k]" :key="p.key" :class="'flex'">
        <label :for="p.key" class="flex flex-grow-1">{{ $t("model.measurements" + p.key) }}</label>
        <div class="flex flex-none">
          <span :id="p.key">{{ p.value }}</span>
        </div>

        <!-- <span :id="p.key">{{ p.value }}</span> -->
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
        pData[k] = [
          { key: "m1", value: 0.0 },
          { key: "m2", value: 10.0 },
        ];
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
