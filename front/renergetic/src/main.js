import { createApp } from 'vue'
import App from './App.vue'
import { createRouter } from './router'
import Keycloak from 'keycloak-js'

const app = createApp(App)
const router = createRouter(app)
app.use(router)

const initOptions = {url:'http://localhost:8280/auth/', realm:'realm-renergetic', clientId:'renergetic-app'}
const keycloak = new Keycloak(initOptions)

keycloak.init({
    //onLoad: 'login-required',
    onLoad: 'check-sso',
    checkLoginIframe: false
  }).then(async (auth) => {
      const basePath = window.location.toString()
      if (auth) {
          router.push(basePath)
          console.log("main "+keycloak)
      }else{
          keycloak.login()
      }
  }).catch((e) => {
    console.log('Serwer lezy: ' + e)
  })
  
  app.mount('#app')
