import Keycloak from 'keycloak-js'

const appName = 'renergetic-app'
const initOptions = {url:'http://localhost:8280/auth/', realm:'realm-renergetic', clientId: appName}
const keycloak = new Keycloak(initOptions)

export default {
    install(app) {
        keycloak.init({
            onLoad: 'login-required',
            //onLoad: 'check-sso',
            checkLoginIframe: false
        }).then( async(_authenticated) =>{
            app.config.globalProperties.authenticated = _authenticated;
            keycloak.authenticated = _authenticated ? true : false
            const data = {
              authenticated : keycloak.authenticated,
              appRoles : keycloak.resourceAccess[appName].roles,
              accountRoles : keycloak.resourceAccess.account.roles,
              realmRoles : keycloak.realmAccess.roles
            }
            //console.log(data);
            localStorage.setItem('data',JSON.stringify(data))
            //console.log(localStorage.getItem("data"));
        }).catch((e) => {
            console.log('failed to initialize ',e);
        });
        app.component("keycloak", keycloak);
  },
    logout(){
        keycloak.logout({ redirectUri: window.location.origin });
        localStorage.setItem('data',null);
  }
};