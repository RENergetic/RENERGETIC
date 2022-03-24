<template>
  <InfoIcon :show-icon="false">
    <template #content>
      <!-- some info -->
    </template>
  </InfoIcon>
  <Card>
    <!-- <div class="card"> -->
    <template v-if="title" #title> {{ title }} </template>
    <template #content>
      <DataTable v-if="mMeasurements" :value="mMeasurements">
        <!-- <Column v-for="col of columns" :key="col" :field="col" :header="$t('model.asset.' + col)"></Column> -->
        <Column field="name" :header="$t('model.measurement.name')"> </Column>
        <Column field="label" :header="$t('model.measurement.label')"> </Column>
        <Column field="measurement_type" :header="$t('model.measurement.measurement_type')"> </Column>
      </DataTable>
      <span v-else>
        {{ $t("view.no_panel_measurements") }}
      </span>
      <Button :label="$t('view.button.add_measurement')" icon="pi pi-plus" @click="addMeasurement" />
      <!-- TODO: filter measurement select by id 
        :area-id="areaid" or tile id -->
      <measurement-select ref="measurementSelectDialog" @select="onMeasurementSelect"></measurement-select>
    </template>
  </Card>
  <!-- </div> -->
  <!-- <Button :label="$t('view.button.add_sensor')" @click="() => (sensorSearchDialog = !sensorSearchDialog)" /> -->

  <!-- <Dialog
    v-model:visible="sensorSearchDialog"
    :style="{ width: '50vw' }"
    :maximizable="true"
    :modal="true"
    :dismissable-mask="true"
  >
    {{ mMeasurements }}
    <MeasurementSelect @select="onMeasurementSelect" />
  </Dialog> -->
</template>

<script>
import InfoIcon from "@/components/miscellaneous/InfoIcon.vue";
import MeasurementSelect from "@/components/management/infrastructure/MeasurementSelect.vue";

export default {
  name: "ManageSensors",
  components: { InfoIcon, MeasurementSelect },
  props: {
    title: {
      type: String,
      default: null,
    },
    // name: {
    //   type: String,
    //   default: null,
    // },
    //area/tile id
    id: {
      type: String,
      default: null,
    },
    //list of measurements connected with area/tile
    modelValue: {
      type: Array,
      default: () => [],
    },
  },
  emits: ["update:modelValue"],
  data() {
    return {
      sensorSearchDialog: false,
      mMeasurements: this.modelValue,
    };
  },
  computed: {},
  watch: {},

  created() {},
  methods: {
    addMeasurement() {
      this.$refs.measurementSelectDialog.open();
    },
    onMeasurementSelect(measurement) {
      this.mMeasurements.push(measurement);
      this.$emit("update:modelValue", this.mMeasurements);
    },
    // async loadData() {
    //   await this.$ren.measurementApi.measurements(this.objects).then((data) => {
    //     this.data = data;
    //   });
    // },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
