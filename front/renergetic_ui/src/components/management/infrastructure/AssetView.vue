<template>
  <div>
    <Dialog v-model:visible="assetDialog" :maximizable="true" :modal="true" :dismissable-mask="true">
      <Card>
        <template #title> {{ $t("view.asset_list") }} </template>
        <template #content>
          <div class="field grid">
            <label for="assetLabel" class="col-fixed" style="width: 5rem">
              {{ $t("model.asset.label") }}
            </label>
            <div class="col">{{ mAsset.label }} ({{ mAsset.name }})</div>
          </div>
          <div class="field grid">
            <label class="col-fixed" style="width: 5rem">
              {{ $t("model.asset.asset_type") }}
            </label>
            <div class="col">{{ mAsset.asset_type }}</div>
          </div>
          <div class="field grid">
            <label class="col-fixed" style="width: 5rem">
              {{ $t("model.asset.child") }}
            </label>
            <div class="col">
              <span v-if="mAsset.child && mAsset.child.length > 0" @click="viewChildren">
                {{ $t("view.view_asset_children") }}
              </span>
              <span v-else class="disabled">
                {{ $t("view.no_asset_children") }}
              </span>
            </div>
          </div>
          <div class="field grid">
            <label class="col-fixed" style="width: 5rem">
              {{ $t("model.asset.child") }}
            </label>
            <div class="col">
              <span v-if="mAsset.parent" @click="setParent">
                {{ mAsset.parent.label }}
              </span>
              <span v-else class="disabled" @click="setParent">
                {{ $t("view.no_parent") }}
              </span>
            </div>
          </div>
          <div class="field grid">
            <label class="col-fixed" style="width: 5rem">
              {{ $t("model.asset.measurements") }}
            </label>
            <div class="col">
              <span v-if="mAsset.measurements && mAsset.measurements.length > 0" @click="viewMeasurements">
                {{ $t("view.view_asset_measurements") }}
              </span>
              <span v-else class="disabled">
                {{ $t("view.no_asset_measurements") }}
              </span>
            </div>
          </div>
        </template>
      </Card>
    </Dialog>

    <AssetSelect ref="assetSelectDialog" @change="onParentChange" />

    <Dialog
      v-model:visible="childDialog"
      :style="{ width: '75vw' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="true"
    >
      <Card>
        <template #title> {{ $t("model.asset.child") }} </template>
        <template #content>
          <DataTable v-if="mAsset" :value="mAsset.child">
            <!-- <Column v-for="col of columns" :key="col" :field="col" :header="$t('model.asset.' + col)"></Column> -->
            <Column field="name" :header="$t('model.asset.name')"> </Column>
            <Column field="label" :header="$t('model.asset.label')"> </Column>
            <Column field="asset_type" :header="$t('model.measurement.asset_type')"> </Column>
          </DataTable>
          <span v-else>
            {{ $t("view.asset_child_empty") }}
          </span>
        </template>
      </Card>
    </Dialog>
    <Dialog
      v-model:visible="measurementDialog"
      :style="{ width: '75vw' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="true"
    >
      <Card>
        <template #title> {{ $t("model.asset.measurements") }} </template>
        <template #content>
          <!-- {{ selectedRow.data.measurements }}  -->
          <DataTable v-if="selectedRow" :value="mAsset.measurements">
            <!-- <Column v-for="col of columns" :key="col" :field="col" :header="$t('model.asset.' + col)"></Column> -->
            <Column field="name" :header="$t('model.measurement.name')"> </Column>
            <Column field="label" :header="$t('model.measurement.label')"> </Column>
            <Column field="measurement_type" :header="$t('model.measurement.measurement_type')"> </Column>
          </DataTable>
          <span v-else>
            {{ $t("view.no_asset_measurements") }}
          </span>

          <Button :label="$t('view.button.add_measurement')" @click="addMeasurement" />
          <measurement-select
            ref="measurementSelectDialog"
            :asset-id="mAsset.id"
            @select="onMeasurementSelect"
          ></measurement-select>
        </template>
      </Card>
    </Dialog>
  </div>
</template>
<script>
export default {
  name: "AssetView",
  components: {},
  props: { asset: { type: Object, default: () => null } },
  emits: ["change", "update:modelValue"],
  data() {
    return {
      mAsset: this.asset,
      childDialog: false,
      measurementDialog: false,
      assetDialog: false,
    };
  },
  async mounted() {},
  methods: {
    setParent() {
      this.$refs.assetSelectDialog.open(this.mAsset.parent);
    },
    viewChildren() {
      this.childDialog = true;
    },

    viewMeasurements() {
      this.measurementDialog = true;
    },

    onParentChange(parent) {
      this.mAsset.parent = parent;
    },
    addMeasurement() {
      this.$refs.measurementSelectDialog.open();
    },
    onMeasurementSelect(measurement) {
      this.mAsset.measurements.push(measurement);
    },
    submit() {
      if (this.selectedAsset) {
        this.$emit("change", this.mAsset);
        this.$emit("update:modelValue", this.mAsset);
        this.assetDialog = false;
      }
    },

    async open(asset = null) {
      if (asset == null) this.mAsset = this.asset;
      else this.mAsset = asset;
      this.assetDialog = true;
    },
    cancel() {
      this.assetDialog = false;
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
