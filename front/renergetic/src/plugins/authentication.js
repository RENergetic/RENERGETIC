import Keycloak from 'keycloak-js'
import axios from 'axios';

const appName = process.env.VUE_APP_KEY_CLOAK_CLIENT_ID
const keycloakUrl = process.env.VUE_APP_KEY_CLOAK_URL
const realm =  process.env.VUE_APP_KEY_CLOAK_REALM
const initOptions = {url:keycloakUrl+'/auth/', realm: realm, clientId: appName}
const keycloak = new Keycloak(initOptions)

export default {
    data: keycloak,
    install(app,router) {
        keycloak.init({
            onLoad: 'login-required',
            //onLoad: 'check-sso',
            checkLoginIframe: false
        }).then( async(_authenticated) =>{
            app.config.globalProperties.authenticated = _authenticated;
            keycloak.authenticated = _authenticated ? true : false
            app.component("keycloak", keycloak);
            app.use(router).mount("#app")
        }).catch((e) => {
            console.log('failed to initialize ',e);
        });
    },
    getCliendId(){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token },
            params: { clientId: appName } 
        };
        return new Promise(function(resolve, reject) {
            axios.get(`${keycloakUrl}/auth/admin/realms/${realm}/clients`, config)
            .then((res) => {
                console.log("res clientid")
                console.log(res)
                if(res.data && res.data.length>0){
                    resolve(res.data[0].id)
                }
                reject(null)
            }).catch((error) => {
                console.warn(error.message);
                console.warn(`No se puede conectar a ${keycloakUrl}`);
                reject(error)
            });
        });
    },
    getUsers(){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token },
        };
        axios.get(`${keycloakUrl}/auth/admin/realms/${realm}/users`, config)
        .then((res) => {
            console.log("users")
            console.log(res)
            return res
        }).catch((error) => {
            console.warn(error.message);
            console.warn(`No se puede conectar a ${keycloakUrl}`);
            return null
        });
    },
    createUsers(body){
        let config = {
            headers: { 
                Authorization: "Bearer "+ keycloak.token,
                Accept: "*/*",
                'Content-Type':"application/json"
            },
        };
        
        axios.post(`${keycloakUrl}/auth/admin/realms/${realm}/users`, body,config)
        .then((res) => {
            console.log("user created")
            return res
        }).catch((error) => {
            console.warn(error.message);
            console.warn(`No se puede conectar a ${keycloakUrl}`);
            return null
        });
    },
    getRoles(clientId){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token },
        };
        axios.get(`${keycloakUrl}/auth/admin/realms/${realm}/clients/${clientId}/roles`, config)
        .then((res) => {
            console.log("roles")
            console.log(res)
            return res
        }).catch((error) => {
            console.warn(error.message);
            console.warn(`No se puede conectar a ${keycloakUrl}`);
        });
    },
    getUserAssignedRoles(userId,clientId){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token },
        };
        axios.get(`${keycloakUrl}/auth/admin/realms/${realm}/users/${userId}/role-mappings/clients/${clientId}`, config)
        .then((res) => {
            console.log("user roles")
            console.log(res)
            return res
        }).catch((error) => {
            console.warn(error.message);
            console.warn(`No se puede conectar a ${keycloakUrl}`);
        });
    },
    assignRolesToUser(userId,clientId,body){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token },
            Accept: "*/*",
            'Content-Type':"application/json"
        };
        axios.post(`${keycloakUrl}/auth/admin/realms/${realm}/users/${userId}/role-mappings/clients/${clientId}`, body,config)
        .then((res) => {
            console.log("user roles")
            console.log(res)
            return res
        }).catch((error) => {
            console.warn(error.message);
            console.warn(`No se puede conectar a ${keycloakUrl}`);
        });
    },
    logout(){
        keycloak.logout({ redirectUri: window.location.origin });
        localStorage.setItem('data',null);
    }
};