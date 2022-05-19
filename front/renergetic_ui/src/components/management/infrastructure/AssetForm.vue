<template>
  <InfoIcon :show-icon="false">
    <template #content>
      <!-- some info -->
    </template>
  </InfoIcon>
  <div class="field grid">
    <label for="assetName" class="col-fixed" style="width: 5rem">
      {{ $t("model.asset.name") }}
    </label>
    <div class="col">
      <InputText id="assetName" v-model="mModel.name" :aria-readonly="!edit" />
    </div>
  </div>
  <div class="field grid">
    <label for="assetLabel" class="col-fixed" style="width: 5rem">
      {{ $t("model.asset.label") }}
    </label>
    <div class="col">
      <InputText id="assetLabel" v-model="mModel.label" :aria-readonly="!edit" />
    </div>
  </div>
  <div class="field grid">
    <label for="assetType" class="col-fixed" style="width: 5rem">
      {{ $t("model.asset.asset_type") }}
    </label>
    <div class="col">
      <Dropdown
        id="assetType"
        v-model="mModel.type"
        :options="assetTypes"
        option-label="label"
        option-value="value"
        :placeholder="$t('view.select_asset_type')"
      />
    </div>
  </div>

  <div class="field grid">
    <label for="assetParent" class="col-fixed" style="width: 5rem">
      {{ $t("model.asset.parent") }}
    </label>
    <div class="col">
      <span v-if="parentLabel" @click="selectAsset">{{ parentLabel }}</span>
      <span v-else @click="selectAsset">{{ $t("view.select_parent_asset") }}</span>
    </div>
  </div>

  <div class="field grid">
    <label for="owner" class="col-fixed" style="width: 5rem">
      {{ $t("model.asset.owner") }}
    </label>
    <div class="col">
      <span v-if="ownerLabel" @click="selectOwner">{{ ownerLabel }}</span>
      <span v-else @click="selectOwner">{{ $t("view.select_owner") }}</span>
    </div>
  </div>

  <Button :label="$t('view.button.submit')" @click="submit" />
  <Button :label="$t('view.button.cancel')" @click="cancel" />
  <AssetSelect ref="assetSelectDialog" v-model="mModel.parent" />
  <AssetSelect ref="ownerSelectDialog" v-model="mModel.owner" category="user" />
  <!-- change -->
</template>

<script>
import InfoIcon from "../../miscellaneous/InfoIcon.vue";
import { AssetTypes } from "@/plugins/model/Enums.js";
import AssetSelect from "./AssetSelect.vue";
export default {
  name: "AssetForm",
  components: { InfoIcon, AssetSelect },
  props: {
    modelValue: {
      type: Object,
      default: () => ({ measurements: [] }),
    },
  },
  emits: ["update:modelValue", "cancel"],
  data() {
    return {
      assetTypes: Object.entries(AssetTypes).map((k) => {
        return { value: k[1], label: this.$t("enums.asset_type." + k[1]) };
      }),
      mModel: this.modelValue,
      // assetTypes: [
      //   { value: "room", label: this.$t("model.asset.asset_types.room") },
      //   { value: "pv", label: this.$t("model.asset.asset_types.pv") },
      // ],
    };
  },
  computed: {
    parentLabel: function () {
      return this.mModel != null && this.mModel.parent != null ? this.mModel.parent.label : null;
    },
    ownerLabel: function () {
      return this.mModel != null && this.mModel.owner != null ? this.mModel.owner.label : null;
    },
  },
  watch: {},
  async mounted() {
    // LOAD ASSET TYPES
    this.assetTypes = (await this.$ren.managementApi.listAssetType()).map((type) => {
      return { value: type.id, label: type.label };
    });
  },
  methods: {
    selectAsset() {
      this.$refs.assetSelectDialog.open();
    },
    selectOwner() {
      this.$refs.ownerSelectDialog.open();
    },
    submit() {
      this.$emit("update:modelValue", this.mModel);
    },
    cancel() {
      this.$emit("cancel", this.model);
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
