<template>
  <div>
    <Button id="sideMenuButton" icon="pi pi-arrow-right" @click="visible = true" />
    <Sidebar v-model:visible="visible" class="ren-sidebar">
      <Logo style="position: relative; display: block; margin: 1rem; left: 1rem; bottom: initial" />
      <PanelMenu :model="menuModel" />
    </Sidebar>
    <Dialogs
      :notifications="notifications"
      :add-dashboard="dashboardDialog"
      @update:notifications="notifications = $event"
      @UpdateMenu="reload"
    ></Dialogs>
  </div>
</template>

<script>
import PanelMenu from "primevue/panelmenu";
import Sidebar from "primevue/sidebar";
import Dialogs from "./Dialogs.vue";
import Logo from "./Logo.vue";

export default {
  name: "SideMenu",
  components: {
    PanelMenu,
    Sidebar,
    Dialogs,
    Logo,
  },
  emits: ["notification"],
  data() {
    return {
      visible: false,
      menuModel: [],
      dashboards: [],
      //tODO: dialog state hashmap
      notifications: false,
      dashboardDialog: false,
      informationPanels: [],
      isAdmin: false, //this.$store.getters["user/isAdmin"],
      user: false,
    };
  },
  watch: {
    isAdmin: function () {
      this.menuModel = this.initMenu();
    },
  },

  async created() {
    this.user = await this.$keycloak.get();
    this.menuModel = this.initMenu();
    this.reload();
  },
  methods: {
    async reload() {
      this.$ren.utils
        .reloadStore()
        .then(async () => {
          //this.dashboards = this.$store.getters["view/dashboards"];
          this.dashboards = await this.$ren.dashboardApi.list();
          this.informationPanels = this.$store.getters["view/informationPanels"];
          let menu = this.initMenu();
          this.menuModel = menu;
        })
        .catch((error) => console.error(error));
    },
    panelItems() {
      if (this.informationPanels.length == 0) {
        return [];
      }
      var items = this.informationPanels.map((panel) => {
        let to = `/panel/view/${panel.id}`;
        return {
          // label: this.$t("menu.group_list"),
          label: panel.label,
          icon: "pi pi-fw pi-th-large",
          to: to,
          command: () => {
            this.$router.push(to);
          },
          visible: () => {
            let route =
              this.$router.getRoutes()[this.$router.getRoutes().findIndex((obj) => obj.name == "InformationPanelView")];
            if (this.user == undefined || this.user.resourceAccess == undefined) return false;
            else {
              return this.$keycloak.hasAccess(
                this.user.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles,
                route.meta.roles,
              );
            }
          },
        };
      });
      items.push({
        label: this.$t("menu.information_panel_list"),
        icon: "pi pi-fw  pi-align-left",
        to: "/panel",
        command: () => {
          this.$router.push({ name: "InformationPanelList" });
        },
        visible: () => {
          let route =
            this.$router.getRoutes()[
              this.$router.getRoutes().findIndex((obj) => obj.name == "InformationPanelListView")
            ];
          if (this.user == undefined || this.user.resourceAccess == undefined) return false;
          else {
            return this.$keycloak.hasAccess(
              this.user.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles,
              route.meta.roles,
            );
          }
        },
      });
      items.push({
        //tODO:
        label: this.$t("menu.add_information_panel"),
        icon: "pi pi-fw pi-plus",
        to: "/panel/add",
        command: () => {
          this.$router.push({ name: "InformationPanelCreator" });
        },
        visible: () => {
          let route =
            this.$router.getRoutes()[
              this.$router.getRoutes().findIndex((obj) => obj.name == "InformationPanelCreator")
            ];
          if (this.user == undefined || this.user.resourceAccess == undefined) return false;
          else {
            return this.$keycloak.hasAccess(
              this.user.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles,
              route.meta.roles,
            );
          }
        },
      });
      return items;
    },
    dashboardItems() {
      var items = [];
      if (this.dashboards && this.dashboards.length != 0) {
        items = this.dashboards.map((dashboardItem) => {
          let to = `/dashboard/view/${dashboardItem.id}`;
          return {
            // label: this.$t("menu.group_list"),
            label: dashboardItem.label,
            icon: "pi pi-fw pi-align-left",
            to: to,
            command: () => {
              this.$router.push(to);
            },
          };
        });
      }
      items.push({
        // label: this.$t("menu.group_list"),
        label: this.$t("menu.add_dashboard"),
        icon: "pi pi-fw pi-plus",
        // to: "/dashboard/add",
        command: () => {
          this.dashboardDialog = !this.dashboardDialog;
          // this.$router.push({ name: "DashboadAdd" });
        },
      });
      return items;
    },

    heatMapItems() {
      return [
        {
          // label: this.$t("menu.group_list"),
          label: this.$t("menu.list_heatmap"),
          icon: "pi pi-fw pi-align-left",
          to: "/dashboard/heatmap/list",
          command: () => {
            this.$router.push({ name: "HeatMapListView" });
          },
          visible: () => {
            let route =
              this.$router.getRoutes()[this.$router.getRoutes().findIndex((obj) => obj.name == "HeatMapListView")];
            if (this.user == undefined || this.user.resourceAccess == undefined) return false;
            else {
              return this.$keycloak.hasAccess(
                this.user.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles,
                route.meta.roles,
              );
            }
          },
        },
        {
          // label: this.$t("menu.group_list"),
          label: this.$t("menu.add_heatmap"),
          icon: "pi pi-fw pi-plus",
          to: "/dashboard/heatmap/add",
          command: () => {
            this.$router.push({ name: "DashboadAdd" });
          },
          visible: () => {
            let route =
              this.$router.getRoutes()[this.$router.getRoutes().findIndex((obj) => obj.name == "HeatMapCreator")];
            if (this.user == undefined || this.user.resourceAccess == undefined) return false;
            else {
              return this.$keycloak.hasAccess(
                this.user.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles,
                route.meta.roles,
              );
            }
          },
        },
      ];
    },
    administrationItems() {
      return [
        {
          // label: this.$t("menu.group_list"),
          label: this.$t("menu.users"),
          icon: "pi pi-fw pi-users",
          to: "/admin/users",
          command: () => {
            this.$router.push({ name: "Users" });
          },
          visible: () => {
            let route = this.$router.getRoutes()[this.$router.getRoutes().findIndex((obj) => obj.name == "Users")];
            if (this.user == undefined || this.user.resourceAccess == undefined) return false;
            else {
              return this.$keycloak.hasAccess(
                this.user.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles,
                route.meta.roles,
              );
            }
          },
        },
      ];
    },

    infrastructureItems() {
      return [
        {
          // label: this.$t("menu.group_list"),
          label: this.$t("menu.assets"),
          icon: "pi pi-fw pi-building",
          items: [
            {
              // label: this.$t("menu.group_list"),
              label: this.$t("menu.manage"),
              icon: "pi pi-fw pi-chevron-circle-right",
              to: "/management/asset",
              command: () => {
                this.$router.push({ name: "AssetList" });
              },
              visible: () => {
                let route =
                  this.$router.getRoutes()[this.$router.getRoutes().findIndex((obj) => obj.name == "AssetList")];
                if (this.user == undefined || this.user.resourceAccess == undefined) return false;
                else {
                  return this.$keycloak.hasAccess(
                    this.user.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles,
                    route.meta.roles,
                  );
                }
              },
            },
            {
              // label: this.$t("menu.group_list"),
              label: this.$t("menu.view"),
              icon: "pi pi-fw pi-chevron-circle-right",
              to: "/management/asset/view",
              command: () => {
                alert("todo:");
                // this.$router.push({ name: "Users" });
              },
              visible: () => {
                // NOT IMPLEMENTED YET
              },
            },
          ],
          visible: function () {
            return this.items.some(function (item) {
              console.log(item.visible);
              return item.visible == undefined || item.visible();
            });
          },
        },
        {
          label: this.$t("menu.measurements"),
          icon: "pi pi-fw pi-chart-line",
          items: [
            {
              label: this.$t("menu.manage"),
              icon: "pi pi-fw pi-chevron-circle-right",
              to: "/management/measurement",
              command: () => {
                this.$router.push({ name: "MeasurementList" });
              },
              visible: () => {
                let route =
                  this.$router.getRoutes()[this.$router.getRoutes().findIndex((obj) => obj.name == "MeasurementList")];
                if (this.user == undefined || this.user.resourceAccess == undefined) return false;
                else {
                  return this.$keycloak.hasAccess(
                    this.user.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles,
                    route.meta.roles,
                  );
                }
              },
            },
            {
              label: this.$t("menu.view"),
              icon: "pi pi-fw pi-chevron-circle-right",
              to: "/management/measurement/view",
              command: () => {
                alert("todo:");
                // this.$router.push({ name: "Users" });
              },
              visible: () => {
                // NOT IMPLEMENTED YET
              },
            },
          ],
          visible: function () {
            return this.items.some(function (item) {
              console.log(item.visible);
              return item.visible == undefined || item.visible();
            });
          },
        },
        {
          label: this.$t("menu.users"),
          icon: "pi pi-fw pi-users",
          command: () => {
            alert("todo:");
            // this.$router.push({ name: "Users" });
          },
          visible: () => {
            // NOT IMPLEMENTED YET
          },
        },
      ];
    },
    initMenu() {
      return [
        {
          label: this.$t("menu.dashboards"),
          icon: "pi pi-fw pi-chart-line",
          items: this.dashboardItems(),
          visible: () => {
            let to = this.$router.getRoutes()[this.$router.getRoutes().findIndex((obj) => obj.name == "Dashboard")];
            if (this.user == undefined || this.user.resourceAccess == undefined) return false;
            else {
              return this.$keycloak.hasAccess(
                this.user.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles,
                to.meta.roles,
              );
            }
          },
        },
        {
          label: this.$t("menu.information_panel"),
          icon: "pi pi-fw pi-chart-line",
          items: this.panelItems(),
          visible: function () {
            return this.items.some(function (item) {
              return item.visible == undefined || item.visible();
            });
          },
        },
        {
          label: this.$t("menu.heatmaps"),
          icon: "pi pi-fw pi-chart-line",
          items: this.heatMapItems(),
          visible: function () {
            return this.items.some(function (item) {
              return item.visible == undefined || item.visible();
            });
          },
        },
        {
          label: this.$t("menu.infrastructure"),
          icon: "pi pi-fw pi-lock",
          items: this.infrastructureItems(),
          visible: function () {
            return this.items.some(function (item) {
              return item.visible == undefined || item.visible();
            });
          },
        },
        {
          label: this.$t("menu.administration"),
          icon: "pi pi-fw pi-lock",
          items: this.administrationItems(),
          visible: function () {
            return this.items.some(function (item) {
              console.log(item.visible);
              return item.visible == undefined || item.visible();
            });
          },
        },
        {
          label: this.$t("menu.profile"),
          icon: "pi pi-fw pi-user",
          to: "/profile",
          command: () => {
            this.$router.push("/profile");
          },
          visible: () => {
            let to = this.$router.getRoutes()[this.$router.getRoutes().findIndex((obj) => obj.name == "Profile")];
            if (this.user == undefined || this.user.resourceAccess == undefined) return false;
            else {
              return this.$keycloak.hasAccess(
                this.user.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles,
                to.meta.roles,
              );
            }
          },
        },
        {
          label: this.$t("menu.feedback"),
          icon: "pi pi-fw pi-check-square",
          command: () => {
            alert("TODO: user feedback");
            // this.$router.push("/feedback");
          },
        },

        {
          label: this.$t("menu.notifications"),
          icon: "pi pi-fw  pi-bell",
          command: () => {
            // this.$emit("notification");
            this.notifications = !this.notifications;
          },
        },
        {
          label: this.$t("menu.login"),
          icon: "pi pi-sign-in",
          visible: () => !this.user.authenticated,
          command: async () => {
            this.user.login();
          },
        },
        {
          label: this.$t("menu.signup"),
          icon: "pi pi-sign-in",
          to: "/signup",
          visible: () => !this.user.authenticated,
          command: () => {
            this.$router.push({ name: "SignUp" });
          },
        },
        {
          label: this.$t("menu.logout"),
          icon: "pi pi-sign-out",
          to: "/",
          visible: () => this.user.authenticated,
          command: () => {
            this.$keycloak.logout();
            this.$router.push("/");
          },
        },
      ];
    },
    toggle(event) {
      this.$refs.userPopup.toggle(event);
    },
    logout: function () {
      this.$ren.rest.auth.logout();
      this.$router.push({ path: "/login" });
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
#sideMenuButton {
  width: $sidemenu-button-width;
  height: $sidemenu-button-width;
  font-size: $sidemenu-button-width;
  position: fixed;
  top: 0.2rem;
  left: 0.2rem;
  z-index: 1001;
}
#sideCloseMenuButton {
  position: absolute;
  top: 0.5rem;
  left: 0.5rem;
  font-size: 2.5rem;
  height: 2.5rem;
  width: 2.5rem;
}
</style>
