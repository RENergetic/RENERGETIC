export default {
  namespaced: true,
  state: {
    data: {
      authenticated: false,
      appRoles: null,
      accountRoles: null,
      realmRoles: null,
    },
  },
  mutations: {
    set(state, payload) {
      // console.info(payload);
      state.data = payload;
    },
  },
  getters: {
    current: (state /* getters*/) => {
      return state.data;
    },
  },
};
