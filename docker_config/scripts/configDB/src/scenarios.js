import { post, put } from "./utils.js";

export function generate(scenario = undefined) {
    switch (scenario) {
        case undefined:
            console.warn("You should to specify a scenario");
            break;
        case "1":
            console.info("Generating scenario 1");
            scenario1().then(console.info("Scenario 1 has been generated"));
            break;
        default:
            console.warn("The specified scenario doesn't exists");
    }
}

// https://gfi1-my.sharepoint.com/:x:/g/personal/raul_parra_inetum_com/EVKBAaRVaEFIk1jYt2DXhN8BKai4nD3vXjjwCSlpfZDHwA?e=9aXE5e
async function scenario1() {
    let path = "";

    // Create users
    const users = [];
    users[0] = {
        name: "user1",
        type: 18,
        label: "User 1",
        user: null
    };
    users[1] = {
        name: "user2",
        type: 18,
        label: "User 2",
        user: null
    };
    path = "api/users";
    users[0].user = (await post(path, {name: "user1"})).id;
    users[1].user = (await post(path, {name: "user2"})).id;

    path = "api/assets";
    for (let i = 0; i < users.length; i++) {
        users[i].id = (await post(path, users[i])).id;
    }

    // Create energy island
    let island = {
        name: "energy_island",
        type: 4,
        label: "Energy Island"
    };
    island.id = (await post(path, island)).id;

    // Create buildings
    let building = {
        name: "building2",
        type: 3,
        label: "Building 2",
        parent: island.id
    };
    building.id = (await post(path, building)).id;

    const residences = []
    residences[0] = {
        name: "flat1",
        type: 2,
        label: "Flat 1",
        parent: building.id
    };
    residences[1] = {
        name: "building1",
        type: 3,
        label: "Building 1",
        parent: island.id
    };
    for (let i = 0; i < residences.length; i++) {
        residences[i].id = (await post(path, residences[i])).id;
    }

    // Create energy assets
    const energy = []
    energy[0] = {
        name: "gas_boiler1",
        type: 7,
        label: "Gas Boiler 1"
    };
    energy[1] = {
        name: "gas_boiler2",
        type: 7,
        label: "Gas Boiler 2"
    };
    energy[2] = {
        name: "solar_collector1",
        type: 13,
        label: "Solar Collector 1"
    };
    for (let i = 0; i < energy.length; i++) {
        energy[i].id = (await post(path, energy[i])).id;
    }

    // Connect assets
    path = `api/assets/connect?asset_id=${users[0].id}&connected_asset_id=${residences[0].id}&type=owner`;
    put(path);
    path = `api/assets/connect?asset_id=${users[1].id}&connected_asset_id=${residences[0].id}&type=resident`;
    put(path);
    path = `api/assets/connect?asset_id=${users[1].id}&connected_asset_id=${residences[1].id}&type=owner`;
    put(path);
    
    path = `api/assets/connect?asset_id=${residences[0].id}&connected_asset_id=${energy[0].id}`;
    put(path);
    path = `api/assets/connect?asset_id=${residences[1].id}&connected_asset_id=${energy[1].id}`;
    put(path);
    path = `api/assets/connect?asset_id=${residences[1].id}&connected_asset_id=${energy[2].id}`;
    put(path);

    // Create measurements
    path = "api/measurements";
    const renewability = [];
    renewability[0] = {
        "name": "renewability",
        "label": "Renewability",
        "type": 5,
        "domain": "heat",
        "sensor_name": "renewability",
        "asset_id": residences[0].id
    };
    renewability[1] = {
        "name": "renewability",
        "label": "Renewability",
        "type": 5,
        "domain": "heat",
        "sensor_name": "renewability",
        "asset_id": residences[1].id
    };
    for (let i = 0; i < renewability.length; i++) {
        renewability[i].id = (await post(path, renewability[i])).id;
    }

    const heatConsumed = [];
    heatConsumed[0] = {
        "name": "heat_consumed",
        "label": "Heat Consumed",
        "type": 7,
        "domain": "heat",
        "direction": "in",
        "sensor_name": "thermostat",
        "asset_id": residences[0].id
    };
    heatConsumed[1] = {
        "name": "heat_consumed",
        "label": "Heat Consumed",
        "type": 7,
        "domain": "heat",
        "direction": "in",
        "sensor_name": "thermostat",
        "asset_id": residences[1].id
    };
    heatConsumed[2] = {
        "name": "heat_consumed",
        "label": "Heat Consumed",
        "type": 7,
        "domain": "heat",
        "direction": "in",
        "sensor_name": "thermostat",
        "asset_id": building.id
    };
    for (let i = 0; i < heatConsumed.length; i++) {
        heatConsumed[i].id = (await post(path, heatConsumed[i])).id;
    }

    const heatSupply = [];
    heatSupply[0] = {
        "name": "heat_supply",
        "label": "Heat Supply",
        "type": 7,
        "domain": "heat",
        "direction": "out",
        "sensor_name": "gas_boiler",
        "asset_id": energy[0].id
    };
    heatSupply[1] = {
        "name": "heat_supply",
        "label": "Heat Supply",
        "type": 7,
        "domain": "heat",
        "direction": "out",
        "sensor_name": "gas_boiler",
        "asset_id": energy[1].id
    };
    heatSupply[2] = {
        "name": "heat_supply",
        "label": "Heat Supply",
        "type": 7,
        "domain": "heat",
        "direction": "out",
        "sensor_name": "solar_collector",
        "asset_id": energy[2].id
    };
    for (let i = 0; i < heatSupply.length; i++) {
        heatSupply[i].id = (await post(path, heatSupply[i])).id;
    }

    // Create tiles and panels
    path = "api/informationPanel";
    let panel = {
        "name": "panel1",
        "label": "Panel 1"        
    };
    panel.id = (await post(path, panel)).id;

    path = "api/informationTile";
    const tile = {
        "name": "renewability",
        "label": "Renewability",
        "type": "knob",
        "layout": null,
        "panel_id": panel.id
    }
    tile.id = (await post(path, tile)).id;

    path = "api/informationTileMeasurement";
    const tileMeasurements = [];
    tileMeasurements[0] = {
        "measurement_id": renewability[0].id,
        "domain": null,
        "direction": null,
        "measurement_type_id": null,
        "sensor_name": null,
        "information_tile_id": tile.id
    };
    tileMeasurements[1] = {
        "measurement_id": renewability[1].id,
        "domain": null,
        "direction": null,
        "measurement_type_id": null,
        "sensor_name": null,
        "information_tile_id": tile.id
    };
    for (let i = 0; i < tileMeasurements.length; i++) {
        tileMeasurements[i].id = (await post(path, tileMeasurements[i])).id;
    }

    // Connect asset with panels
    path = `api/informationPanel/connect?panel_id=${panel.id}&asset_id=${residences[0].id}`;
    put(path);
    path = `api/informationPanel/connect?panel_id=${panel.id}&asset_id=${residences[1].id}`;
    put(path);

    // Creating user demands definitions
    path = "api/demandRequests/definition";
    const definitions = [];
    definitions[0] = {
      "action": "INCREASE_TEMPERATURE",
      "message": "Please, increase the temperature",
      "action_type": "INCREASE"
    };
    definitions[1] = {
      "action": "DECREASE_TEMPERATURE",
      "message": "Please, decrease the temperature",
      "action_type": "DECREASE"
    };
    for (let i = 0; i < definitions.length; i++) {
        definitions[i].id = (await post(path, definitions[i])).id;
    }

    // Creating user demands schedules
    path = "api/demandRequests";
    const schedules = [];
    let now = new Date();    
    //now = new Date(now.getTime() - now.getTimezoneOffset() * 60000)
    schedules[0] = {
        "asset_id": residences[0].id,
        "demand_definition": {
          "id": definitions[0].id
        },
        "demand_start": now.toISOString(),
        "demand_stop": (new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours() + 1, now.getMinutes(), now.getSeconds())).toISOString(),
        "demand_update": now.toISOString()
    };
    schedules[1] = {
        "asset_id": residences[1].id,
        "demand_definition": {
          "id": definitions[1].id
        },
        "demand_start": now.toISOString(),
        "demand_stop": (new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours() + 1, now.getMinutes(), now.getSeconds())).toISOString(),
        "demand_update": now.toISOString()
    };
    schedules[2] = {
        "asset_id": residences[1].id,
        "demand_definition": {
          "id": definitions[1].id
        },
        "demand_start": (new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours() - 2, now.getMinutes(), now.getSeconds())).toISOString(),
        "demand_stop": (new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours() - 1, now.getMinutes(), now.getSeconds())).toISOString(),
        "demand_update": now.toISOString()
    };
    for (let i = 0; i < schedules.length; i++) {
        schedules[i].id = (await post(path, schedules[i])).id;
    }
}