<template>
  <InfoIcon :show-icon="false">
    <template #content>
      <!-- some info -->
    </template>
  </InfoIcon>

  <DataTable :value="measurementList">
    <Column v-for="col of columns" :key="col" :field="col" :header="$t('model.measurement.' + col)"></Column>
  </DataTable>
  <Button :label="$t('view.button.add')" @click="measurementAdd = true" />
  <Dialog
    v-model:visible="measurementAdd"
    :style="{ width: '75vw' }"
    :maximizable="true"
    :modal="true"
    :dismissable-mask="true"
  >
    <MeasurementForm @update:model-value="onCreate($event, 0)"></MeasurementForm>
  </Dialog>
</template>

<script>
import InfoIcon from "../../miscellaneous/InfoIcon.vue";
import MeasurementForm from "./MeasurementForm.vue";
export default {
  name: "MeasurementList",
  components: { InfoIcon, MeasurementForm },
  props: {},
  data() {
    return { measurementAdd: false, measurementList: [], columns: [] };
  },
  computed: {},
  watch: {},
  async created() {
    this.measurementList = await this.$ren.infrastructureApi.measurementList();
    if (this.measurementList != null && this.measurementList.length > 0) {
      this.columns = Object.keys(this.measurementList[0]);
    }
  },
  methods: {
    onCreate(o, i) {
      alert(o);
      alert(i);
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
