<template>
  <InformationTileItem
    v-for="m in tile.measurements"
    :key="m.id"
    :style="{ fontSize: fontSize }"
    :tile-item="m"
    :pdata="pdata"
  ></InformationTileItem>
</template>
<script>
import InformationTileItem from "./InformationTileItem.vue";
export default {
  name: "InformationTile",
  components: { InformationTileItem },
  props: {
    // edit: { type: Boolean, default: false },
    tile: {
      type: Object,
      default: () => ({}),
    },
    pdata: {
      type: Object,
      default: () => ({}),
    },
    fontSize: {
      type: String,
      default: "2rem",
    },
  },
  // emits: ["edit", "notification"],
  // data() {
  //   return {};
  // },
  computed: {},

  mounted() {},
};
</script>

<style lang="scss"></style>
