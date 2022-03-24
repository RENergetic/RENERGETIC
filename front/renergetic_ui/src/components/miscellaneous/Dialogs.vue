<template>
  <Dialog
    v-model:visible="mNotifications"
    :style="{ width: '75vw' }"
    :maximizable="false"
    :modal="true"
    :dismissable-mask="true"
  >
    <NotificationList></NotificationList>
  </Dialog>
  <Dialog
    v-model:visible="mAddDashboard"
    :style="{ width: '50vw' }"
    :maximizable="true"
    :modal="true"
    :dismissable-mask="true"
  >
    <DashboardForm @save="onSave" @cancel="mAddDashboard = false"></DashboardForm>
  </Dialog>
</template>

<script>
import Dialog from "primevue/dialog";
import NotificationList from "../management/notification/NotificationList.vue";
import DashboardForm from "../../components/dashboard/DashboardForm.vue";
export default {
  name: "Dialogs",
  components: {
    Dialog,
    NotificationList,
    DashboardForm,
  },
  props: {
    notifications: {
      type: Boolean,
      default: false,
    },
    addDashboard: {
      type: Boolean,
      default: false,
    },
  },
  emits: ["update:dashboard", "update:notifications", "UpdateMenu"],
  data() {
    return {
      mNotifications: this.notifications,
      mAddDashboard: this.addDashboard,
    };
  },
  watch: {
    //dashboard
    mAddDashboard: {
      handler(newVal, oldValue) {
        if (oldValue == null && newVal == null) {
          return;
        }
        this.$emit("update:dashboard", newVal);
      },
      immediate: true,
    },
    addDashboard: {
      handler(newVal) {
        this.mAddDashboard = newVal;
      },
      immediate: true,
    },
    //notifications
    mNotifications: {
      handler(newVal, oldValue) {
        if (oldValue == null && newVal == null) {
          return;
        }
        this.$emit("update:notifications", newVal);
      },
      immediate: true,
    },
    notifications: {
      handler(newVal) {
        this.mNotifications = newVal;
      },
      immediate: true,
    },
  },
  methods: {
    async onSave(dashboard) {
      await this.$ren.dashboardApi.add(dashboard).then((dashboardReq) => {
        this.$store.commit("view/dashboardsAdd", dashboardReq);
        this.$emit("UpdateMenu", null);
      });
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
