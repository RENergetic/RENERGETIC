<template>
  <div>
    TODO: list notifications:
    {{ mNotifications }}
    objects:{{ objects }}
  </div>
</template>
<script>
// THEN to get HTML5 drag&drop
export default {
  name: "NotificationList",
  components: {},
  props: {
    notifications: {
      type: Array,
      default: () => null,
    },
    objects: { type: Array, default: null },
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
    objects: {
      handler: function () {
        // todo: load data
      },
      deep: true,
    },
  },
  async created() {
    if (this.notifications == null && this.objects != null) {
      //todo:
      this.$ren.dataApi.getNotifications(this.objects).then((notifications) => {
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
