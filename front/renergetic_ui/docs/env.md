Each key must start with 'VUE_APP_'
### Application
`VUE_APP_HOST` *str* - host

`VUE_APP_DUMMY_API` *bool* - use dummy data

### KEYCLOAK
`VUE_APP_KEY_CLOAK_URL` *str* - Keycloak host,e.g. http://localhost:8080/auth

`VUE_APP_KEY_CLOAK_REALM` *str* - Keycloak realm

`VUE_APP_KEY_CLOAK_CLIENT_ID` *str* - Keycloak client id

TODO: describe parameters

### RENERGETIC SERVICES
`VUE_APP_API_KEY` *str* -
`VUE_APP_API_URL` *str* - ren REST APIs host, e.g. http://front-ren-prototype.apps.paas-dev.psnc.pl
`VUE_APP_API_ISLANDS_PATH` *str* -/api-postgre/1.0/api/islands
`VUE_APP_API_DASHBOARD_PATH` *str* -/api-postgre/1.0/api/dashboard
`VUE_APP_API_BUILDINGS_PATH` *str* -/api-buildings/1.0/api/buildings

#### GRAFANA
VUE_APP_GRAFANA_URL *str* -http://grafana-ren-prototype.apps.paas-dev.psnc.pl/d-solo/{dashboard-id}/{dashboard-name}?orgId=1&from=1634427441815&to=1636724258711&theme=light&panelId={panel-id}