<template>
  <div>
    <Menu id="overlay_menu" ref="menu" :model="model" :popup="true" />
    <Button
      id="menu-toggle"
      :class="'p-button-rounded p-button-text ' + position + fixedPosition"
      aria-haspopup="true"
      aria-controls="overlay_menu"
      icon="pi pi-ellipsis-v"
      @click="toggle"
    >
      <!-- <BIconThreeDotsVertical /> -->
    </Button>
    <!-- <Button
      type="button"
      label="Toggle"
      @click="toggle"
      aria-haspopup="true"
      aria-controls="overlay_menu"
    /> -->
  </div>
</template>

<script>
import Menu from "primevue/menu";
// import { BIconThreeDotsVertical } from "bootstrap-icons-vue";
export default {
  name: "DotMenu",
  components: {
    Menu,
    // BIconThreeDotsVertical,
  },
  props: {
    model: {
      type: Object,
      default: () => ({}),
    },
    position: {
      type: String,
      default: "top right",
    },
    fixed: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return { fixedPosition: this.fixed ? " fixed" : " absolute" };
  },
  watch: {},

  async created() {},
  methods: {
    toggle(event) {
      this.$refs.menu.toggle(event);
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
#menu-toggle {
  font-size: 1.5rem;
  z-index: 1002;
  padding: 0;
  // height: 2rem;
  // text-align: center;
  // width: 2rem;
}

.fixed {
  position: fixed;
}

.absolute {
  position: absolute;
}

.top {
  top: 0.5rem;
}
.right {
  right: 0.5rem;
}
</style>
