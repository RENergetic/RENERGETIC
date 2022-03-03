import { createStore } from "vuex";
import auth from "./modules/auth";
import view from "./modules/view";
import settings from "./modules/settings";
import createPersistedState from "vuex-persistedstate";
export default createStore({
  state: {},
  mutations: {},
  actions: {},
  modules: { auth, view, settings },

  plugins: [
    createPersistedState({
      paths: ["auth", "view", "settings"],
      storage: window.sessionStorage,
    }),
  ],
});
