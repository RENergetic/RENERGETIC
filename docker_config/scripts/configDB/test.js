const { default: axios } = require("axios");

let type = {
    "name": "current_temperature",
    "label": "Current temperature",
    "type": 4,
  }
//35
//36
axios.put("http://front-ren-prototype.apps.paas-dev.psnc.pl/api-postgre/1.0/api/assets/measurement", type,
{
    headers: { "Content-type": "application/json; charset=UTF-8" },
    params: {"asset_id": 8, "measurement_id": 9}
}).then(response => console.log(response.data));