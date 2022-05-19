//EV charging module
var management = [
  {
    path: "/management/asset",
    name: "AssetList",
    props: {},
    meta: { isAuthenticated: true, roles: ["ren-admin", "ren-dev"] },
    component: () => import("../views/management/infrastructure/AssetListView.vue"),
  },
  {
    path: "/management/measurement",
    name: "MeasurementList",
    props: {},
    meta: { isAuthenticated: true, roles: ["ren-admin", "ren-dev"] },
    component: () => import("../views/management/infrastructure/MeasurementListView.vue"),
  },
];

export default management;
