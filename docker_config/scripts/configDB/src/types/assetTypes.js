
import { post } from "../utils.js";
const path = "api/assets/type"

const types = [
    {
        id: "1",
        name: "room",
        label: "Room",
        category: "structural",
        renovable: null
    },
    {
        id: "2",
        name: "flat",
        label: "Flat",
        category: "structural",
        renovable: null
    },
    {
        id: "3",
        name: "building",
        label: "Building",
        category: "structural",
        renovable: null
    },
    {
        id: "4",
        name: "energy_island",
        label: "Energy island",
        category: "structural",
        renovable: null
    },
    {
        id: "5",
        name: "generation_plant",
        label: "Generation plant",
        category: "energy",
        renovable: 50
    },
    {
        id: "6",
        name: "heat_pump",
        label: "Heat pump",
        category: "energy",
        renovable: 40
    },
    {
        id: "7",
        name: "gas_boiler",
        label: "Gas boiler",
        category: "energy",
        renovable: 0
    }, 
    {
        id: "8",
        name: "co-generation_unit",
        label: "Co-generation unit",
        category: "energy",
        renovable: 0
    }, 
    {
        id: "9",
        name: "coal_plant",
        label: "Coal plant",
        category: "energy",
        renovable: 0
    }, 
    {
        id: "10",
        name: "pv_plant",
        label: "PV plant",
        category: "energy",
        renovable: 100
    }, 
    {
        id: "11",
        name: "external_heat_grid",
        label: "External heat grid",
        category: "energy",
        renovable: null
    }, 
    {
        id: "12",
        name: "external_electricity_grid",
        label: "External electricity grid",
        category: "energy",
        renovable: null
    }, 
    {
        id: "13",
        name: "solar_thermal_collector",
        label: "Solar thermal collectro",
        category: "energy",
        renovable: 100
    }, 
    {
        id: "14",
        name: "steam",
        label: "Steam",
        category: "infrastructure",
        renovable: null
    }, 
    {
        id: "15",
        name: "district_heating",
        label: "District heating",
        category: "infrastructure",
        renovable: null
    }, 
    {
        id: "16",
        name: "district_cooling",
        label: "District cooling",
        category: "infrastructure",
        renovable: null
    }, 
    {
        id: "17",
        name: "electricity",
        label: "Electricity",
        category: "infrastructure",
        renovable: null
    }, 
    {
        id: "18",
        name: "user",
        label: "User",
        category: "infrastructure",
        renovable: null
    },
    {
        id: "19",
        name: "ev_charging_station",
        label: "Ev charging station",
        category: "energy",
        renovable: 100
    },
];

export function generateAssetTypes() {
    for (let type of types)
        post(path, type);
}