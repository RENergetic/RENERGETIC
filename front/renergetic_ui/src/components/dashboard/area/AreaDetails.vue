<template>
  <Card>
    <template v-if="mArea != null" #title>
      <div class="flex">
        <div class="flex flex-grow-1">{{ mArea.label }}</div>
        <div class="flex flex-none">
          <i v-tooltip="$t('view.measurements')" class="pi pi-chart-line" @click="viewMeasurements()" />
        </div>
        <!-- TODO: new icon for manage_sensors -->
        <div class="flex flex-none">
          <i
            v-if="edit"
            v-tooltip="$t('view.manage_sensors')"
            class="pi pi-plus"
            @click="() => (manageSensorsDialog = !manageSensorsDialog)"
          />
        </div>
        <div class="flex flex-none">
          <i v-if="edit" v-tooltip="$t('view.delete_area')" class="pi pi-times" />
        </div>
      </div>
    </template>
    <template v-else #title>
      {{ $t("view.selected_area") }}
    </template>
    <template v-if="mArea != null" #content>
      <div class="field grid">
        <label for="heatmapLabel" class="col-fixed" style="width: 5rem">
          {{ $t("model.heatmap.label") }}
        </label>
        <div class="col">
          <InputText id="heatmapLabel" v-model="mArea.label" :aria-readonly="!edit" />
        </div>
      </div>
      <!-- ASSET -->
      <div id="asset-select" class="flex">
        <div v-if="mArea.asset" class="flex flex-grow-1">
          {{ mArea.asset.label }}
          <i
            v-if="mArea.asset"
            v-tooltip="$t('view.asset')"
            class="pi pi-arrow-circle-right"
            @click="assetDialog = true"
          />
        </div>
        <div v-else class="flex flex-grow-1">
          <span v-if="edit">
            {{ $t("view.select_asset") }}
          </span>
          <span v-else class="disabled"> {{ $t("view.no_asset") }} </span>
        </div>
        <div class="flex flex-none">
          <i v-if="edit" v-tooltip="$t('view.select_asset')" class="pi pi-pencil" @click="selectAsset" />
        </div>
      </div>
      <!-- DASHBOARD  -->
      <div id="dashboard-select" class="flex">
        <div v-if="mArea.dashboard" class="flex flex-grow-1">
          {{ $t("view.go_to_dashboard") }}
          <i
            v-if="mArea.dashboard"
            v-tooltip="$t('view.go_to_dashboard')"
            class="pi pi-arrow-circle-right"
            @click="$router.push(`/dashboard/view/${mArea.dashboard.id}`)"
          />
        </div>
        <div v-else class="flex flex-grow-1">
          <span v-if="edit">
            {{ $t("view.select_dashboard") }}
          </span>
          <span v-else class="disabled"> {{ $t("view.no_dashboard") }} </span>
        </div>
      </div>
      <!-- HEATMAP  -->
      <div id="heatmap-select" class="flex">
        <div v-if="mArea.heatmap" class="flex flex-grow-1">
          {{ $t("model.heatmap.heatmap") }}
        </div>
        <div v-else class="flex flex-grow-1">
          <span v-if="edit">
            {{ $t("view.select_heatmap") }}
          </span>
          <span v-else class="disabled"> {{ $t("view.no_heatmap") }} </span>
        </div>
        <div class="flex flex-none">
          <i
            v-if="mArea.heatmap"
            v-tooltip="$t('view.go_to_heatmap')"
            class="pi pi-arrow-circle-right"
            @click="$router.push(`/dashboard/heatmap/view/${mArea.heatmap.id}`)"
          />
          <i v-if="edit" v-tooltip="$t('view.select_heatmap')" class="pi pi-pencil" @click="selectHeatmap" />
        </div>
      </div>
    </template>
  </Card>
  <!-- Dialog section -->
  <div v-if="mArea">
    <Dialog
      v-model:visible="measurementDialog"
      :style="{ width: '75vw' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="true"
    >
      <MeasurementChart v-if="mArea != null" :objects="[mArea.id]"></MeasurementChart>
    </Dialog>
    <Dialog
      v-model:visible="manageSensorsDialog"
      :style="{ width: '75vw' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="true"
    >
      <ManageSensors></ManageSensors>
    </Dialog>

    <HeatMapSelect ref="heatmapSelectDialog" :current="mArea.dashboard" @change="updateHeatMap"></HeatMapSelect>
    <DashboardSelect ref="dashboardDialog"></DashboardSelect>
    <AssetSelect ref="dashboardDialog"></AssetSelect>
  </div>
</template>
<script>
import Card from "primevue/card";
import MeasurementChart from "../measurements/MeasurementChart.vue";
import ManageSensors from "../measurements/ManageSensors.vue";
import AssetSelect from "../../management/infrastructure/AssetSelect.vue";
import HeatMapSelect from "./HeatMapSelect.vue";
import DashboardSelect from "../DashboardSelect.vue";

import Dialog from "primevue/dialog";
export default {
  name: "AreaDetails",
  components: {
    Card,
    Dialog,
    MeasurementChart,
    ManageSensors,
    HeatMapSelect,
    DashboardSelect,
    AssetSelect,
  },
  props: {
    edit: {
      type: Boolean,
      default: false,
    },
    modelValue: {
      type: Object,
      default: () => null,
    },
  },
  emits: ["delete", "update:modelValue"],
  data() {
    return {
      label: this.modelValue != null ? this.modelValue.label : null,
      mArea: null,
      measurementDialog: false,
      manageSensorsDialog: false,
      manageAssets: false,
      heatMapDialog: false,
    };
  },
  watch: {
    mArea: {
      handler(newVal, oldValue) {
        if (oldValue == null && newVal == null) {
          return;
        }
        this.$emit("update:modelValue", newVal);
      },
      deep: true,
      immediate: true,
    },
    modelValue: function (newVal) {
      this.mArea = newVal;
    },
  },
  methods: {
    selectHeatmap() {
      this.$refs.heatmapSelectDialog.open();
    },
    selectDashboard() {
      this.$refs.dashboardDialog.open();
    },
    selectAsset() {
      this.$refs.assetSelect.open();
    },

    viewMeasurements() {
      this.measurementDialog = true;
    },

    updateDashboard(dashboard) {
      this.mArea.dashboard = dashboard;
      // todo: save to api
    },
    updateHeatMap(heatmap) {
      this.mArea.heatmap = heatmap;
      // todo: save to api
    },
    deleteArea() {
      this.$confirm.require({
        message: this.$t("view.heatmap_area_delete_confirm", {
          label: this.label,
        }),
        header: this.$t("view.heatmap_area_delete"),
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          this.$emit("delete", this.mArea);
        },
        reject: () => {
          this.$confirm.close();
        },
      });
    },
  },
};
</script>

<style lang="scss">
i.pi {
  font-size: 1rem;
}
</style>
