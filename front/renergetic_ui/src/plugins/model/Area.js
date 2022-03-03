export class MapArea {
  constructor(id, roi = null, dashboardId = null) {
    if (roi != null) this.roi = roi;
    else this.roi = [];
    this.id = id;
    //for primefaces modules
    this.value = id;
    //TODO: set some label
    this.label = id;
    //Grafana's dashboard id
    this.dashboardId = dashboardId;
  }
}
