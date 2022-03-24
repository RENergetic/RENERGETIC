<template>
  <InfoIcon :show-icon="false">
    <template #content>
      <!-- some info -->
    </template>
  </InfoIcon>
  <div class="field grid">
    <label for="panelLabel" class="col-fixed" style="width: 5rem">
      {{ $t("model.panel.label") }}
    </label>
    <div class="col">
      <InputText id="panelLabel" v-model="label" :aria-readonly="!edit" />
    </div>
  </div>
  <div class="field grid">
    <label for="panelName" class="col-fixed" style="width: 5rem">
      {{ $t("model.panel.name") }}
    </label>
    <div class="col">
      <InputText id="panelName" v-model="name" :aria-disabled="true" :aria-readonly="!edit" />
    </div>
  </div>
  <div class="field grid">
    <div class="col">
      <Button :label="$t('view.button.submit')" @click="submit" />
    </div>
    <div class="col">
      <Button :label="$t('view.button.cancel')" @click="cancel" />
    </div>
  </div>

  <!-- change -->
</template>

<script>
export default {
  name: "InformationPanelForm",
  components: {},
  props: {
    edit: { type: Boolean, default: true },
    modelValue: {
      type: Object,
      default: () => ({}),
    },
  },
  emits: ["update:modelValue", "cancel", "save"],
  data() {
    return {
      name: "",
      label: null,
    };
  },
  computed: {},
  watch: {
    label: {
      handler(newVal) {
        if (newVal == null) this.name = null;
        else this.name = newVal.toLowerCase().trim().replace(" ", "_");
      },
    },
  },
  methods: {
    submit() {
      let model = { tiles: [], name: this.name, label: this.label };
      this.$emit("update:modelValue", model);
      this.$emit("save", model);
    },
    cancel() {
      this.$emit("cancel", this.model);
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
