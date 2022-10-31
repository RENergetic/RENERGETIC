//user notifications, user feedback, user engagement
var userRoutes = [
  {
    path: "/signup",
    name: "SignUp",
    meta: {
      isAuthenticated: false,
    },
    component: () => import("../views/user/SignUp.vue"),
  },
];

export default userRoutes;
