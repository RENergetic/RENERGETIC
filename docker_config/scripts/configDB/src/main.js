
import { generateMeasurements } from "./other/influxRequirements.js";
import { generate  as generateScenario } from "./scenarios.js";
import { generateAssetTypes } from "./types/assetTypes.js";
import { generateMeasurementTypes } from "./types/measurementTypes.js";
import { filterArgs } from "./utils.js";

const args = filterArgs(process.argv);

console.log(args);

if(args.assetTypes != undefined && args.assetTypes.toLowerCase() == "true") 
    generateAssetTypes();
    
if(args.measurementTypes != undefined && args.measurementTypes.toLowerCase() == "true") 
    generateMeasurementTypes();

if(args.influxRequirements != undefined && args.influxRequirements.toLowerCase() == "true")
    generateMeasurements();

generateScenario(args.scenario);