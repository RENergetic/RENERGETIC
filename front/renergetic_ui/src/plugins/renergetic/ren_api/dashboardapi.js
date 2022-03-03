import RestComponent from "./restcomponent";
export default class DashboardApi extends RestComponent {
  constructor(axiosInstance, vueInstance) {
    super(axiosInstance, vueInstance);
  }
  //DASHBOARDS REQUESTS
  list() {
    // TODO:
    return this.axios
      .get(`/api-postgre/1.0/api/dashboard`, {
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
      .get(`/api-postgre/1.0/api/dashboard/test/${id}`, {
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
      .post(`/api-postgre/1.0/api/dashboard`, dashboard, {
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
      .put(`/api-postgre/1.0/api/dashboard`, dashboard, {
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
      .delete(`/api-postgre/1.0/api/dashboard/${id}`, {
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
  // async listHeatMap(userId=null) { }  src/assets/dummy/samples/heatmap.js
  // async getHeatMap(id) { }
  // async addHeatMap(heatmap) { }
  // async updateHeatMap(heatmap) { }
  // async deleteHeatMap(id) { }
  // async getDemand(areaId) {} ./docs/model/demand.json
}
