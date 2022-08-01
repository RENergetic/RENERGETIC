
import { generateMeasurements, generateTags } from "./other/influxRequirements.js";
import { generate  as generateScenario } from "./scenarios.js";
import { generateAssetTypes } from "./types/assetTypes.js";
import { generateMeasurementTypes } from "./types/measurementTypes.js";
import { filterArgs } from "./utils.js";

const args = filterArgs(process.argv);

console.log(args);
main();

async function main() {
    if(args.assetTypes != undefined && args.assetTypes.toLowerCase() == "true") 
        await generateAssetTypes();
        
    if(args.measurementTypes != undefined && args.measurementTypes.toLowerCase() == "true") 
        await generateMeasurementTypes();

    if(args.influxRequirements != undefined && args.influxRequirements.toLowerCase() == "true") {
        await generateMeasurements();
        await generateTags();
    }

    generateScenario(args.scenario);
}