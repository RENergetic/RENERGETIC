<template>
  <div>
    <Dialog v-model:visible="measurementDialog" :maximizable="true" :modal="true" :dismissable-mask="true">
      <Card>
        <template #title> {{ $t("view.add_measurement") }} </template>
        <template #content>
          <AutoComplete
            v-model="selectedMeasurement"
            :placeholder="$t('view.find_measurement')"
            :suggestions="measurementList"
            field="label"
            @complete="searchMeasurement($event)"
          />
          <div v-if="selectedMeasurement" class="field grid">
            <div class="col">
              <Button :label="$t('view.button.submit')" @click="submit" />
            </div>
            <div class="col">
              <Button :label="$t('view.button.cancel')" @click="cancel" />
            </div>
          </div>
        </template>
      </Card>
    </Dialog>
  </div>
</template>
<script>
export default {
  name: "MeasurementSelect",
  components: {},
  props: { current: { type: Object, default: () => null }, assetId: { type: Number, default: null } },
  emits: ["select", "update:modelValue"],
  data() {
    return {
      measurementList: [],
      selectedMeasurement: null,
      measurementDialog: false,
    };
  },
  async mounted() {},
  methods: {
    // onChange(option) {},
    submit() {
      if (this.selectedMeasurement) {
        this.$emit("select", this.selectedMeasurement);
        this.$emit("update:modelValue", this.selectedMeasurement);
        this.measurementDialog = false;
      }
    },
    async searchMeasurement(event) {
      let q = event.query.trim();
      if (q.length > 0)
        await this.$ren.managementApi.searchMeasurement(q, this.assetId).then((measurementList) => {
          this.measurementList = measurementList;
        });
      else {
        await this.$ren.managementApi.listMeasurement().then((measurementList) => {
          this.measurementList = measurementList;
        });
      }
    },
    async open(current = null) {
      this.measurementDialog = true;
      if (current == null) this.selectedMeasurement = this.current;
      else this.selectedMeasurement = current;
    },
    cancel() {
      this.measurementDialog = false;
    },
  },
};
</script>

<style lang="scss">
i.pi {
  margin-left: 0.25rem;
}
.flex > div {
  flex-grow: 1;
}
</style>
