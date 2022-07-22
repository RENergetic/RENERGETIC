
import { generate  as generateScenario } from "./scenarios.js";
import { generateAssetTypes } from "./types/assetTypes.js";
import { generateMeasurementTypes } from "./types/measurementTypes.js";
import { filterArgs } from "./utils.js";

const args = filterArgs(process.argv);

if(args.assetTypes == undefined || args.assetTypes.toLowerCase() != "false") 
    generateAssetTypes();
    
if(args.measurementTypes == undefined || args.measurementTypes.toLowerCase() != "false") 
    generateMeasurementTypes();

generateScenario(args.scenario);