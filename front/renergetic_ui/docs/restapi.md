## REST API description
default parameters:
- 'o','offset' - skip 'n' rows
- 'l','limit' - number of returned rows
date filter
- 'from' - unix timestamp, [ms]
- 'to' - unix timestamp, [ms]

### Dashboard
data layout api

* list() - returns list of grafana based dashboards available for the user. 
* add(dashboard) - add dashboard
* delete(dashboard) - delete dashboard
* informationPanelList() - returns list of information panels to the user. 
* getInformationPanel(id) - get information panel with details by it
* listHeatMap  - returns list of grafana based dashboards to the user. 
* getHeatMap(id) -  get heatmap by id
* addHeatMap(heatmap)
      
### Data API
api for data aquuisition
* getMeasurement(resourceId,measurements) -
    * measurements - list of measurements, possible values: 'heat','power',...  ,empty = all TODO:

getCurrentMeasurements -> TODO:

* getMeasurements(resourceIds,measurements) -
    * measurements - list of measurements, possible values: 'heat','power',...  ,empty = all TODO:

* getPanelMeasurements(informationPanel)  -> TODO:
* getHeatmapMeasurements(Heatmap)  -> TODO:

### management
manage resources in the energy island


### admin

### user