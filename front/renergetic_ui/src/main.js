import { createApp } from "vue";
import App from "./App.vue";
import createRouter from "./router";
import store from "./store";

import i18n from "./plugins/locale";
import PrimeVue from "primevue/config";
//import renergetic plugins
//renergetic plugins this.$ren.{plugin}
import plugin from "./plugins/ren_plugins";
//import primevue plugins
import PrimeImport from "./plugins/primevue_import";
import "primevue/resources/themes/saga-green/theme.css";
// theme showcase: https://www.primefaces.org/primevue/showcase/
import "primevue/resources/primevue.min.css";
import "primeicons/primeicons.css";
//https://www.primefaces.org/primevue/showcase/#/primeflex
import "primeflex/primeflex.css";
import "./assets/style/ren.scss";
import VueKonva from "vue-konva";

import Tooltip from "primevue/tooltip";
import ToastService from "primevue/toastservice";
// import PrimeLocale from './assets/locale/EN-en/primevue.json'
import ConfirmationService from "primevue/confirmationservice";
// import mitt from 'mitt'
// const emitter = mitt()

//initialize app

const app = createApp(App);
app.use(i18n);
//TODO: if normal user?
app.use(VueKonva);
// app.use(PrimeVue, { locale: PrimeLocale });
app.use(PrimeVue);
app.use(PrimeImport);

app.directive("tooltip", Tooltip);

app.use(ToastService);
app.use(ConfirmationService);
// app.config.globalProperties.emitter = emitter

app.use(store);
app.use(plugin);

app.use(createRouter(app));
app.mount("#app");
