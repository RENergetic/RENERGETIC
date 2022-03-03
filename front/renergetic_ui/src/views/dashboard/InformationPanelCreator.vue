<template>
  <div>
    <!-- <DotMenu :model="menuModel" /> -->

    todo create new panel
  </div>
</template>
<script>
// import InformationPanel from "../../components/dashboard/InformationPanel.vue";
// import DotMenu from "../../components/miscellaneous/DotMenu.vue";

export default {
  name: "InformationPanelCreator",
  components: {
    // InformationPanel,
    // DotMenu,
  },
  data() {
    return {
      panel: null,
      locked: false,
      editMode: false,
    };
  },

  methods: {},
};
</script>

<style lang="scss"></style>
