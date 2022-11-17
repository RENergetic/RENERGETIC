import { NotificationContext } from "@/plugins/model/Enums.js";
import dashboardList from "./samples/dashboard.js";
import informationPanelList from "./samples/informationpanels.js";
import heatmapList from "./samples/heatmap.js";
import notificationList from "./samples/notifications.js";
import demandList from "./samples/demand.js";
// import { measurementsGenerator, currentMeasurementsGenerator, dataGenerator } from "./dashboard.js";
import * as generator from "./samples/data_generator.js";
import assetList from "./samples/assets.js";
import assetPanelList from "./samples/asset_panels.js";
import measurementList from "./samples/measurement";
import storage from "./storage.js";
import measurement_types from "./samples/measurement_types.js";
// import measurementAttributes from "./samples/measurement_attributes.js";
import { AssetTypes } from "../../plugins/model/Enums.js";

const DASHBOARD_API_KEY = "dashboard_api";
const MANAGEMENT_API_KEY = "management_api";
const USER_API_KEY = "user_api";
// const DATA_API_KEY = "data_api";
const DASHBOARD_KEY = "dashboard";
const HEATMAP_KEY = "heatmap";
const PANEL_KEY = "panel";
const MEASUREMENT_KEY = "measurement";
const ASSET_KEY = "asset";
const SETTINGS_KEY = "settings";

//init default
async function initDummy() {
  console.info("Init dummy data");
  var measurementTypes = Object.assign({}, ...measurement_types.map((x) => ({ [x.id]: x })));
  measurementList.map((it) => (it.type = measurementTypes[it.metric_type_id]));

  // console.info(measurementList);
  await storage.setDefault(`${DASHBOARD_API_KEY}.${DASHBOARD_KEY}`, dashboardList);
  await storage.setDefault(`${DASHBOARD_API_KEY}.${PANEL_KEY}`, informationPanelList);
  await storage.setDefault(`${DASHBOARD_API_KEY}.${HEATMAP_KEY}`, heatmapList);
  await storage.setDefault(`${MANAGEMENT_API_KEY}.${MEASUREMENT_KEY}`, measurementList);
  await storage.setDefault(`${MANAGEMENT_API_KEY}.${ASSET_KEY}`, assetList);
}
//TODO: temporaty example objectc

class ManagementApi {
  //Infrastructure  REQUESTS
  async listAsset(userId) {
    console.info(`listAsset for ${userId}`);
    let assets = await storage.get(`${MANAGEMENT_API_KEY}.${ASSET_KEY}`, assetList);

    for (let asset of assets) {
      asset.measurements = await Promise.all(
        asset.measurements.map(async (it) => {
          return this.getMeasurement(it.id);
        }),
      );
    }
    console.info(assets);
    return assets;
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
    let assets = await this.listAsset();

    let asset = assets.find((it) => it.id == id);
    await Promise.all(asset.measurements.map(async (it) => this.getMeasurement(it.id)));
    return asset;
  }
  async searchAsset(q) {
    let mQ = q.toLowerCase();
    let assets = await this.listAsset();
    let f = function (s) {
      return s.name.toLowerCase().includes(mQ) || (s.label != null && s.label.toLowerCase().includes(mQ));
    };
    assets = assets.filter((it) => f(it));
    for (let asset of assets) {
      asset.measurements = await Promise.all(
        asset.measurements.map(async (it) => {
          return this.getMeasurement(it.id);
        }),
      );
    }
    return assets;
  }

  async getDemand(assetId) {
    //TODO:
    console.info(assetId);
    return demandList;
  }
  async getNotifications(assetId, context = NotificationContext.USER) {
    //TODO:
    console.info(assetId + " " + context);
    return notificationList;
  }
  async deleteAsset(id) {
    return new Promise((resolve) => {
      resolve(id);
    });
  }
  async listMeasurement(userId) {
    console.info(`listMeasurement for user:${userId}`);
    return await storage.get(`${MANAGEMENT_API_KEY}.${MEASUREMENT_KEY}`, measurementList);
  }

  async updateMeasurement(measurement) {
    storage.updateList(`${DASHBOARD_API_KEY}.${MEASUREMENT_KEY}`, measurementList);
    return new Promise((resolve) => {
      resolve(measurement.id);
    });
  }
  async getMeasurement(id) {
    let maps = await this.listMeasurement();
    let res = maps.find((it) => it.id == id);
    return res;
  }
  async searchMeasurement(q) {
    let mQ = q.toLowerCase();
    let measurements = await this.listMeasurement();
    let f = function (s) {
      return s.name.toLowerCase().includes(mQ) || (s.label != null && s.label.toLowerCase().includes(mQ));
    };
    return measurements.filter((it) => f(it));
  }
}
class DashboardApi {
  managementApi = new ManagementApi();

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

  async getInformationPanel(panelId, assetId) {
    let panels = await storage.get(`${DASHBOARD_API_KEY}.${PANEL_KEY}`, informationPanelList);
    if (assetId) {
      let panel = panels.find((it) => it.id == panelId);
      // let panel = await panels.find((it) => it.id == -1);
      let asset = await this.managementApi.getAsset(assetId);
      let assetLabel = asset.label ? asset.label : asset.name;
      if (panel.label != null) {
        panel.label = panel.label.replace("{asset}", assetLabel);
      } else {
        panel.label = assetLabel;
      }
      return panel;
    }
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
  getDemand(userId) {
    //TODO:
    console.info(userId);
    return demandList;
  }
}

class DataApi {
  dashboardApi = new DashboardApi();
  managementApi = new ManagementApi();

  // //TODO: discuss with Raul
  // async attributes(/*area, areaId*/) {
  //   console.info(JSON.stringify(measurementAttributes));
  //   return measurementAttributes;
  //   // return storage.get(`${MANAGEMENT_KEY}.panel_list`, measurementAttributes);
  // }

  async getTimeseries(measurementIds, attributes = {}) {
    console.info(attributes);
    let timeseries = generator.generateTimeseries(measurementIds);
    console.info(JSON.stringify(timeseries));
    return timeseries;
  }
  async getPanelData(panelId) {
    let panel = await this.dashboardApi.getInformationPanel(panelId);
    return { data: generator.generatePanelData(panel), state: generator.generatePanelState(panel) };
  }
  // async getCurrentData(measurementIds) {
  //   return generator.
  // }

  async getHeatMapState(heatmapId) {
    let heatmap = await this.dashboardApi.getHeatMap(heatmapId);
    let state = generator.generateHeatMapState(heatmap);
    console.info(JSON.stringify(state));
    return state;
  }
  async getDemandData(demands, assetId, predictionWindow) {
    console.info(assetId);
    // let data = {
    //   data: generator.generateTileData(demand.tile, predictionWindow),
    // };
    let data = generator.generateDemandData(demands, predictionWindow);
    console.info(JSON.stringify(data));
    return data;
  }

  async getAssetData(assetId) {
    let asset = await this.managementApi.getAsset(assetId);
    return generator.getAssetData(asset);
  }

  // async getAssetsData(assetIds) {
  //   var res = {};
  //   for (var assetId of assetIds) {
  //     let asset = await this.managementApi.getAsset(assetId);
  //     res[assetId] = generator.getAssetData(asset);
  //   }
  //   return res;
  // }

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
  managementApi = new ManagementApi();

  async getDemand() {
    return demandList;
  }
  async setSettings(settings) {
    storage.update(`${USER_API_KEY}.${SETTINGS_KEY}`, settings);
  }
  async getSettings() {
    return storage.get(`${USER_API_KEY}.${SETTINGS_KEY}`, null);
  }

  async getAssets() {
    let assets = await this.managementApi.listAsset();
    return assets.filter((asset) => asset.type.name == AssetTypes.BUILDING);
  }

  async listAssetPanels(userId) {
    console.info(`assetPanelList for ${userId}`);
    let panels = await storage.get(`${USER_API_KEY}.${PANEL_KEY}`, assetPanelList);

    console.info(panels);
    return panels;
  }
  async listInformationPanel(userId = null) {
    console.info(`listInformationPanel for ${userId}`);
    return storage.get(`${DASHBOARD_API_KEY}.${PANEL_KEY}`, informationPanelList);
  }
}

class WrapperApi {
  dashboardApi = new DashboardApi();
  managementApi = new ManagementApi();
  userApi = new UserApi();
  dataApi = new DataApi();
  async get(query) {
    //tODO: merge data fields
    console.info("query: " + JSON.stringify(query));
    let calls = query["calls"];
    let res = {};
    if (calls["assets"]) {
      res["assets"] = await this.managementApi.listAsset();
    }
    if (calls["dashboards"]) {
      res["dashboards"] = await this.dashboardApi.list();
    }
    if (calls["data"]) {
      //do nothing
    }
    if (calls["demands"]) {
      res["demands"] = await this.userApi.getDemand();

      res["data"] = await this.dataApi.getDemandData(res["demands"]);
    }
    if (calls["panels"]) {
      let ap = await this.userApi.listAssetPanels();
      if (calls["assets"]) {
        res["asset_panels"] = ap;
      }
      let panelIds = ap.map((it) => it.panel.id);
      // console.info(panelIds);
      if (calls["panels"]["id"]) {
        res["data"] = await this.dataApi.getPanelData(calls["panels"]["id"]).then((resp) => {
          if (resp) return resp["data"];
          return null;
        });
        res["panels"] = [await this.managementApi.getInformationPanel(calls["panels"]["id"])];
      } else {
        res["panels"] = await this.userApi.listInformationPanel();
        res["panels"] = res["panels"].filter((it) => panelIds.includes(it.id));
        console.info(res["panels"]);
      }
    }
    console.info(JSON.stringify(res));
    return res;
  }
}

export { DashboardApi, ManagementApi, DataApi, UserApi, WrapperApi, initDummy };
