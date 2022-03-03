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
        v-model="mModel.asset_type"
        :options="assetTypes"
        option-label="label"
        option-value="value"
        :placeholder="$t('view.select_asset')"
      />
    </div>
  </div>
  todo: select parent, select measurements

  <Button :label="$t('view.button.submit')" @click="submit" />
  <Button :label="$t('view.button.cancel')" @click="cancel" />
</template>

<script>
import InfoIcon from "../../miscellaneous/InfoIcon.vue";
import { AssetTypes } from "@/plugins/model/Enums.js";
export default {
  name: "AssetForm",
  components: { InfoIcon },
  props: {
    model: {
      type: Object,
      default: () => ({}),
    },
  },
  emits: ["update:modelValue", "cancel"],
  data() {
    return {
      assetTypes: Object.entries(AssetTypes).map((k) => {
        return { value: k[1], label: this.$t("enums.asset_type." + k[1]) };
      }),
      mModel: this.model,
      // assetTypes: [
      //   { value: "room", label: this.$t("model.asset.asset_types.room") },
      //   { value: "pv", label: this.$t("model.asset.asset_types.pv") },
      // ],
    };
  },
  computed: {},
  watch: {},
  methods: {
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
