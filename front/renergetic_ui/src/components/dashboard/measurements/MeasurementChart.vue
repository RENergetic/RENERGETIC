<template>
  <InfoIcon :show-icon="false">
    <template #content>
      <!-- some info -->
    </template>
  </InfoIcon>
  <!-- {{ data }} -->

  <Chart v-if="data" type="line" :data="data" :options="options" />
  <!-- :plugins="plugin"   "-->
  <div class="card">
    <div class="field grid">
      <!-- <div class="col">
        <Button :label="$t('menu.filter')" @click="setfilter" />
      </div> -->
      <div class="col">
        <Button :label="$t('view.button.export_csv')" @click="$ren.utils.exportChartData({ chartData: chart })" />
      </div>
    </div>
  </div>
  <!-- <FilterModal
    v-model:visible="filterDialogState"
    v-model:filter="currentFilter"
    :global="false"
    @close="onclose()"
  /> -->
  <!-- {{currentFilter}} -->
</template>

<script>
import Chart from "primevue/chart";
// import chartjsPluginAnnotation from "chartjs-plugin-annotation";
import chartjsMoment from "chartjs-adapter-moment";
import InfoIcon from "../../miscellaneous/InfoIcon.vue";

export default {
  name: "MasurementChart",
  components: { Chart, InfoIcon },
  props: {
    datasets: { type: Array, default: () => null }, //,default:
    labels: { type: Array, default: null },
    objects: { type: Array, default: null },
  },
  data() {
    return {
      // mDatasets: [],
      // mLabels: [],
      plugins: [chartjsMoment],
      data: null,
      // currentFilter: this.$store.getters.dateFilter,
    };
  },
  computed: {
    options: function () {
      // var _this = this;
      return {
        tooltips: {
          callbacks: {
            label: function (context, data) {
              let dataset = data.datasets[context.datasetIndex]; //#['data'][context.index]
              let value = dataset.data[context.index];
              return `${dataset.label}: ${value}`;
            },
          },
        },
        // annotation: {
        //   annotations: _this.annotations,
        // },
        // parsing: {
        //   xAxisKey: "timestamp",
        //   yAxisKey: "measurement_name",
        // },
        scales: {
          x: {
            type: "timeseries",
            // time: {
            //  format: timeFormat,
            // tooltipFormat: "YYYY-MM-DD HH:mm:ss",
            // displayFormats: {
            //   millisecond: "HH:mm:ss.SSS ",
            //   second: "HH:mm:ss ",
            //   minute: "HH:mm ",
            //   hour: "MM.DD HH:mm",
            //   day: "YYYY-MM-DD",
            //   week: "ll",
            //   month: "YYYY-MM",
            //   quarter: "YYYY-MM",
            //   year: "YYYY",
            // },
            // },
          },
        },
      };
    },
    chart: function () {
      return this.data;
    },
  },
  watch: {
    objects: {
      handler: function () {
        this.loadData();
      },
      deep: true,
    },
  },
  async created() {
    if (this.datasets != null) {
      if (this.xLabels == null) {
        this.data = { datasets: this.datasets };
      } else {
        this.data = {
          datasets: this.datasets,
          labels: this.labels,
        };
      }
    } else {
      if (this.objects) {
        await this.$ren.dataApi.getTimeseries(this.objects).then((data) => {
          // console.info(JSON.stringify(data));
          this.data = data;
        });
      }
    }
  },
  methods: {
    async loadData() {
      await this.$ren.dataApi.getTimeseries(this.objects).then((data) => {
        // console.info(JSON.stringify(data));
        this.data = data;
      });
    },
    // setfilter: function () {
    //   this.filterDialogState = true;
    // },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
