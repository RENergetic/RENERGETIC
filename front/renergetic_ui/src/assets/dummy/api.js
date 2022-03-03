import dashboardList from "./samples/dashboard.js";
import informationPanelList from "./samples/informationpanels.js";
import heatmapList from "./samples/heatmap.js";
import demandList from "./samples/demand.js";
// import { measurementsGenerator, currentMeasurementsGenerator, dataGenerator } from "./dashboard.js";
import * as generator from "./samples/data_generator.js";
import assetList from "./samples/assets.js";
import measurementList from "./samples/measurement";
import storage from "./storage.js";
const DASHBOARD_API_KEY = "dashboard_api";
const MANAGEMENT_API_KEY = "management_api";
// const DATA_API_KEY = "data_api";
const DASHBOARD_KEY = "dashboard";
const HEATMAP_KEY = "heatmap";
const PANEL_KEY = "panel";
const MEASUREMENT_KEY = "measurement";
const ASSET_KEY = "asset";

//init default
await storage.setDefault(`${DASHBOARD_API_KEY}.${DASHBOARD_KEY}`, dashboardList);
await storage.setDefault(`${DASHBOARD_API_KEY}.${PANEL_KEY}`, informationPanelList);
await storage.setDefault(`${DASHBOARD_API_KEY}.${HEATMAP_KEY}`, heatmapList);
await storage.setDefault(`${MANAGEMENT_API_KEY}.${MEASUREMENT_KEY}`, measurementList);
await storage.setDefault(`${MANAGEMENT_API_KEY}.${ASSET_KEY}`, assetList);

//TODO: temporaty example objectc
var measurementAttributes = [
  {
    key: "id4",
    label: "Predictors",
    name: "predictors",
    type: "group",
    children: [
      {
        key: "id5",
        name: "electrical",
        label: "Electrical",
        type: "measurement",
        children: [],
      },
      {
        key: "id6",
        name: "thermic",
        label: "Thermic",
        type: "measurement",
        children: [],
      },
    ],
  },
  {
    key: "id1",
    label: "Target",
    name: "target",
    type: "group",
    children: [
      {
        key: "id2",
        name: "electrical",
        label: "Electrical",
        type: "measurement",
        children: [],
      },
      {
        key: "id3",
        name: "thermic",
        label: "Thermic",
        type: "measurement",
        children: [],
      },
    ],
  },
  {
    key: "id7",
    label: "Prediction Interval",
    name: "prediction_interval",
    type: "group",
    children: [
      {
        key: "id8",
        name: "3h",
        label: "3 H",
        type: "measurement",
        children: [],
      },
      {
        key: "id9",
        name: "6h",
        label: "6 H",
        type: "measurement",
        children: [],
      },
    ],
  },
];

class DashboardApi {
  async list() {
    return storage.get(`${DASHBOARD_API_KEY}.${DASHBOARD_KEY}`, dashboardList);
  }
  async add(dashboard) {
    dashboard.id = Math.floor(Math.random() * 150);
    storage.push(`${DASHBOARD_API_KEY}.${DASHBOARD_KEY}`, dashboard);
    return new Promise((resolve) => {
      resolve(dashboard.id);
    });
  }
  async update(dashboard) {
    storage.updateList(`${DASHBOARD_API_KEY}.${DASHBOARD_KEY}`, dashboard);
    return new Promise((resolve) => {
      resolve(dashboard.id);
    });
  }
  delete(id) {
    return new Promise((resolve) => {
      resolve(id);
    });
  }

  listInformationPanel(userId = null) {
    console.info(`listInformationPanel for ${userId}`);
    return storage.get(`${DASHBOARD_API_KEY}.${PANEL_KEY}`, informationPanelList);
  }

  async getInformationPanel(panelId) {
    let panels = await storage.get(`${DASHBOARD_API_KEY}.${PANEL_KEY}`, informationPanelList);
    return panels.find((it) => it.id == panelId);
  }
  async addInformationPanel(panel) {
    panel.id = Math.floor(Math.random() * 150);
    storage.push(`${DASHBOARD_API_KEY}.${PANEL_KEY}`, panel);
    return new Promise((resolve) => {
      resolve(panel.id);
    });
  }
  async updateInformationPanel(panel) {
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
  getDemand(areaId) {
    //TODO:
    console.info(areaId);
    return demandList;
  }
}

class ManagementApi {
  //Infrastructure  REQUESTS
  async listAsset(userId) {
    console.info(`listAsset for ${userId}`);
    return storage.get(`${MANAGEMENT_API_KEY}.${ASSET_KEY}`, assetList);
  }

  async addAsset(asset) {
    asset.id = Math.floor(Math.random() * 150);
    storage.push(`${MANAGEMENT_API_KEY}.${ASSET_KEY}`, asset);
    return new Promise((resolve) => {
      resolve(asset.id);
    });
  }
  async updateAsset(asset) {
    storage.updateList(`${MANAGEMENT_API_KEY}.${ASSET_KEY}`, asset);
    return new Promise((resolve) => {
      resolve(asset.id);
    });
  }
  async getAsset(id) {
    let maps = await this.assetList();
    return maps.find((it) => it.id == id);
  }
  getDemand(assetId) {
    //TODO:
    console.info(assetId);
    return demandList;
  }
  async deleteAsset(id) {
    return new Promise((resolve) => {
      resolve(id);
    });
  }
  async listMeasurement(userId) {
    console.info(`listMeasurement for ${userId}`);
    return storage.get(`${MANAGEMENT_API_KEY}.${MEASUREMENT_KEY}`, measurementList);
  }

  async updateMeasurement(measurement) {
    storage.updateList(`${DASHBOARD_API_KEY}.${MEASUREMENT_KEY}`, measurementList);
    return new Promise((resolve) => {
      resolve(measurement.id);
    });
  }
  async getMeasurement(id) {
    let maps = await this.measurementList();
    return maps.find((it) => it.id == id);
  }
}
class DataApi {
  dashboardApi = new DashboardApi();
  managementApi = new ManagementApi();

  //TODO: discuss with Raul
  async attributes(/*area, areaId*/) {
    return measurementAttributes;
    // return storage.get(`${MANAGEMENT_KEY}.panel_list`, measurementAttributes);
  }

  async getTimeseries(measurementIds) {
    return generator.generateTimeseries(measurementIds);
  }

  // async getCurrentData(measurementIds) {
  //   return generator.
  // }

  async getHeatMapState(heatmapId) {
    let heatmap = await this.dashboardApi.getHeatMap(heatmapId);
    return generator.generateHeatMapState(heatmap);
  }
  async getPanelData(panelId) {
    let panel = await this.dashboardApi.getInformationPanel(panelId);
    return { data: generator.generatePanelData(panel), state: generator.generatePanelState(panel) };
  }
  async getAssetData(assetId) {
    let asset = await this.managementApi.getAsset(assetId);
    return generator.getAssetData(asset);
  }
  async getHeatMapData(heatmapid) {
    let heatmap = await this.dashboardApi.getHeatMap(heatmapid);
    return generator.getHeatMapData(heatmap);
  }
  async getNotifications(objectIds) {
    // todo:
    return objectIds;
  }
  async getUserNotifications(userId) {
    // todo:
    return userId;
  }
}

class UserApi {
  async getDemad(userId) {
    console.info(userId);
    return demandList;
  }
}
export { DashboardApi, ManagementApi, DataApi, UserApi };
