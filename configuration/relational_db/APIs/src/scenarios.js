import { post, put , upperFirst} from "./utils.js";

export async function generate(scenario = undefined) {
    switch (scenario) {
        case undefined:
            console.warn("You should to specify a scenario");
            break;
        case "1":
            console.info("Generating scenario 1");
            await scenario1();
            console.info("Scenario 1 generated");
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
	
	var addRenewabilityF = async function (asset_id,domain){
		 let measurement = {"name": "renewability","label": "Renewability","type": 5,"domain": domain,"sensor_name": "renewability","asset_id": asset_id};
		 measurement.id = (await post(path, measurement)).id;
		 return measurement;
	}
	var renewability = [
	await addRenewabilityF(residences[0].id,"heat"),	await addRenewabilityF(residences[0].id,"electricity"),
	await addRenewabilityF(residences[1].id,"heat"),	await addRenewabilityF(residences[1].id,"electricity")
	]; 
	
	var addHeatF = async function (asset_id ){
		 let measurement = { "name": "heat_consumed", "label": "Heat Consumed" , "type": 7,  "domain": "heat",  "direction": "in",  "sensor_name": "thermostat", "asset_id":asset_id};
		 measurement.id = (await post(path, measurement)).id; return measurement;
	}
    var heatConsumed = [await addHeatF(residences[0].id), await addHeatF(residences[1].id),await addHeatF(building.id)];
 
	var addEnergyF = async function (asset_id,domain  ){
		 let type_id = domain=="heat"? 3:8;			 
		 let measurement = { "name": `${domain}_consumed`, "label": `${upperFirst(domain)} Consumed`,
		 "type": type_id,  "domain":domain, "direction": "in",  "sensor_name": "energy_meter", "asset_id":asset_id };
		 measurement.id = (await post(path, measurement)).id; return measurement;
	}
    var energyUsed = [
		await addEnergyF(residences[0].id,"heat"),await addEnergyF(residences[0].id,"electricity"),
		await addEnergyF(residences[1].id,"heat"),await addEnergyF(residences[1].id,"electricity"), 
		await addEnergyF(building.id,"heat"),await addEnergyF(building.id,"electricity")
	];
	var addEnergyProducedF = async function (asset_id,domain,suffix=""  ){
		suffix = suffix==null ? "": "_"+suffix
		 let type_id = domain=="heat"? 3:8;			 
		 let measurement = { "name": `${domain}_produced${suffix}`, "label": `${upperFirst(domain)} produced${suffix} `,
		 "type": type_id,  "domain":domain, "direction": "out",  "sensor_name": "energy_meter", "asset_id":asset_id };
		 measurement.id = (await post(path, measurement)).id; return measurement;
	}
    var energyProduced = [ 
		await addEnergyProducedF(building.id,"heat",null),await addEnergyProducedF(building.id,"heat","dirty"),await addEnergyProducedF(building.id,"heat","import"),
		await addEnergyProducedF(building.id,"electricity",null),
		await addEnergyProducedF(building.id,"electricity","dirty"),
		await addEnergyProducedF(building.id,"electricity","import")
	];
	
	var heatSupplyF = async function (asset_id ,sensor_name){
		 let measurement = { "name": "heat_consumed", "label": "Heat Consumed" , "type": 7,  "domain": "heat",  "direction": "in",  "sensor_name":sensor_name, "asset_id":asset_id};
		 measurement.id = (await post(path, measurement)).id; return measurement;
	}
    var heatSupply = [await heatSupplyF(energy[0].id,"gas_boiler"),await heatSupplyF( energy[1].id,"gas_boiler"),await heatSupplyF(energy[2].id,"solar_collector")];

 
    // Create tiles and panels
    path = "api/informationPanel";
	var addPanelF = async function (panelName,panelLabel,is_template){
		 let panel = { "name": panelName,  "label": panelLabel ,"is_template":is_template};
		 panel.id = (await post(path, panel)).id;
		 return panel;
	}
	 var panels = [ await addPanelF("panel1", "Panel 1",0),await addPanelF("renewability_panel", "Renewability panel",1),
	 await addPanelF("renewability_panel_template", "Renewability panel for {asset}",1) ];
	 
    path = "api/informationTile";
    const tile = {
        "name": "renewability",
        "label": "Renewability",
        "type": "knob",
        "layout": null,
        "panel_id": panels[0].id
    }
	var addTileF = async function ( panel_id,label,layout,type="single",props={icon_visibility: false}){
		 let tile = {  "panel_id":panel_id,  "label": label ,"type":type,"props":JSON.stringify(props),"layout":JSON.stringify(layout)};
		 tile.id = (await post(path, tile)).id;
		 return tile;
	}
	let tiles = [
	await addTileF(panels[2].id,"Total energy production of own usage",{ "x": 0, "y": 0, "w": 12,"h": 1},"single"  ),
	await addTileF(panels[2].id,"Electricity",{ "x": 0, "y": 1, "w": 5,"h": 4},"multi_knob" ,{ icon: "electricity"} ),
	await addTileF(panels[2].id,"Electricity",{ "x": 5, "y": 1, "w": 2,"h": 2},"single" ,{ icon: "electricity"}  ),
	await addTileF(panels[2].id,"Heat",{ "x": 5, "y": 3, "w": 5,"h": 2},"single" , { icon: "heat"} ),
	await addTileF(panels[2].id,"Heat" ,{ "x": 7, "y": 3, "w": 5,"h": 4},"multi_knob"  ,{ icon: "heat"} ),
	
	
	await addTileF(panels[1].id,"Total energy production of own usage",{ "x": 0, "y": 0, "w": 12,"h": 1},"single"  ),
	await addTileF(panels[1].id,"Electricity",{ "x": 0, "y": 1, "w": 5,"h": 4},"multi_knob" ,{ icon: "electricity"} ),
	await addTileF(panels[1].id,null,{ "x": 5, "y": 1, "w": 2,"h": 2},"single" ,{ } ),
	await addTileF(panels[1].id,null,{ "x": 5, "y": 3, "w": 5,"h": 2},"single" , ),
	await addTileF(panels[1].id, "Heat" ,{ "x": 7, "y": 3, "w": 5,"h": 4},"multi_knob"  ,{ icon: "heat"} ),
	]; 

	tile.id = (await post(path, tile)).id;
	path = "api/informationTileMeasurement";

 
    let tileMeasurements = [ 
	{"measurement_id": renewability[1].id,  "domain": null,     "direction": null, "measurement_type_id": null,  "sensor_name": null, "information_tile_id": tiles[0].id},
	{"measurement_id": energyProduced[3].id,  "domain": null,     "direction": null, "measurement_type_id": null,  "sensor_name": null, "information_tile_id": tiles[1].id},
	{"measurement_id": energyProduced[4].id,  "domain": null,     "direction": null, "measurement_type_id": null,  "sensor_name": null, "information_tile_id": tiles[1].id},
	{"measurement_id": energyProduced[5].id,  "domain": null,     "direction": null, "measurement_type_id": null,  "sensor_name": null, "information_tile_id": tiles[1].id},
	{"measurement_id": renewability[1].id,  "domain": null,     "direction": null, "measurement_type_id": null,  "sensor_name": null, "information_tile_id": tiles[2].id},
	{"measurement_id": renewability[0].id,  "domain": null,     "direction": null, "measurement_type_id": null,  "sensor_name": null, "information_tile_id": tiles[3].id},
	{"measurement_id": energyProduced[0].id,  "domain": null,     "direction": null, "measurement_type_id": null,  "sensor_name": null, "information_tile_id": tiles[4].id},
	{"measurement_id": energyProduced[1].id,  "domain": null,     "direction": null, "measurement_type_id": null,  "sensor_name": null, "information_tile_id": tiles[4].id},
	{"measurement_id": energyProduced[2].id,  "domain": null,     "direction": null, "measurement_type_id": null,  "sensor_name": null, "information_tile_id": tiles[4].id   },
	{
        "measurement_id": renewability[0].id,
        "domain": null,
        "direction": null,
        "measurement_type_id": null,
        "sensor_name": null,
        "information_tile_id": tile.id
    },{
        "measurement_id": renewability[1].id,
        "domain": null,
        "direction": null,
        "measurement_type_id": null,
        "sensor_name": null,
        "information_tile_id": tile.id
    }];
    for (let i = 0; i < tileMeasurements.length; i++) {
        tileMeasurements[i].id = (await post(path, tileMeasurements[i])).id;
    }

    // Connect asset with panels
    path = `api/informationPanel/connect?panel_id=${panels[2].id}&asset_id=${residences[0].id}`;
    put(path);
    path = `api/informationPanel/connect?panel_id=${panels[2].id}&asset_id=${residences[1].id}`;
    put(path);
    path = `api/informationPanel/connect?panel_id=${panels[1].id}&asset_id=${residences[0].id}`;
    put(path);
    path = `api/informationPanel/connect?panel_id=${panels[1].id}&asset_id=${residences[1].id}`;
    put(path);
    path = `api/informationPanel/connect?panel_id=${panels[1].id}&asset_id=${building.id}`;
    put(path);
    path = `api/informationPanel/connect?panel_id=${panels[1].id}&asset_id=${building.id}`;
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
        "asset":{"id": residences[0].id},
        "demand_definition": {
          "id": definitions[0].id
        },
        "demand_start": now.getTime(),
        "demand_stop": (new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours() + 1, now.getMinutes(), now.getSeconds())).getTime(),
        "demand_update": now.getTime()
    };
    schedules[1] = {
        "asset":{"id":  residences[1].id},
        "demand_definition": {
          "id": definitions[1].id
        },
        "demand_start": now.getTime(),
        "demand_stop": (new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours() + 1, now.getMinutes(), now.getSeconds())).getTime(),
        "demand_update": now.getTime()
    };
    schedules[2] = {
        "asset":{"id":  residences[1].id},
        "demand_definition": {
          "id": definitions[1].id
        },
        "demand_start": (new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours() - 2, now.getMinutes(), now.getSeconds())).getTime(),
        "demand_stop": (new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours() - 1, now.getMinutes(), now.getSeconds())).getTime(),
        "demand_update": now.getTime()
    };
    for (let i = 0; i < schedules.length; i++) {
        schedules[i].id = (await post(path, schedules[i])).id;
    }
}