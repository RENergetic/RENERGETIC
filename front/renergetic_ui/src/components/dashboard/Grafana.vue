<template>
  <div id="dashboard">
    <iframe v-if="url != null" :src="url"></iframe>
  </div>
</template>
<script>
export default {
  name: "Grafana",
  components: {},
  props: {
    url: {
      type: String,
      default: null,
    },
  },
  // data() {
  //   return {};
  // },

  mounted() {},
  methods: {},
};
</script>

<style lang="scss">
#dashboard iframe {
  width: 100%;
  height: 100%;
}
#dashboard {
  position: relative;
  height: 100%;
  width: 100%;
  display: inline-block;
}
</style>
