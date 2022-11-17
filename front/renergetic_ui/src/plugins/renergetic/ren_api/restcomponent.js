export default class RestComponent {
  //TODO: remove temmporary user id
  USER_ID = 2;
  constructor(axiosInstance, vueInstance) {
    this.vueInstance = vueInstance;
    this.axios = axiosInstance;
  }
  /**
   * convert dictionary to URL query string
   * @param {*} args
   * @returns query string
   */
  parseArgs(args = {}) {
    var q = "";
    if (!args || args.len == 0) {
      return "";
    }
    Object.keys(args).forEach(function (k) {
      let v = args[k];
      if (v !== undefined && v != null) {
        if (v instanceof Set || Array.isArray(v)) {
          v.forEach((item) => {
            q += `&${k}=${item}`;
          });
        } else {
          q += `&${k}=${v}`;
        }
      }
    });
    return q;
  }

  get $ren() {
    return this.vueInstance.config.globalProperties.$ren;
  }
  get $router() {
    return this.vueInstance.config.globalProperties.$router;
  }
}
