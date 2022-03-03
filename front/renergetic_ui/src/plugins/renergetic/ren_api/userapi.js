import RestComponent from "./restcomponent";
export default class UserApi extends RestComponent {
  constructor(axiosInstance, vueInstance) {
    super(axiosInstance, vueInstance);
  }
  // TODO: implement
  // async getDemad(userId) {}

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
}
