export default {
  namespaced: true,
  state: {
    dashboards: [],
    informationPanels: [],
    locationList: ["en-EN"],
  },
  mutations: {
    // Requires a Dashboards List
    dashboards(state, payload) {
      // console.info(payload);
      state.dashboards = payload;
    },
    informationPanels(state, payload) {
      state.informationPanels = payload;
    },
    // Requires a Dashboard object ({id:0, name:"", url:"", label:""})
    //TODO: i suggest to add single dashboard via REST API and than download new list - so we have coherent state with the backend.
    // Issue: connection error -> user creates dashboard and might lose it because there is no internet connection
    dashboardsAdd(state, payload) {
      //TODO: verify if there is no duplicate id
      let duplicated = false;
      for (let dashboard of state.dashboards) {
        if (dashboard.id != undefined && payload.id != undefined && dashboard.id == payload.id) duplicated = true;
      }
      if (!duplicated) state.dashboards.push(payload);
    },
    // Requires a Dashboard
    dashboardsUpdate(state, payload) {
      if (payload.id != undefined)
        for (let i = 0; i < state.dashboards.length; i++) {
          if (state.dashboards[i].id == payload.id) {
            state.dashboards.splice(i, 1);
            state.dashboards.push(payload);
          }
        }
    },
    // Requires a Dashboard Id
    dashboardsDel(state, payload) {
      for (let i = 0; i < state.dashboards.length; i++) {
        if (state.dashboards[i].id == payload) {
          state.dashboards.splice(i, 1);
        }
      }
    },
    locationList(state, payload) {
      // console.info(payload);
      state.locationList = payload;
    },
  },
  getters: {
    locationList: (state /* getters*/) => {
      return state.locationList;
    },
    dashboards: (state /* getters*/) => {
      return state.dashboards;
    },
    informationPanels: (state /* getters*/) => {
      return state.informationPanels;
    },
  },
};
