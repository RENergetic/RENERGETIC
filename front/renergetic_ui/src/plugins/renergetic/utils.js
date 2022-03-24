import { v4 as uuidv4 } from "uuid";
export default class RenUtils {
  vueInstance = null;
  host = document.location.origin;
  constructor(vueInstance) {
    this.vueInstance = vueInstance;
  }
  get app() {
    return this.vueInstance.config.globalProperties;
  }
  uuid() {
    return uuidv4();
  }
  /**
   * get default value if null/undefined
   */
  default(value, defaultValue = undefined) {
    return value !== null && value !== undefined ? value : defaultValue;
  }
  currentDate() {
    var today = new Date();
    var dd = String(today.getDate()).padStart(2, "0");
    var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0!
    var yyyy = today.getFullYear();
    today = mm + "/" + dd + "/" + yyyy + " " + today.getHours() + ":" + today.getMinutes();
    return today;
  }
  saveSettings(settingsKey, settings) {
    this.app.$store.commit(settingsKey, settings);
    let allSettings = this.app.$store.getters["settings/all"];
    this.app.$ren.userApi.setSettings(allSettings);
  }
  async reloadStore() {
    console.info("reload");
    this.reloadDashboard();
    this.app.$ren.dashboardApi.listInformationPanel().then((informationPanels) => {
      this.app.$store.commit("view/informationPanels", informationPanels);
    });
    this.app.$ren.userApi.getSettings().then((settings) => {
      this.app.$store.commit("settings/all", settings);
    });
  }
  async reloadDashboard() {
    return await this.app.$ren.dashboardApi.list().then((dashboards) => {
      this.app.$store.commit("view/dashboards", dashboards);
      return dashboards;
    });
  }
  parseUnixTimestamp(timestamp) {
    var ts = new Date(timestamp);
    var dd = String(ts.getDate()).padStart(2, "0");
    var MM = String(ts.getMonth() + 1).padStart(2, "0"); //January is 0!
    var yyyy = ts.getFullYear();
    var mm = ts.getMinutes();
    var hh = ts.getHours();
    if (mm < 10) mm = `0${mm}`;
    if (hh < 10) hh = `0${hh}`;
    let parsed = MM + "/" + dd + "/" + yyyy + " " + hh + ":" + mm;
    return parsed;
  }
}
