import RestComponent from "./restcomponent";
import storage from "../../../assets/dummy/storage.js";
const USER_API_KEY = "user_api";
const SETTINGS_KEY = "settings";
export default class UserApi extends RestComponent {
  constructor(axiosInstance, vueInstance) {
    super(axiosInstance, vueInstance);
  }
  getProfile() {
    return this.axios
      .get(`/api/users/profile`, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error(error.response);
      });
  }
  /**
   * get panels assigned to assets
   * ./docs/model/asset_panel.json
   */
  listAssetPanels() {
    //TODO: API_INTEGRATION
  }
  getDemand() {
    //TODO: API_INTEGRATION
  }
  listInformationPanel(userId = null) {
    //TODO: API_INTEGRATION
    console.info(userId);
  }
  // TODO: implement
  // async getDemad(userId) {}
  // async setSettings(settings) {   }
  // async getSettings() {   }
  // async getAssets(){
  // todo:
  // }
  static loggedUser = undefined;
  register(user) {
    //validate
    // TODO:
    return this.axios
      .post(`/api-postgre/1.0/api/users/register`, user, {
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        return response.data;
      })
      .catch(function (error) {
        console.error(error.response);
      });
  }
  getByUsername(userName) {
    //validate
    // TODO:
    return this.axios
      .get(`/api-postgre/1.0/api/users/info/keycloak_name`, {
        params: { value: userName },
        headers: { "Content-type": "application/json; charset=UTF-8" },
      })
      .then((response) => {
        console.log(response);
        return response.data[0];
      })
      .catch(function (error) {
        console.error(error.response);
      });
  }
  async setSettings(settings) {
    storage.update(`${USER_API_KEY}.${SETTINGS_KEY}`, settings);
  }
  async getSettings() {
    return storage.get(`${USER_API_KEY}.${SETTINGS_KEY}`, null);
  }

  // async getNotifications(objectIds) {

  //   return objectIds;
  // }
}
