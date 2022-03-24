import RestComponent from "./restcomponent";
import storage from "../../../assets/dummy/storage.js";
import informationPanelList from "../../../assets/dummy/samples/informationpanels.js";
import heatmapList from "../../../assets/dummy/samples/heatmap.js";
import demandList from "../../../assets/dummy/samples/demand.js";
const DASHBOARD_API_KEY = "dashboard_api";
const HEATMAP_KEY = "heatmap";
const PANEL_KEY = "panel";
export default class DashboardApi extends RestComponent {
  constructor(axiosInstance, vueInstance) {
    super(axiosInstance, vueInstance);
  }
  //DASHBOARDS REQUESTS
  list() {
    // TODO:
    return this.axios
      .get(`/api/dashboard`, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("list entity error" + error.message);
      });
  }
  get(id) {
    return this.axios
      .get(`/api/dashboard/test/${id}`, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("list entity error" + error.message);
      });
  }
  add(dashboard) {
    //validate
    // TODO:
    return this.axios
      .post(`/api/dashboard`, dashboard, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("add entity error" + error.message);
      });
  }
  update(dashboard) {
    //validate
    // TODO:
    return this.axios
      .put(`/api/dashboard`, dashboard, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("add entity error" + error.message);
      });
  }
  delete(id) {
    return this.axios
      .delete(`/api/dashboard/${id}`, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("delete entity error" + error.message);
      });
  }
  // INFORMATION PANEL REQUESTS

  //TODO:
  // listInformationPanel(userId=null,offset=0,limit=10) {} =>  src/assets/dummy/samples/information_panel.js
  // async getInformationPanel(panelId) {}
  // async addInformationPanel(panel) {}
  // async updateInformationPanel(panel) {}
  // async deleteInformationPanel(id) {}

  // HEATMAP REQUESTS
  //TODO:
  // async listHeatMap(userId=null,offset=0,limit=20) { }  src/assets/dummy/samples/heatmap.js
  // async getHeatMap(id) { }
  // async addHeatMap(heatmap) { }
  // async updateHeatMap(heatmap) { }
  // async deleteHeatMap(id) { }
  // This one may not be needed anymore and should use getDemand(assetId) in managementApi: async getDemand(areaId) {} ./docs/model/demand.json

  // TEMPORAL CHANGES TO CONNECT WITH BACKEND
  listInformationPanel(userId = null) {
    console.info(`listInformationPanel for ${userId}`);
    return storage.get(`${DASHBOARD_API_KEY}.${PANEL_KEY}`, informationPanelList);
  }

  async getInformationPanel(panelId) {
    let panels = await storage.get(`${DASHBOARD_API_KEY}.${PANEL_KEY}`, informationPanelList);
    return panels.find((it) => it.id == panelId);
  }
  async addInformationPanel(panel) {
    panel.id = Math.floor(Math.random() * 1500);
    storage.push(`${DASHBOARD_API_KEY}.${PANEL_KEY}`, panel);
    return new Promise((resolve) => {
      resolve(panel.id);
    });
  }
  async updateInformationPanel(panel) {
    // console.info(panel);
    // console.info(JSON.stringify(panel));
    storage.updateList(`${DASHBOARD_API_KEY}.${PANEL_KEY}`, panel);
    return new Promise((resolve) => {
      resolve(panel.id);
    });
  }
  async deleteInformationPanel(id) {
    return new Promise((resolve) => {
      resolve(id);
    });
  }

  async listHeatMap(userId = null) {
    console.info(`listHeatMap for ${userId}`);
    return storage.get(`${DASHBOARD_API_KEY}.${HEATMAP_KEY}`, heatmapList);
  }
  async getHeatMap(id) {
    let maps = await this.listHeatMap();
    return maps.find((it) => it.id == id);
  }

  async addHeatMap(heatmap) {
    heatmap.id = Math.floor(Math.random() * 150);
    storage.push(`${DASHBOARD_API_KEY}.${HEATMAP_KEY}`, heatmap);
    return new Promise((resolve) => {
      resolve(heatmap.id);
    });
  }
  async updateHeatMap(heatmap) {
    storage.updateList(`${DASHBOARD_API_KEY}.${HEATMAP_KEY}`, heatmap);
    return new Promise((resolve) => {
      resolve(heatmap.id);
    });
  }
  async deleteHeatMap(id) {
    return new Promise((resolve) => {
      resolve(id);
    });
  }
  getDemand(heatmapId) {
    //TODO:
    console.info(heatmapId);
    return demandList;
  }
}
