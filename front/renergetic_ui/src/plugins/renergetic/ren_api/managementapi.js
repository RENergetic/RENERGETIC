import RestComponent from "./restcomponent";
export default class ManagementApi extends RestComponent {
  constructor(axiosInstance, vueInstance) {
    super(axiosInstance, vueInstance);
  }
  // ASSET REQUESTS
  // TODO:
  // async listAsset(userId=null) { } ./docs/model/asset.json or src/assets/dummy/samples/assets.js
  // async addAsset(asset) { }
  // async updateAsset(asset) { }
  // async getAsset(id) { }
  // async deleteAsset(id) { }
  // async getDemand(assetId) {} ./docs/model/demand.json

  // MEASUREMENT REQUESTS
  // TODO:
  // async listMeasurement(userId=null) {}  ./docs/model/measurement.json  or src/assets/dummy/samples/measurement.js
  // async updateMeasurement(measurement) {} -> only allow to update labels ,  color, and key-value properties
  // async getMeasurement(id) {}
  //
}
