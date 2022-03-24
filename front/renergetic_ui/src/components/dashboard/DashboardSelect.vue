<template>
  <div>
    <Dialog
      v-model:visible="dashboardDialog"
      :style="{ width: '75vw' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="true"
    >
      <Card>
        <template #title> {{ $t("view.dashboard_list") }} </template>
        <template #content>
          <Listbox v-if="dashboardList" v-model="selectedDashboard" :options="dashboardList" option-label="label">
            <template #option="slotProps">
              <div>{{ slotProps.option.label }}</div>
            </template>
          </Listbox>
          <div v-if="selectedDashboard" class="field grid">
            <div class="col">
              <Button :label="$t('view.button.submit')" @click="submit" />
            </div>
            <div class="col">
              <Button :label="$t('view.button.cancel')" @click="cancel" />
            </div>
          </div>
        </template>
      </Card>
    </Dialog>
  </div>
</template>
<script>
import Listbox from "primevue/listbox";

import Card from "primevue/card";
export default {
  name: "DashboardSelect",
  components: { Card, Listbox },
  props: { current: { type: Object, default: () => ({}) } },
  emits: { change: null },
  data() {
    return {
      dashboardList: [],
      selectedDashboard: null,
      dashboardDialog: false,
    };
  },
  async mounted() {},
  methods: {
    submit() {
      if (this.selectedDashboard) {
        this.$emit("change", this.selectedDashboard);
      }
    },
    async open() {
      await this.$ren.utils.reloadDashboard().then((dashboardList) => {
        this.dashboardDialog = true;
        if (this.current) {
          if (dashboardList.find((it) => it.id == this.current.id) == null) {
            this.dashboardList = [this.current] + dashboardList;
          }
          this.dashboardDialog = this.current;
        }
        this.dashboardList = dashboardList;
      });
    },
    cancel() {
      this.dashboardDialog = false;
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
