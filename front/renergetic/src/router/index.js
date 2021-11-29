import { createRouter as createVueRouter, createWebHistory } from 'vue-router'
import Islands from "@/pages/Islands.vue";
import HeatDemand from "@/pages/HeatDemand.vue";
import Forbidden from "@/pages/Forbidden.vue";
import NotFound from "@/pages/NotFound.vue";

const createRoutes = () => [
  {
    path: "/",
    name: "Islands",
    component: Islands,
    meta: { requiresAuth: false },
  },
  {
    path: "/graphs",
    name: "HeatDemand",
    component: HeatDemand,
    meta: { requiresAuth: false },
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
        //const basePath = window.location.toString()               
        const authenticated = app.config.globalProperties.$keycloak?.authenticated
        if (to.meta.requiresAuth && !authenticated) {
            //next({ name: 'Login' })
            //keycloak.login({ redirectUri: basePath.slice(0, -1) + to.path })
        } else {
            next()
        }
    })
    return router
}