<template>
  <Card>
    <template v-if="mArea != null" #title>
      <div class="flex">
        <div class="flex flex-none">
          <i v-tooltip="$t('view.measurements')" class="pi pi-chart-line" @click="viewMeasurements()" />
        </div>
        <div class="flex flex-none">
          <i v-if="edit" v-tooltip="$t('view.delete_area')" class="pi pi-times" @click="deleteArea" />
        </div>
        <div class="flex flex-grow-1">{{ mArea.label }}</div>
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
          <span v-if="mArea.asset != null">
            <i v-tooltip="$t('view.show')" class="pi pi-arrow-circle-right" @click="showAsset" />
          </span>
        </div>
        <div v-else class="flex flex-grow-1">
          <span v-if="edit">
            {{ $t("view.select_asset") }}
          </span>
          <span v-else class="disabled"> {{ $t("view.no_asset") }} </span>
        </div>
        <div class="flex flex-none">
          <i v-if="edit" v-tooltip="$t('view.select_asset')" class="pi pi-pencil" @click="selectAsset()" />
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

        <div class="flex flex-none">
          <i v-if="edit" v-tooltip="$t('view.select_dashboard')" class="pi pi-pencil" @click="selectDashboard" />
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

    <HeatMapSelect ref="heatmapSelectDialog" :current="mArea.dashboard" @change="updateHeatMap"></HeatMapSelect>
    <DashboardSelect ref="dashboardDialog" @change="onDashboardUpdate"></DashboardSelect>
    <AssetSelect ref="assetSelectDialog" @change="onAssetUpdate"></AssetSelect>
    <AssetView ref="assetViewDialog"></AssetView>
  </div>
</template>
<script>
import MeasurementChart from "../measurements/MeasurementChart.vue";
import AssetSelect from "../../management/infrastructure/AssetSelect.vue";
import HeatMapSelect from "./HeatMapSelect.vue";
import DashboardSelect from "../grafana/DashboardSelect.vue";
import AssetView from "@/components/management/infrastructure/AssetView.vue";
export default {
  name: "AreaDetails",
  components: {
    MeasurementChart,
    HeatMapSelect,
    DashboardSelect,
    AssetSelect,
    AssetView,
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
    modelValue: {
      handler(newVal) {
        this.mArea = newVal;
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    selectHeatmap() {
      this.$refs.heatmapSelectDialog.open();
    },
    selectDashboard() {
      this.$refs.dashboardDialog.open();
    },
    async selectAsset() {
      if (this.mArea.asset != null) {
        await this.$ren.managementApi
          .getAsset(this.mArea.asset.id)
          .then((asset) => this.$refs.assetSelectDialog.open(asset));
      } else this.$refs.assetSelectDialog.open();
    },
    async showAsset() {
      if (this.mArea.asset != null) {
        await this.$ren.managementApi
          .getAsset(this.mArea.asset.id)
          .then((asset) => this.$refs.assetViewDialog.open(asset));
      }
    },

    onAssetUpdate(asset) {
      this.mArea.assetId = asset.id;
      this.mArea.asset = asset;
    },
    viewMeasurements() {
      this.measurementDialog = true;
    },

    onDashboardUpdate(dashboard) {
      this.mArea.dashboard = dashboard;
    },
    updateHeatMap(heatmap) {
      this.mArea.heatmap = heatmap;
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
