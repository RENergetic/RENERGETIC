<template>
  <div style="display: block">some user feed back component - questionnaires or user reports action.</div>
</template>
<script>
// THEN to get HTML5 drag&drop
export default {
  name: "Feedback",
  components: {},
  props: {
    recommendations: {
      type: Array,
      default: () => null,
    },
  },
  data() {
    return {};
  },
  watch: {},
  created() {
    this.$router.push("/");
  },

  methods: {},
};
</script>

<style lang="scss"></style>
