<template>
  <InfoIcon :show-icon="false">
    <template #content>
      <!-- some info -->
    </template>
  </InfoIcon>
  <div class="card">
    <div class="field grid">todo: find asset</div>
  </div>
</template>

<script>
import InfoIcon from "../miscellaneous/InfoIcon.vue";

export default {
  name: "FindAsset",
  components: { InfoIcon },
  props: {},
  data() {
    return {
      data: null,
    };
  },
  computed: {},
  watch: {
    objects: {
      handler: function () {
        this.loadData();
      },
      deep: true,
    },
  },
  async created() {},
  methods: {
    async loadData() {
      await this.$ren.dataApi.listAssets().then((data) => {
        this.data = data;
      });
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
