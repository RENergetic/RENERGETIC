import Keycloak from 'keycloak-js'
import axios from 'axios'
axios.defaults.headers.post['Access-Control-Allow-Origin'] = '*';
axios.defaults.headers.post['Access-Control-Allow-Credentials'] = 'true';
axios.defaults.headers.post['Access-Control-Allow-Methods'] = 'GET, POST, DELETE, OPTIONS';
axios.defaults.headers.post['Access-Control-Allow-Headers'] = 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type, Origin';
axios.defaults.headers.post['Content-Type'] = 'application/json';

axios.defaults.headers.delete['Access-Control-Allow-Origin'] = '*';
axios.defaults.headers.delete['Access-Control-Allow-Credentials'] = 'true';
axios.defaults.headers.delete['Access-Control-Allow-Methods'] = 'GET, POST, DELETE, OPTIONS';
axios.defaults.headers.delete['Access-Control-Allow-Headers'] = 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type, Origin';
axios.defaults.headers.delete['Content-Type'] = 'application/json';

let VUE_APP_KEY_CLOAK_URL = undefined;
if (process.env.VUE_APP_KEY_CLOAK_URL == undefined || process.env.VUE_APP_KEY_CLOAK_URL == '')
    VUE_APP_KEY_CLOAK_URL = `${location.origin}/auth`;
else VUE_APP_KEY_CLOAK_URL = process.env.VUE_APP_KEY_CLOAK_URL;

const initOptions = {
    url: VUE_APP_KEY_CLOAK_URL, 
    realm: process.env.VUE_APP_KEY_CLOAK_REALM, 
    clientId: process.env.VUE_APP_KEY_CLOAK_CLIENT_ID
}
const keycloak = new Keycloak(initOptions)

export default {
    data: keycloak,
    info: {
        app: process.env.VUE_APP_KEY_CLOAK_CLIENT_ID,
        realm: process.env.VUE_APP_KEY_CLOAK_REALM,
        url: VUE_APP_KEY_CLOAK_URL,
        clientId: undefined
    },
    async install(app, router) {
        keycloak.init({
            onLoad: 'login-required',
            //onLoad: 'check-sso',
            checkLoginIframe: false
        }).then( async(_authenticated) =>{
            app.config.globalProperties.authenticated = _authenticated;
            keycloak.authenticated = _authenticated ? true : false
            if(_authenticated && keycloak.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID] != undefined){
                for(let allowedRole of keycloak.resourceAccess[process.env.VUE_APP_KEY_CLOAK_CLIENT_ID].roles)
                    if(allowedRole.indexOf("administrator")!==-1)
                        this.info.clientId = (await this.getClientId()).data[0].id;
            }
            app.component("keycloak", keycloak);
            app.use(router).mount("#app");
        }).catch((e) => {
            console.log('failed to initialize ',e);
        });
        app.component("keycloak", keycloak);
    },
    logout(){
        keycloak.logout({ redirectUri: window.location.origin });
        localStorage.setItem('data', null);
    },
    async getClientRoles(){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token }
        };
        return axios.get(`${this.info.url}/admin/realms/${this.info.realm}/clients/${this.info.clientId}/roles`, config).then((res) => {
            return res.data
        }).catch((error) => {
            console.warn(error.message);
            console.warn(`No se puede conectar a ${this.info.url}`);
        });
    },
// KEYCLOAK API CALLS
//  Manage Clients Methods
    async getClientId(){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token }
        };

        return axios.get(`${this.info.url}/admin/realms/${this.info.realm}/clients?clientId=${this.info.app}`, config);
    },
//  Manage Users Methods
    async getUsers(){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token },
            params: { clientId: this.info.app } 
        };
        return axios.get(`${this.info.url}/admin/realms/${this.info.realm}/users`, config).then(async (res) => {
            
            if(res.data && res.data.length > 0){
                let users = Array();
                for (const user of res.data){
                    users.push({
                        id: user.id,
                        username: user.username,
                        name: `${user.firstName && user.lastName?`${user.firstName} ${user.lastName}`: ''}`,
                        email: user.email,
                        roles: await this.getUserRoles(user.id)
                    });
                }
                return users;
            }
        }).catch((error) => {
            console.warn(error.message);
            console.warn(`No se puede conectar a ${this.info.url}`);
        });
    },
    async getUserRoles(user_id){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token }
        };
        return axios.get(`${this.info.url}/admin/realms/${this.info.realm}/users/${user_id}/role-mappings/clients/${this.info.clientId}`, config).then((res) => {
            return res.data
        }).catch((error) => {
            console.warn(error.message);
            console.warn(`No se puede conectar a ${this.info.url}`);
        });
    },
    assignRolesToUser(userId, body){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token },
            Accept: "*/*",
            'Content-Type':"application/json"
        };
        axios.post(`${this.info.url}/admin/realms/${this.info.realm}/users/${userId}/role-mappings/clients/${this.info.clientId}`, body,config)
        .then((res) => {
            return res
        }).catch((error) => {
            console.warn(error.message);
            console.warn(`No se puede conectar a ${this.info.url}`);
        });
    },
    unAssignRolesToUser(userId, body){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token, 
            Accept: "*/*",
            'Content-Type':"application/json"},
            data: body
        };
        axios.delete(`${this.info.url}/admin/realms/${this.info.realm}/users/${userId}/role-mappings/clients/${this.info.clientId}`, config)
        .then((res) => {
            return res
        }).catch((error) => {
            console.warn(error.message);
            console.warn(`No se puede conectar a ${this.info.url}`);
        });
    },
    async createUser(body){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token },
            Accept: "*/*",
            'Content-Type':"application/json"
        };
        return axios.post(`${this.info.url}/admin/realms/${this.info.realm}/users`, body, config);
    },
    async deleteUser(userId){
        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token },
            Accept: "*/*",
            'Content-Type':"application/json"
        };
        return axios.delete(`${this.info.url}/admin/realms/${this.info.realm}/users/${userId}`, config);
    }
};