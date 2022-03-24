export default {
  namespaced: true,
  state: {
    //TODO: default home settings
    home: {
      selectedPanel: "607",
      feedbackVisibility: false,
      notificationVisibility: false,
      demandVisibility: true,
    },
    homeLayout: {
      // demandTile: { x: 8, y: 0, w: 4, h: 5 },
      // feedbackTile: { x: 4, y: 2, w: 1, h: 1 },
      // notificationTile: { x: 0, y: 5, w: 3, h: 2 },
      // panelTile: { x: 0, y: 0, w: 8, h: 5 },
      demandTile: { x: 0, y: 3, w: 6, h: 3 },
      feedbackTile: { x: 4, y: 2, w: 1, h: 1 },
      notificationTile: { x: 6, y: 3, w: 6, h: 3 },
      panelTile: { x: 0, y: 0, w: 12, h: 3 },
    },
    heatmap: {
      heatmapVisibility: true,
    },
    panel: {},
  },
  mutations: {
    home(state, payload) {
      state.home = payload;
    },
    homeLayout(state, payload) {
      state.homeLayout = payload;
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
    all(state, payload) {
      state = payload;
    },
  },
  getters: {
    home: (state /* getters*/) => {
      return state.home;
    },
    homeLayout: (state /* getters*/) => {
      return state.homeLayout;
    },
    panel: (state /* getters*/) => {
      return state.panel;
    },
    heatmap: (state) => {
      return state.heatmap;
    },
    all: (state) => {
      return state;
    },
  },
};
