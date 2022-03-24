<template>
  <Card>
    <template #title>
      {{ $t("view.add_dashboard") }}
    </template>
    <template #content>
      <!--name-->
      <div class="field grid">
        <label for="dasboardName" class="col-12 mb-2 md:col-2 md:mb-0">
          {{ $t("model.dashboard.name") }}
        </label>

        <div class="col-12 md:col-10">
          <InputText id="dasboardName" v-model="mDashboard.name" />
        </div>
      </div>
      <!--url-->

      <div class="field grid">
        <label for="dasboardUrl" class="col-12 mb-2 md:col-2 md:mb-0">
          {{ $t("model.dashboard.url") }}
        </label>

        <div class="col-12 md:col-10"><InputText id="dasboardUrl" v-model="mDashboard.url" /></div>
      </div>

      <!-- label -->

      <div class="field grid">
        <label for="dasboardLabel" class="col-12 mb-2 md:col-2 md:mb-0"> {{ $t("model.dashboard.label") }} </label>

        <div class="col-12 md:col-10">
          <InputText id="dasboardLabel" v-model="mDashboard.label" />
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
import Card from "primevue/card";
export default {
  name: "DashboardAdd",
  components: { Card },
  props: {
    dashboard: {
      type: Object,
      default: () => ({}),
    },
  },
  emits: ["update:modelValue", "save", "cancel"],
  data() {
    // this.$emit("update:modelValue", newVal);
    // this.$emit("save", newVal);
    return { mDashboard: this.dashboard };
  },
  watch: {
    mDashboard: {
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
  mounted() {},
  methods: {
    clear() {
      this.mDashboard = {};
    },
    cancel() {
      this.$emit("cancel");
    },
    async submit() {
      this.$emit("save", this.mDashboard);
    },
  },
};
</script>

<style lang="scss"></style>
