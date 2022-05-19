import AuthApi from "./ren_api/auth";
import DashboardApi from "./ren_api/dashboardapi";
import DataApi from "./ren_api/dataapi";
import ManagementApi from "./ren_api/managementapi";
import UserApi from "./ren_api/userapi";
import AxiosAPI from "./ren_api/axiosapi";
import {
  DashboardApi as DummyDashboardApi,
  ManagementApi as DummyManagementApi,
  DataApi as DummyDataApi,
  UserApi as DummyUserApi,
} from "../../assets/dummy/api";

// import UserApi from './renergetic/ren_api/user'
// import i18n from "../locale";
import axios from "axios";
// DON'T WORKS | For any reason get undefined from env file
//const USE_DUMMY = process.env.VUE_APP_DUMMY_API;
const USE_DUMMY = false;

export var BASE_URL = process.env.VUE_APP_API_URL;

const axiosInstance = axios.create({ baseURL: BASE_URL });

export default function createRest(vueInstance) {
  return {
    auth: new AuthApi(axiosInstance, vueInstance),
    axiosApi: new AxiosAPI(axiosInstance, vueInstance, this.auth),
    dashboardApi: !USE_DUMMY ? new DashboardApi(axiosInstance, vueInstance) : new DummyDashboardApi(),
    dataApi: !USE_DUMMY ? new DataApi(axiosInstance, vueInstance) : new DummyDataApi(),
    managementApi: !USE_DUMMY ? new ManagementApi(axiosInstance, vueInstance) : new DummyManagementApi(),
    userApi: !USE_DUMMY ? new UserApi(axiosInstance, vueInstance) : new DummyUserApi(),
  };
}
