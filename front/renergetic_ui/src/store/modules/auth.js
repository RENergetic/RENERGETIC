import { RenRoles } from "../../plugins/model/Enums";
export default {
  namespaced: true,
  state: {
    data: {
      //#region keyclock data:
      authenticated: false,
      appRoles: null,
      accountRoles: null,
      realmRoles: null,
      //#endregion
      //role mask
      renRole: RenRoles.REN_GUEST,
    },
    // role:null,
  },
  mutations: {
    reset(state) {
      console.info("reset auth");
      state.data = {
        authenticated: false,
        appRoles: null,
        accountRoles: null,
        realmRoles: null,
        renRole: RenRoles.REN_GUEST,
      };
    },
    /**
     * set keyclock auth details
     * @param {*} state
     * @param {*} payload -> keyclock object
     */
    set(state, payload) {
      console.info("set auth");
      if (payload != null && payload.appRoles != null) {
        let roles = payload.appRoles[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles;
        let role = RenRoles.REN_GUEST;
        roles.forEach((it) => {
          role = role | RenRoles[it];
        });
        payload.renRole = role;
      }
      state.data = payload;
    },
  },
  getters: {
    current: (state /* getters*/) => {
      return state.data;
    },
    /**
     * return role mask
     * @param {*} state
     * @returns
     */
    renRole: (state /* getters*/) => {
      try {
        if (state.data.renRole) return state.data.renRole;
        return RenRoles.REN_GUEST;
      } catch (error) {
        return RenRoles.REN_GUEST;
      }
    },
  },
};
