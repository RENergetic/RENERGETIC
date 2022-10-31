<template>
  <Card>
    <template #title>
      {{ $t("view.add_heatmap") }}
    </template>
    <template #content>
      <!--name-->
      <div class="field grid">
        <label for="heatmapName" class="col-12 mb-2 md:col-2 md:mb-0">
          {{ $t("model.heatmap.name") }}
        </label>

        <div class="col-12 md:col-10">
          <InputText id="heatmapName" v-model="mHeatmap.name" />
        </div>
      </div>
      <!--img url-->
      <!-- TODO:  -->
      <!-- <div class="field grid">
        <label for="dasboardUrl" class="col-12 mb-2 md:col-2 md:mb-0">
          {{ $t("model.heatmap.url") }}
        </label>

        <div class="col-12 md:col-10"><InputText id="dasboardUrl" v-model="mDashboard.url" /></div>
      </div> -->

      <!-- label -->

      <div class="field grid">
        <label for="heatmapLabel" class="col-12 mb-2 md:col-2 md:mb-0"> {{ $t("model.heatmap.label") }} </label>

        <div class="col-12 md:col-10">
          <InputText id="heatmapLabel" v-model="mHeatmap.label" />
        </div>
      </div>
      <div class="field grid">
        <label for="heatmapLabel" class="col-12 mb-2 md:col-2 md:mb-0"> {{ $t("model.heatmap.label") }} </label>

        <div class="col-12 md:col-10">
          <Dropdown
            v-model="mHeatmap.type"
            :placeholder="$t('model.heatmap.select_type')"
            :options="typeList"
            option-label="label"
            option-value="value"
            class="col-12"
          />
        </div>
      </div>

      <div class="field grid">
        <div class="col-6 md:col-6"><Button :label="$t('view.button.submit')" @click="submit" /></div>
        <div class="col-6 md:col-6">
          <Button :label="$t('view.button.cancel')" @click="cancel" />
        </div>
      </div>
    </template>
  </Card>
</template>
<script>
//TODO: validate
import { HeatMap } from "@/plugins/model/HeatMap.js";
import { HeatMapType } from "../../../plugins/model/Enums";
export default {
  name: "HeatMapForm",
  props: {
    modelValue: {
      type: Object,
      default: () => new HeatMap(),
    },
  },
  emits: ["update:modelValue", "save", "cancel"],
  data() {
    return {
      mHeatmap: this.modelValue,
      typeList: [
        { value: HeatMapType.DEFAULT, label: this.$t(`enums.heatmap_type.${HeatMapType.DEFAULT}`) },
        { value: HeatMapType.OSM, label: this.$t(`enums.heatmap_type.${HeatMapType.OSM}`) },
      ],
    };
  },
  watch: {
    mHeatmapName: {
      handler(newVal, oldValue) {
        if (oldValue == null && newVal == null) {
          return;
        }
        this.$emit("update:modelValue", newVal);
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    clear() {
      this.mHeatmap = {};
    },
    cancel() {
      this.$emit("cancel");
    },

    async submit() {
      this.$emit("save", this.mHeatmap);
    },
  },
};
</script>

<style lang="scss"></style>
