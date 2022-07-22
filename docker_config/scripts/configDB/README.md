# INTRODUCTION
Here you can find scripts to insert data in relational database using HDR API

This scripts allow to create:
 - **AssetTypes**: you can find the list of AssetTypes to create in [AssetTypes file](src/types/assetTypes.js)
 - **MeasurementTypes**: you can find the list of MeasurementTypes to create in [MeasurementTypes file](src/types/measurementTypes.js)
 - **Scenarios**: the elements that creates each scenario can be found in the schemas at [Scenarios folder](scenarios/)

To configure the API URL and other paramenters you can modify the [.env file](.env)

## HOW TO USE

*To use it you should have installed Node in your computer.* [Install it here](https://nodejs.org/en/download/)

The command to run the scripts is `npm generate`

It have three parameters:
 - --scenario: to set the scenario to create, if the scenario isn't exists show a message, if you call the command without this parameter no scenario is created
 - --assetTypes: setting it to *false* the asset types isn't created
 - --measurementTypes: setting it to *false* the measurement types isn't created

The parameters should be write after *' -- '* and to set a value to it use *'='* operator

### Examples

Set you CLI at this folder and execute this command to generate types and a scenario:

`npm generate -- --scenario=1`

If you only want generate the types:

`npm generate`

To generate only the scenario (*WARN | The scenario can need that the types are in the database*)

`npm generate -- --scenario=1 --assetTypes=false --measurementTypes=false`