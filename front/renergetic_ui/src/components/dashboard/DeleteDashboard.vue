<template>
  <div></div>
</template>
<script>
export default {
  name: "DeleteDashboard",
  components: {},
  props: {
    id: {
      type: String,
      default: null,
    },
    label: {
      type: String,
      default: null,
    },
  },
  mounted() {},
  methods: {
    delete() {
      this.$confirm.require({
        message: this.$t("view.dashboard_delete_confirm", {
          label: this.label,
        }),
        header: this.$t("view.dashboard_delete"),
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          this.deleteDashboard(this.id);
          this.$store.commit("view/dashboardsDel", this.id);
          this.$router.replace("Dashboard");
        },
        reject: () => {
          this.$confirm.close();
        },
      });
    },
    async deleteDashboard(id) {
      await this.$ren.dashboardApi
        .delete(id)
        .then(() => {
          this.$toast.add({
            severity: "success",
            summary: this.$t("view.toast.dashboard_delete_success", {
              label: this.label,
            }),
            life: 3000,
          });
        })
        .catch(() => {
          this.$toast.add({
            severity: "error",
            summary: this.$t("view.toast.dashboard_delete_fail", {
              label: this.label,
            }),
            life: 3000,
          });
        });
    },
  },
};
</script>

<style lang="scss"></style>
