import { Colors } from "../../../plugins/model/Enums.js";
console.info(Object.keys(Colors));
function listMeasurements(informationPanel) {
  console.info(informationPanel);
  let md = {};
  for (let tile of informationPanel.tiles) {
    for (let m of tile.measurements) {
      md[m.id] = "";
    }
  }
  return md;
}
function listAssetMeasurements(asset) {
  let m = {};

  for (let m of asset.measurements) {
    m[m.id] = null;
  }

  return m;
}
function generateHeatMapState(heatmap) {
  let state = {};
  for (let area of heatmap.areas) {
    let value = Math.random();
    if (value <= 0.6) {
      state[area.id] = "OK";
    } else if (value <= 0.85) {
      state[area.id] = "WARNING";
    } else state[area.id] = "ERROR";
  }
  return state;
}

function generatePanelData(informationPanel) {
  let measurements = listMeasurements(informationPanel);
  let data = {};
  for (let m of Object.keys(measurements)) {
    let value = Math.floor(Math.random() * 150) - 50;
    data[m] = value;
  }
  return data;
}

function generatePanelState(informationPanel) {
  let state = {};
  for (let panel of informationPanel.tiles) {
    let value = Math.random();
    if (value <= 0.6) {
      state[panel.id] = "OK";
    } else if (value <= 0.85) {
      state[panel.id] = "WARNING";
    } else state[panel.id] = "ERROR";
  }
  return state;
}

function generateAssetData(asset) {
  let measurements = listAssetMeasurements(asset);
  let data = {};
  for (let m of Object.keys(measurements)) {
    let value = Math.floor(Math.random() * 150) - 50;
    data[m] = value;
  }
  return data;
}

function generateTimeseries(ids, n = 50) {
  let datasets = [];
  for (let id of ids) {
    var data = [];
    let next = Math.floor(Math.random() * 150) - 50;
    for (var j = 0; j < n; j++) {
      data.push(next);
      next += Math.floor(Math.random() * 150) - 50;
    }
    datasets.push({
      data: data,
      label: `label_${id}`,
      // borderColor: ,
      fill: false,
    });
  }
  let labels = [];

  var t = Date.now();
  t = t - (t % 60000);
  for (j = 0; j < n; j++) {
    labels.push(t - 60000 * (n - j));
  }
  return { datasets: datasets, labels: labels };
}

export { generatePanelData, generateTimeseries, generatePanelState, generateAssetData, generateHeatMapState };
