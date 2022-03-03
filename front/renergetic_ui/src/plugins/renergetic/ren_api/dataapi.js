import RestComponent from "./restcomponent";
export default class DataApi extends RestComponent {
  constructor(axiosInstance, vueInstance) {
    super(axiosInstance, vueInstance);
  }

  // INFORMATION PANEL REQUESTS

  //TODO: discuss with  others
  // async attributes(/*area, areaId*/) {}

  // async getTimeseries(measurementIds) {   } ./docs/model/timeseries.json , labels -> list of unix timestamps , data list of values for those timestamps
  // async getHeatMapState(heatmapid){} ./docs/model/heatmap_state.json , dictionary with states: area_id => state
  // async getCurrentData(measurementIds) {  } TODO:
  // async getPanelData(panelId) {} ./docs/model/panel_data.json , dictionary {"state":{panel_id:state},"data":{measurement_id:value}}
  // async getAssetData(assetId) {}   dictionary with current measurements associated with asset : measurement_id => value
  // async getHeatMapData(heatmapId) {}  dictionary with current measurements associated with heatmap: measurement_id => value
  // async getNotifications(objectIds) {} //TODO:
  // async getUserNotifications(userId) {} //TODO:
}
