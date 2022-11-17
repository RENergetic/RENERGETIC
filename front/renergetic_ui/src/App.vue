<template>
  <div v-if="keycloakState == 1">
    <Toast />
    <ConfirmDialog></ConfirmDialog>
    <SideMenu ref="sideMenu" />
    <router-view v-if="hasAccess" :key="$route.path" :class="layout()" @update-menu="updateMenu()" />
    <!-- TODO: v-else -->
    <!-- {{ $keycloak && $keycloak.isInitialized() }} -->
    <Footer style="display: none">
      <template #right> </template>
    </Footer>
  </div>
  <div v-else-if="keycloakState == 0">
    <h2>Keycloak not initialized</h2>
  </div>

  <!-- TODO: v-else do something when keycloakState == -1 -->
</template>
<script>
import SideMenu from "./components/miscellaneous/SideMenu";
import Toast from "primevue/toast";
import ConfirmDialog from "primevue/confirmdialog";
import Footer from "./components/miscellaneous/Footer.vue";
// import createKeyCloak from "@/plugins/auth2";
export default {
  name: "App",
  components: {
    Toast,
    SideMenu,
    ConfirmDialog,
    Footer,
  },
  data() {
    return {
      keycloakState: -1,
    };
  },
  computed: {
    hasAccess() {
      if (this.$route.meta.roleFlag == null || this.$route.meta.roleFlag == undefined) return true;
      return this.$ren.utils.checkAccess(this.$route.meta.roleFlag);
    },
    isLoading() {
      return this.$store.getters["spinner/isLoading"];
    },
  },
  async mounted() {
    await this.$keycloak.get();
    if (this.$keycloak.isInitialized()) this.keycloakState = 1;
    else this.keycloakState = 0;
  },
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
