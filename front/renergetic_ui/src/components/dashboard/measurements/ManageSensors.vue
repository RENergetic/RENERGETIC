<template>
  <InfoIcon :show-icon="false">
    <template #content>
      <!-- some info -->
    </template>
  </InfoIcon>
  <div class="card">
    TODO: list of current metrics senors etcc
    <div class="field grid">todo: manage sensors sensor</div>
    <Button :label="$t('view.button.add_sensor')" @click="() => (sensorSearchDialog = !sensorSearchDialog)" />
  </div>

  <Dialog
    v-model:visible="sensorSearchDialog"
    :style="{ width: '50vw' }"
    :maximizable="true"
    :modal="true"
    :dismissable-mask="true"
  >
    <p>todo edit options, list of metrics</p>
  </Dialog>
</template>

<script>
import InfoIcon from "../../miscellaneous/InfoIcon.vue";

export default {
  name: "ManageSensors",
  components: { InfoIcon },
  props: {
    //area/tile name
    name: {
      type: String,
      default: null,
    },
    //area/tile id
    id: {
      type: String,
      default: null,
    },
    //list of measurements connected with area/tile
    measurements: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      sensorSearchDialog: false,
    };
  },
  computed: {},
  watch: {
    objects: {
      handler: function () {
        this.loadData();
      },
      deep: true,
    },
  },
  created() {},
  methods: {
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
