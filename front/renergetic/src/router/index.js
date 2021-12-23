import { createRouter as createVueRouter, createWebHistory } from 'vue-router'
import Login from "@/pages/Login.vue"
import Islands from "@/pages/Islands.vue";
import HeatDemand from "@/pages/HeatDemand.vue";
import Roles from "@/pages/Roles.vue";
import Forbidden from "@/pages/Forbidden.vue";
import NotFound from "@/pages/NotFound.vue";

const createRoutes = () => [
  {
    path: "/",
    name: "Home",
    component: Login,
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
    meta: { requiresAuth: true, roles:['manager', 'admin'] },
  },
  {
    path: "/roles",
    name: "Roles",
    component: Roles,
    meta: { requiresAuth: true, roles:['manager', 'admin'] },
  },
  {
    path: "/forbidden",
    name: "Forbidden",
    component: Forbidden,
  },
  {
    path: "/:catchAll(.*)",
    component: NotFound,
  },
];

function hasAccess(assignedRoles, allowedRoles){
  for(var i=0;i<allowedRoles.length;i++){
    if(assignedRoles.indexOf(allowedRoles[i])!==-1)
      return true
  }
  return false
}

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
            } else if (!to.meta.roles || (to.meta.roles && hasAccess(data.appRoles,to.meta.roles))){// hasResourceRole('manager')) {
              next()
            } else { // The user was authenticated, but did not have the correct role (is not authorized) redirect the user to an error page
              next({ name: 'Forbidden' })
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