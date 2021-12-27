import Keycloak from 'keycloak-js'
import axios from 'axios'

const appName = 'renergetic-app'
const initOptions = {url:'http://localhost/auth/', realm:'realm-renergetic', clientId: appName}
const keycloak = new Keycloak(initOptions)

export default {
    data: keycloak,
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
            localStorage.setItem('data',JSON.stringify(data))
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
        }).catch((error) => {
            console.warn(error.message);
            console.warn(`No se puede conectar a ${this.ip}`);
        });
    },
    async getUserRoles(user_id){
        axios.defaults.headers.post['Access-Control-Allow-Origin'] = '*';
        axios.defaults.headers.post['Access-Control-Allow-Credentials'] = 'true';
        axios.defaults.headers.post['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS';
        axios.defaults.headers.post['Access-Control-Allow-Headers'] = 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type';

        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token }
        };
        let client_id = await this.getClientID();
        client_id = client_id.data[0].id;
        return axios.get(`http://localhost/auth/admin/realms/realm-renergetic/users/${user_id}/role-mappings/clients/${client_id}`, config);
    },
    async getClientID(){
        axios.defaults.headers.post['Access-Control-Allow-Origin'] = '*';
        axios.defaults.headers.post['Access-Control-Allow-Credentials'] = 'true';
        axios.defaults.headers.post['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS';
        axios.defaults.headers.post['Access-Control-Allow-Headers'] = 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type';

        let config = {
            headers: { Authorization: "Bearer "+ keycloak.token }
        };

        return axios.get(`http://localhost/auth/admin/realms/realm-renergetic/clients?clientId=${appName}`, config);
    }
};