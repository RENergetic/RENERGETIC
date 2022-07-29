
import { post } from "../utils.js";

const measurements = [
    {
        name: "renewability",
        type: 5,
        sensor_name: "renewability"
    },
    {
        name: "heat_pump",
        type: 3,
        sensor_name: "heat_pump"
    },
    {
        name: "energy_meter",
        type: 8,
        sensor_name: "energy_meter"
    },
    {
        name: "pv",
        type: 3,
        sensor_name: "pv",
    },
    {
        name: "battery",
        type: 8,
        sensor_name: "battery"
    },
    {
        name: "hot_water",
        type: 4,
        sensor_name: "hot_water"
    },
    {
        name: "cold_water",
        type: 4,
        sensor_name: "cold_water"
    },
    {
        name: "weather",
        type: 4,
        sensor_name: "weather"
    },
    {
        name: "heat_exchange",
        type: 3,
        sensor_name: "heat_exchange"
    },
    {
        name: "cooling_circuits",
        type: 3,
        sensor_name: "cooling_circuits"
    },
    {
        name: "chiller",
        type: 4,
        sensor_name: "chiller"
    },
    {
        name: "heat_meter",
        type: 4,
        sensor_name: "heat_meter"
    },
    {
        name: "dh_temperature",
        type: 4,
        sensor_name: "string"
    },
    {
        name: "dhw_temperature",
        type: 4,
        sensor_name: "string"
    },
    {
        name: "tapping_water",
        type: 3,
        sensor_name: "string"
    },
    {
        name: "thermostate",
        type: 4,
        sensor_name: "thermostate"
    },
    {
        name: "temperature",
        type: 4,
        sensor_name: "temperature"
    },
    {
        name: "cpu",
        type: 4,
        sensor_name: "cpu"
    }
];

export function generateMeasurements() {
    const path = "api/measurements"
    for (let measurement of measurements)
        post(path, measurement);
}

// TODO: There aren't path to add tags to database in HDR API
// export function generateTags() {
//     const path = "api/assets/type"
//     for (let tag of tags)
//         post(path, tag);
// }