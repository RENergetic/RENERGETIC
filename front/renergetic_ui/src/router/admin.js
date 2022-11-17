import { RenRoles } from "@/plugins/model/Enums";
var adminRoutes = [
  {
    path: "/admin/users",
    name: "Users",
    props: {},
    meta: { isAuthenticated: true, roles: ["ren-admin"], roleFlag: RenRoles.REN_ADMIN },
    component: () => import("../views/admin/Users.vue"),
  },
];

export default adminRoutes;
