export class TileItem {
  constructor(
    id,
    value,
    unit = null,
    description = null,
    icon = null,
    state = "ok"
  ) {
    this.id = id;
    this.value = value;
    this.unit = unit;
    this.description = description;
    this.icon = icon;
    this.state = state; //ok,warning,error
  }
}
