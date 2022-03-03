<template>
  <InfoIcon :show-icon="false">
    <template #content>
      <!-- some info -->
    </template>
  </InfoIcon>

  <div class="field grid">
    <label for="measurementName" class="col-fixed" style="width: 5rem">
      {{ $t("model.measurement.name") }}
    </label>
    <div class="col">
      <InputText id="measurementName" v-model="mModel.name" :aria-readonly="true" />
    </div>
  </div>
  <div class="field grid">
    <label for="measurementLabel" class="col-fixed" style="width: 5rem">
      {{ $t("model.measurement.label") }}
    </label>
    <div class="col">
      <InputText id="measurementLabel" v-model="mModel.label" :aria-readonly="!edit" />
    </div>
  </div>

  <Button :label="$t('view.button.submit')" @click="submit" />
  <Button :label="$t('view.button.cancel')" @click="cancel" />
</template>

<script>
import InfoIcon from "../../miscellaneous/InfoIcon.vue";

export default {
  name: "MeasurementForm",
  components: { InfoIcon },
  props: {
    model: {
      type: Object,
      default: () => ({}),
    },
  },
  emits: ["update:modelValue", "cancel"],
  data() {
    return { mModel: this.model };
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
