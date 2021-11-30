import { createApp } from 'vue'
import App from './App.vue'
import { createRouter } from './router'
import Keycloak from './plugins/authentication'

const app = createApp(App)
const router = createRouter(app)
app.use(Keycloak).use(router).mount("#app");