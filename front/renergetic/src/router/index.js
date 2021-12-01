import { createRouter as createVueRouter, createWebHistory } from 'vue-router'
import Islands from "@/pages/Islands.vue";
import HeatDemand from "@/pages/HeatDemand.vue";
import Forbidden from "@/pages/Forbidden.vue";
import NotFound from "@/pages/NotFound.vue";

const createRoutes = () => [
  {
    path: "/",
    name: "Home",
    component: Islands,
    meta: { requiresAuth: true },
  },
  {
    path: "/islands",
    name: "Islands",
    component: Islands,
    meta: { requiresAuth: true },
  },
  {
    path: "/graphs",
    name: "HeatDemand",
    component: HeatDemand,
    meta: { requiresAuth: true },
  },
  {
    path: "/forbidden",
    name: "Forbidden",
    component: Forbidden,
    meta: { requiresAuth: false },
  },
  {
    path: "/:catchAll(.*)",
    component: NotFound,
  },
];

export const createRouter = (app) => {
    const router = createVueRouter({
        history: createWebHistory(),
        routes: createRoutes(),
    })

    router.beforeEach((to, from, next) => {
        const basePath = window.location.toString()
        const $keycloak = app.component('keycloak')
        const data = JSON.parse(localStorage.getItem("data"))
        if (to.meta.requiresAuth) {
            if (!$keycloak.authenticated && data && !data.authenticated) {
              // The page is protected and the user is not authenticated. Force a login.
              $keycloak.login({ redirectUri: basePath.slice(0, -1) + to.path })
            /*} else if (data.appRoles.indexOf('manager')!==-1){ // hasResourceRole('manager')) {
              next()*/
            } else { // The user was authenticated, but did not have the correct role (is not authorized) redirect the user to an error page
              //next({ name: 'Forbidden' })
              next()
            }
        } else {
          if (!$keycloak.authenticated && data && !data.authenticated) {
            $keycloak.login({ redirectUri: window.location.origin })
          }else{
            next()
          }
        }
    })
    return router
}