export default {
  namespaced: true,
  state: {
    //TODO: default home settings
    home: {},
    heatmap: {
      heatmapVisibility: true,
    },
    panel: {},
  },
  mutations: {
    home(state, payload) {
      state.home = payload;
    },
    panel(state, payload) {
      state.panel = payload;
    },
    heatmap(state, payload) {
      state.heatmap = payload;
    },
    toggle(state, payload) {
      state[payload.section][payload.key] = !state[payload.section][payload.key];
    },
  },
  getters: {
    home: (state /* getters*/) => {
      return state.home;
    },
    panel: (state /* getters*/) => {
      return state.panel;
    },
    heatmap: (state) => {
      return state.heatmap;
    },
  },
};
