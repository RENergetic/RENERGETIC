export class HeatMap {
  constructor(id, imgUrl, areas = null) {
    if (areas != null) this.areas = areas;
    else this.areas = [];
    this.id = id;
    this.imgUrl = imgUrl;
    //TODO: set some label
    this.label = id;
  }
}
