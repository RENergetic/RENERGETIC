# INTRODUCTION

Here you can find SQL files to insert data into PostgreSQL

The SQL should be executed in the order that they appears here because some of them have relations with others

## Necessary scripts to start
 
 - measurementTypes.sql -------- Generate rows with all measurements types
 - assetTypes.sql -------------- Generate rows with all asset types
 - influxRequirements.sql ------ Generate rows at PostgreSQL with InfluxDB allowed tags and measurements
 - notificationDefinitions.sql - Generate definitions to generate notifications using them

## Script for each installation

### ren-prototype

 - scenario1.sql ---------- Generate rows with data proposed at [scenario 1](../scenarios/scenario1.xlsx)

### renergetic-wp4

Files to automatize Gent pilot configuration

 - renergetic_wp4.sql ---------- Default configuration for measurements, assets and example tiles
 - production_screen.sql ------- Example for generate a production screen similar to a given mockup

### renergetic-wp5

Files to automatize Poznan pilot configuration

 - renergetic_wp5.sql ---------- Default configuration for measurements, assets and example tiles