<template>
  <div>
    <Toast />
    <ConfirmDialog></ConfirmDialog>
    <SideMenu ref="sideMenu" />
    <router-view :class="layout()" @update-menu="updateMenu()" />
    <Footer style="display: none">
      <template #right> </template>
    </Footer>
  </div>
</template>
<script>
import SideMenu from "./components/miscellaneous/SideMenu";
import Toast from "primevue/toast";
import ConfirmDialog from "primevue/confirmdialog";
import Footer from "./components/miscellaneous/Footer.vue";
export default {
  name: "App",
  components: {
    Toast,
    SideMenu,
    ConfirmDialog,
    Footer,
  },
  computed: {
    isLoading() {
      return this.$store.getters["spinner/isLoading"];
    },
  },
  watch: {},
  mounted() {},
  methods: {
    layout() {
      let layout = this.$route.meta.layout == null ? "standard" : this.$route.meta.layout;
      return layout;
    },
    updateMenu() {
      this.$refs.sideMenu.reload();
    },
  },
};
</script>

<style lang="scss">
.standard {
  margin-top: 3rem;
}
.fullscr {
  margin-top: 0rem;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  // text-align: center;
  color: #2c3e50;
}

#nav {
  padding: 30px;

  a {
    font-weight: bold;
    color: #2c3e50;

    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
</style>
