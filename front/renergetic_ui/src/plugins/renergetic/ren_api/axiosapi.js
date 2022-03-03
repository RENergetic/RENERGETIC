export default class AxiosAPI {
  constructor(axiosInstance, vueInstance, authApi) {
    this.axios = axiosInstance;
    this.vueInstance = vueInstance;
    this.authApi = authApi;
    this.init();
  }
  //wrapper for store
  storeCommit(action) {
    this.vueInstance.config.globalProperties.$store.commit(action);
  }
  //wrapper for i18n
  $t(msgCode) {
    // return i18n.global.t("error." + msgCode);
    return this.vueInstance.config.globalProperties.$t("error." + msgCode);
  }
  //wrapper for global toast service
  toastError(code = "rest_error") {
    this.vueInstance.config.globalProperties.$toast.add({
      severity: "error",
      summary: this.$t("error_title"),
      detail: this.$t(code),
      life: 30000,
    });
  }

  /**
   * init axxios interceptors
   */
  init() {
    this.axios.interceptors.request.use(
      (req) => {
        if (req.spinner) {
          this.storeCommit("spinner/start");
        }
        req.withCredentials = false;
        console.log(`${req.method} ${req.url} ${req.spinner}`);
        // Important: request interceptors **must** return the request.
        return req;
      },
      (error) => {
        console.error("Request error: " + error.message);
        if (error.config.spinner) {
          this.storeCommit("spinner/stop");
        }
        this.toastError("connection_error");
      },
    );

    this.axios.interceptors.response.use(
      (res) => {
        if (res.config.spinner) {
          this.storeCommit("spinner/stop");
        }
        // Important: response interceptors **must** return the response.
        return res;
      },
      (error) => {
        console.error("Response error: " + error.message);
        if (error.config.spinner) {
          this.storeCommit("spinner/stop");
        }
        return this._onRequestError(error);
      },
    );
  }

  async _onRequestError(error) {
    const origReq = error.config;
    var response = error.response;
    if (error.message == "Network Error") {
      this.toastError("connection_error");
      return Promise.reject(error);
    } else if (response.status == 403) {
      console.error(`REST ${origReq.url}:  code=${response.status}: msg=${error.message}`);
      error.message = "PRIVILEGE_ERROR";
      return Promise.reject(error);
    } else if (response.status == 401) {
      this.toastError("AUTHORIZATION_ERROR");
    } else {
      console.error(`REST ${origReq.url}:  code=${response.status}`);
      return Promise.reject(error);
    }
  }
}
