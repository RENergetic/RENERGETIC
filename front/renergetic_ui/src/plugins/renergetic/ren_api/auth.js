// import router from '../../../router';
import RestComponent from "./restcomponent";
export default class AuthApi extends RestComponent {
  constructor(axiosInstance, vueInstance) {
    super(axiosInstance, vueInstance);
  }

  canRefresh() {
    console.info("can refresh");
    var v = sessionStorage.getItem("REFRESH_COUNT");
    if (v == null) {
      v = 0;
      sessionStorage.setItem("REFRESH_COUNT", v);
    } else v = parseInt(v);
    console.info("can refresh: " + v);
    return v < 3;
  }

  onRefreshFail() {
    var v = sessionStorage.getItem("REFRESH_COUNT");
    if (v == null) {
      sessionStorage.setItem("REFRESH_COUNT", 1);
    } else {
      v = parseInt(v);
      sessionStorage.setItem("REFRESH_COUNT", v + 1);
    }
    console.info("on refresh fail:" + v);
  }
  /*async*/
  refresh() {
    //TODO: block other refresh requests
    //https://stackoverflow.com/questions/51563821/axios-interceptors-retry-original-request-and-access-original-promise
    // console.log("refresh 0");
    var onRefreshFail = this.onRefreshFail;
    if (!this.canRefresh()) {
      //TODO: make some message , toast?
      return Promise.reject(new Error("Too many refresh attempts"));
    }
    return this.axios
      .post("/api/auth/refresh")
      .then(() => {
        sessionStorage.setItem("REFRESH_COUNT", 0);
      })
      .catch(function () {
        onRefreshFail();
      });
  }
  // login(username, password) {
  //   console.info(username + password);
  // }
  // logout() {
  //   return this.axios
  //     .post("/api/auth/logout", null, {
  //       headers: {
  //         "Content-Type": "application/x-www-form-urlencoded",
  //       },
  //       spinner: true,
  //     })
  //     .then(() => {
  //       let nextRoute = this.$ren.utils.default(this.$router.from, {
  //         path: "/login",
  //       });
  //       this.vueInstance.config.globalProperties.$store.dispatch("resetStore");
  //       this.$router.push(nextRoute);
  //     })
  //     .catch(function (error) {
  //       console.log(error);
  //       this.vueInstance.config.globalProperties.$store.dispatch("resetStore");
  //       let nextRoute = this.$ren.utils.default(this.$router.from, {
  //         path: "/login",
  //       });
  //       // console.log(response);
  //       this.$router.push(nextRoute);
  //     });
  // }
}
