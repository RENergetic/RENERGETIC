import { RenRoles } from "@/plugins/model/Enums";
var dashboardRoutes = [
  {
    path: "/dashboard/view/:dashboard_id?",
    name: "Dashboard",
    props: {},
    meta: {
      isAuthenticated: true,
      layout: "fullscr",
      roles: ["ren-admin", "ren-dev", "ren-manager"],
      roleFlag: RenRoles.REN_MANAGER | RenRoles.REN_TECHNICAL_MANAGER | RenRoles.REN_ADMIN,
    },
    component: () => import("../views/dashboard/Dashboard.vue"),
  },
  // {
  //   path: "/dashboard/add",
  //   name: "DashboardAdd",
  //   props: {},
  //   meta: { isAuthenticated: true, roles: ["manager", "administrator"] },
  //   component: () => import("../views/dashboard/DashboardAdd.vue"),
  // },
  // {
  //   path: "/dashboard/edit/:dashboard_id?",
  //   name: "DashboardEdit",
  //   props: {},
  //   meta: { isAuthenticated: true },
  //   component: () => import("../views/dashboard/DashboardAdd.vue"),
  // },
  {
    path: "/dashboard/heatmap/add",
    name: "HeatMapCreator",
    props: {},
    meta: {
      isAuthenticated: true,
      roles: ["ren-admin", "ren-dev"],
      roleFlag: RenRoles.REN_TECHNICAL_MANAGER | RenRoles.REN_ADMIN,
    },

    component: () => import("../views/dashboard/HeatMapCreator.vue"),
  },
  {
    path: "/dashboard/heatmap/view/:id?",
    name: "HeatMapView",
    props: {},
    meta: {
      isAuthenticated: true,
      layout: "fullscr",
      roles: ["ren-admin", "ren-dev", "ren-manager"],
      roleFlag: RenRoles.REN_MANAGER | RenRoles.REN_TECHNICAL_MANAGER | RenRoles.REN_ADMIN,
    },
    component: () => import("../views/dashboard/HeatMap.vue"),
  },
  {
    path: "/dashboard/heatmap/edit/:id?",
    name: "HeatMapEditView",
    props: {},
    meta: {
      isAuthenticated: true,
      layout: "fullscr",
      roles: ["ren-admin", "ren-dev"],
      roleFlag: RenRoles.REN_TECHNICAL_MANAGER | RenRoles.REN_ADMIN,
    },
    component: () => import("../views/dashboard/HeatMapEditView.vue"),
  },
  {
    path: "/dashboard/heatmap/list",
    name: "HeatMapListView",
    props: {},
    meta: {
      isAuthenticated: true,
      roles: ["ren-admin", "ren-dev", "ren-manager"],
      roleFlag: RenRoles.REN_MANAGER | RenRoles.REN_TECHNICAL_MANAGER | RenRoles.REN_ADMIN,
    },
    component: () => import("../views/dashboard/HeatMapListView.vue"),
  },
  // {
  //   path: "/panel/view/:id?",
  //   name: "InformationPanel",
  //   meta: { isAuthenticated: true },
  //   component: () => import("../views/dashboard/InformationPanel.vue"),
  // },
  {
    path: "/panel/view/:id?",
    name: "InformationPanelView",
    meta: {
      isAuthenticated: true,
      layout: "fullscr",
      roles: ["ren-admin", "ren-dev", "ren-manager", "ren-user", "ren-visitor", "ren-technical-manager"],

      roleFlag:
        RenRoles.REN_USER |
        RenRoles.REN_VISITOR |
        RenRoles.REN_MANAGER |
        RenRoles.REN_TECHNICAL_MANAGER |
        RenRoles.REN_ADMIN |
        RenRoles.REN_STAFF,
    },
    component: () => import("../views/dashboard/InformationPanelView.vue"),
  },

  {
    path: "/asset/:asset_id/panel/:id?",
    name: "AssetPanelView",
    meta: {
      isAuthenticated: true,
      layout: "fullscr",
      roles: ["ren-admin", "ren-dev", "ren-manager", "ren-user", "ren-visitor", "ren-technical-manager"],
      roleFlag:
        RenRoles.REN_USER |
        RenRoles.REN_VISITOR |
        RenRoles.REN_MANAGER |
        RenRoles.REN_TECHNICAL_MANAGER |
        RenRoles.REN_ADMIN |
        RenRoles.REN_STAFF,
    },
    component: () => import("../views/dashboard/InformationPanelView.vue"),
  },
  {
    path: "/panel",
    name: "InformationPanelListView",
    meta: {
      isAuthenticated: true,
      roles: ["ren-admin", "ren-dev", "ren-manager", "ren-user"],
      roleFlag:
        RenRoles.REN_USER |
        RenRoles.REN_VISITOR |
        RenRoles.REN_MANAGER |
        RenRoles.REN_TECHNICAL_MANAGER |
        RenRoles.REN_ADMIN |
        RenRoles.REN_STAFF,
    },
    component: () => import("../views/dashboard/InformationPanelListView.vue"),
  },
  {
    path: "/panel/add",
    name: "InformationPanelCreator",
    meta: {
      isAuthenticated: true,
      roles: ["ren-admin", "ren-dev"],
      roleFlag: RenRoles.REN_USER | RenRoles.REN_TECHNICAL_MANAGER | RenRoles.REN_ADMIN,
    },
    component: () => import("../views/dashboard/InformationPanelCreator.vue"),
  },
];

export default dashboardRoutes;
