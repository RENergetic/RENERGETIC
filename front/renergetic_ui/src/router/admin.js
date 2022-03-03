var adminRoutes = [
  {
    path: "/admin/users",
    name: "Users",
    props: {},
    meta: { isAuthenticated: true },
    component: () => import("../views/admin/Users.vue"),
  },
];

export default adminRoutes;
