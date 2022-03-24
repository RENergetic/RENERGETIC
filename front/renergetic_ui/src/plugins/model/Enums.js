const Colors = Object.freeze({
  OK: "#8fe388",
  WARNING: "#ffa059",
  ERROR: "#de0000",
  SELECTED: "#0a0fc9",
  DEFAULT: "#eaeef5",
});
const AssetTypes = Object.freeze({
  BUILDING: "bulding",
  FLAT: "flat",
  HEAT_SOURCE: "heat_source",
  ISLAND: "island",
  PV: "pv",
});

const TileTypes = Object.freeze({
  knob: "knob",
  doughnut: "doughnut",
  list: "list",
});

const NotificationContext = Object.freeze({
  ASSET: "asset",
  USER: "user",
  AREA: "area",
  TILE: "area",
});
export { Colors, AssetTypes, TileTypes, NotificationContext };
