function mapPanelId(objectArray) {
  let dict = objectArray.reduce((dict, el, index) => ((dict[el["id"]] = index), dict), {});
  return dict;
}
function mapAssetPanelId(objectArray) {
  let dict = objectArray.reduce(
    (dict, el, index) => ((dict[el["panel"]["id"] + "_" + el["asset"]["id"]] = index), dict),
    {},
  );
  return dict;
}
export default {
  namespaced: true,
  state: {
    // graphana dashboards
    dashboards: [],
    dashboardMap: {},
    // list of information panel visible in the sidemenu
    informationPanels: [],
    informationPanelsMap: {},
    // list of featured user assets
    assets: [],
    assetsMap: {},
    // list of featured user assets
    assetPanels: [],
    assetPanelsMap: {},
    demands: [],
    data: [],
    locationList: ["en-EN"],
  },
  mutations: {
    wrapper(state, payload) {
      let getF = (key, defaultValue) => (payload[key] ? payload[key] : defaultValue);
      state.informationPanels = getF("panels", []);
      state.informationPanelsMap = mapPanelId(state.informationPanels);
      state.assets = getF("assets", []);
      state.assetsMap = mapPanelId(state.assets);
      // state.state =  getF("state",[]);
      state.data = getF("data", []);
      state.assetPanels = getF("asset_panels", []);
      state.assetPanelsMap = mapAssetPanelId(state.assetPanels);
      state.dashboards = getF("dashboards", []);
      state.dashboardMap = mapPanelId(state.dashboards);
      state.demands = getF("demands", []);
    },
    dashboards(state, payload) {
      // console.info(payload);
      state.dashboards = payload;
      state.dashboardMap = mapPanelId(payload);
    },
    informationPanels(state, payload) {
      state.informationPanels = payload;
      state.informationPanelsMap = mapPanelId(payload);
    },
    assets(state, payload) {
      state.assets = payload;
      state.assetsMap = mapPanelId(payload);
    },
    data(state, payload) {
      state.data = payload;
    },
    demands(state, payload) {
      state.demands = payload;
    },
    assetPanels(state, payload) {
      state.assetPanels = payload;
      state.assetPanelsMap = mapAssetPanelId(state.assetPanels);
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
    wrapper: (state) => {
      return state;
    },
    locationList: (state /* getters*/) => {
      return state.locationList;
    },
    dashboards: (state /* getters*/) => {
      return state.dashboards;
    },
    assets: (state /* getters*/) => {
      return state.assets;
    },
    demands: (state /* getters*/) => {
      return state.demands;
    },
    data: (state /* getters*/) => {
      return state.data;
    },
    assetPanels: (state /* getters*/) => {
      return state.assetPanels;
    },
    informationPanels: (state /* getters*/) => {
      return state.informationPanels;
    },
    assetPanelsMap: (state /* getters*/) => {
      return state.assetPanelsMap;
    },
    informationPanelsMap: (state /* getters*/) => {
      return state.informationPanelsMap;
    },
  },
};
