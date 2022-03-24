// import { is } from "core-js/core/object";
import RestComponent from "./restcomponent";
export default class ManagementApi extends RestComponent {
  constructor(axiosInstance, vueInstance) {
    super(axiosInstance, vueInstance);
  }
  //getNotifications(objectId=null,context="user") => context:NotificationContext -> Enums.js
  // ASSET REQUESTS
  // TODO:
  async listAsset(userId = null, offset = 0, limit = 20) {
    //TODO: is user id required here ?
    console.info(userId);
    return this.axios
      .get(`/api/assets`, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
        params: { offset: offset, limit: limit },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("list asset error" + error.message);
        if (error.response.status == 404) console.error("there are no assets" + error.message);
      });
  } //./docs/model/asset.json or src/assets/dummy/samples/assets.js

  async addAsset(asset) {
    if (asset.parent != undefined) asset.parent = asset.parent.id;
    return this.axios
      .post(`/api/assets`, asset, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("add asset error" + error.message);
      });
  }

  async updateAsset(asset) {
    if (asset.parent != undefined) asset.parent = asset.parent.id;
    return this.axios
      .put(`/api/assets/${asset.id}`, asset, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("update asset error" + error.message);
        if (error.response.status == 404) console.error("asset not found" + error.message);
      });
  }

  async getAsset(id) {
    return this.axios
      .get(`/api/assets/${id}`, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("list asset error" + error.message);
        if (error.response.status == 404) console.error("asset not found" + error.message);
      });
  }

  // async searchAsset(q,offset=0,limit=20){}

  async deleteAsset(id) {
    return this.axios
      .delete(`/api/assets/${id}`, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("delete asset error" + error.message);
        if (error.response.status == 404) console.error("asset not found" + error.message);
      });
  }

  async getDemand(assetId) {
    return this.axios
      .get(`/api/demandRequests/assetId/${assetId}`, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("retrieve demand request for assetId " + assetId + " error" + error.message);
        if (error.response.status == 404) console.error("asset with id " + assetId + " not found" + error.message);
      });
  } // ./docs/model/demand.json

  // MEASUREMENT REQUESTS
  // TODO:
  async listMeasurement(userId = null, offset = 0, limit = 20) {
    //TODO: is user id required here ?
    console.info(userId);
    return this.axios
      .get(`/api/measurements`, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
        params: { offset: offset, limit: limit },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("list measurement error" + error.message);
        if (error.response.status == 404) console.error("there are no measurements" + error.message);
      });
  } // ./docs/model/measurement.json  or src/assets/dummy/samples/measurement.js

  async addMeasurement(measurement) {
    if (measurement.type != undefined) measurement.type = measurement.type.id;
    return this.axios
      .post(`/api/measurements`, measurement, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("add measurement error" + error.message);
      });
  }

  async updateMeasurement(measurement) {
    if (measurement.type != undefined) measurement.type = measurement.type.id;
    return this.axios
      .put(`/api/measurements/${measurement.id}`, measurement, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("update measurement error" + error.message);
        if (error.response.status == 404) console.error("measurement not found" + error.message);
      });
  } // TODO: -> only allow to update labels ,  color, and key-value properties

  async getMeasurement(id) {
    return this.axios
      .get(`/api/measurements/${id}`, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("list measurement error" + error.message);
        if (error.response.status == 404) console.error("measurement not found" + error.message);
      });
  }
  // async searchMeasurement(q,assetId=null,offset=0,limit=20){}

  async deleteMeasurement(id) {
    return this.axios
      .delete(`/api/measurements/${id}`, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error("delete measurement error" + error.message);
        if (error.response.status == 404) console.error("measurement not found" + error.message);
      });
  }
  //
}
