import RenUtils from "./renergetic/utils";
import RestClient from "./renergetic/rest";
import createKeyCloak from "./auth";
const plugin = {};

plugin.install = function (Vue /*, options*/) {
  Vue.config.globalProperties.$keycloak = createKeyCloak(Vue);
  Vue.config.globalProperties.$ren = {
    utils: new RenUtils(Vue),
    ...new RestClient(Vue),
  };
  // Vue.config.globalProperties.$pluginLoaded = true;
};

export default plugin;
