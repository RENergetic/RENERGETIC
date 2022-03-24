<template>
  <div>
    <div style="height: 100%" class="flex flex-column align-items-start align-content-start">
      <template v-for="n in mNotifications" :key="n.id">
        <NotificationItem :notification="n"></NotificationItem>
      </template>
    </div>
  </div>
</template>
<script>
import NotificationItem from "./NotificationItem.vue";
import { NotificationContext } from "@/plugins/model/Enums.js";
export default {
  name: "NotificationList",
  components: { NotificationItem },
  props: {
    notifications: {
      type: Array,
      default: () => null,
    },
    objectId: { type: Number, default: null },
    context: { type: String, default: NotificationContext.USER },
    dateInterval: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {
      mNotifications: [],
    };
  },
  computed: {},
  watch: {
    notifications: function (newValue) {
      this.mNotifications = newValue;
    },
  },
  async created() {
    if (this.notifications == null) {
      //todo:
      this.$ren.managementApi.getNotifications(this.objectId, this.context).then((notifications) => {
        this.mNotifications = notifications;
      });
    } else {
      this.mNotifications = this.notifications;
    }
    //todo: catch
  },

  methods: {},
};
</script>

<style lang="scss"></style>
