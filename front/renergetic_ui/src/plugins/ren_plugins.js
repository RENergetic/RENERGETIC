import RenUtils from "./renergetic/utils";
import RestClient from "./renergetic/rest";
import createKeyCloak from "./auth";
const plugin = {};

plugin.install = function (Vue /*, options*/) {
  Vue.config.globalProperties.$keycloak = createKeyCloak(Vue);
  Vue.config.globalProperties.$ren = {
    utils: RenUtils,
    ...new RestClient(Vue),
  };
};

export default plugin;
