<template>
  <div style="display: block; width: 10rem">
    todo view list of recommendation {{ mRecommendations }} objects:{{ objects }}
  </div>
</template>
<script>
//todo: download recommendations
export default {
  name: "RecommendationView",
  components: {},
  props: {
    recommendations: {
      type: Array,
      default: () => null,
    },

    objects: { type: Array, default: null },
  },
  data() {
    return { mRecommendations: [] };
  },
  computed: {},
  watch: {
    objects: {
      handler: function () {
        // todo: load data
      },
      deep: true,
    },
  },
  created() {
    this.mRecommendations = this.recommendations;
  },

  methods: {},
};
</script>

<style lang="scss"></style>
