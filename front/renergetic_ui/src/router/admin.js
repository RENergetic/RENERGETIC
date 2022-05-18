var adminRoutes = [
  {
    path: "/admin/users",
    name: "Users",
    props: {},
    meta: { isAuthenticated: true, roles: ["ren-admin", "ren-dev"] },
    component: () => import("../views/admin/Users.vue"),
  },
];

export default adminRoutes;
