import Keycloak from 'keycloak-js'
import axios from 'axios'

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
        app.component("keycloak", keycloak);
    },
    logout(){
        keycloak.logout({ redirectUri: window.location.origin });
        localStorage.setItem('data',null);
    },
    async getUsers(){
        axios.defaults.headers.post['Access-Control-Allow-Origin'] = '*';
        axios.defaults.headers.post['Access-Control-Allow-Credentials'] = 'true';
        axios.defaults.headers.post['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS';
        axios.defaults.headers.post['Access-Control-Allow-Headers'] = 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type';

        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token },
            params: { clientId: appName } 
        };
        return axios.get(`http://localhost/auth/admin/realms/realm-renergetic/users`, config)
        .then(async (res) => {
            if(res.data && res.data.length > 0){
                let users = Array();
                for (const user of res.data){
                    users.push({
                        id: user.id,
                        username: user.username,
                        name: `${user.firstName && user.lastName?`${user.firstName} ${user.lastName}`: ''}`,
                        email: user.email,
                        roles: (await this.getUserRoles(user.id)).data
                    });
                }
                return users;
            }
            return undefined
        })
    },
    async getUserRoles(user_id){
        axios.defaults.headers.post['Access-Control-Allow-Origin'] = '*';
        axios.defaults.headers.post['Access-Control-Allow-Credentials'] = 'true';
        axios.defaults.headers.post['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS';
        axios.defaults.headers.post['Access-Control-Allow-Headers'] = 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type';

        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token }
        };
        let client_id = await this.getClientId();
        client_id = client_id.data[0].id;
        return axios.get(`http://localhost/auth/admin/realms/realm-renergetic/users/${user_id}/role-mappings/clients/${client_id}`, config);
    },
    async getClientId(){
        axios.defaults.headers.post['Access-Control-Allow-Origin'] = '*';
        axios.defaults.headers.post['Access-Control-Allow-Credentials'] = 'true';
        axios.defaults.headers.post['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS';
        axios.defaults.headers.post['Access-Control-Allow-Headers'] = 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type';

        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token }
        };

        return axios.get(`http://localhost/auth/admin/realms/realm-renergetic/clients?clientId=${appName}`, config);
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
    }
};