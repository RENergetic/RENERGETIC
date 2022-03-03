<template>
  <Chart type="doughnut" :data="chartData" :options="options" />
</template>
<script>
import Chart from "primevue/chart";
export default {
  name: "DoughnutTile",
  components: { Chart },
  props: {
    pdata: { type: Object, default: () => ({}) },
    tile: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      options: {
        plugins: {
          legend: {
            labels: {
              color: "#495057",
            },
          },
        },
      },
    };
  },
  computed: {
    chartData: function () {
      let labels = this.tile.measurements.map((m) => m.label);

      let data = this.tile.measurements.map((m) => this.pdata[m.id]);
      console.info(this.tile.measurements);
      let backgroundColor = this.tile.measurements.map((m) =>
        m.measurement_details.color ? m.measurement_details.color : "#90A4AE",
      );

      return {
        labels: labels,
        //TODO: set colors
        datasets: [
          {
            data: data,
            backgroundColor: backgroundColor,
          },
        ],
      };
    },
  },

  mounted() {},
  methods: {},
};
</script>

<style lang="scss"></style>
