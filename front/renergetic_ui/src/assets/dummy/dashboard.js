//TODO: timestamp
//async () => {return

// var measurementAttributes = [
//   {
//     key: "id4",
//     label: "Predictors",
//     name: "predictors",
//     type: "group",
//     children: [
//       {
//         key: "id5",
//         name: "electrical",
//         label: "Electrical",
//         type: "measurement",
//         children: [],
//       },
//       {
//         key: "id6",
//         name: "thermic",
//         label: "Thermic",
//         type: "measurement",
//         children: [],
//       },
//     ],
//   },
//   {
//     key: "id1",
//     label: "Target",
//     name: "target",
//     type: "group",
//     children: [
//       {
//         key: "id2",
//         name: "electrical",
//         label: "Electrical",
//         type: "measurement",
//         children: [],
//       },
//       {
//         key: "id3",
//         name: "thermic",
//         label: "Thermic",
//         type: "measurement",
//         children: [],
//       },
//     ],
//   },
//   {
//     key: "id7",
//     label: "Prediction Interval",
//     name: "prediction_interval",
//     type: "group",
//     children: [
//       {
//         key: "id8",
//         name: "3h",
//         label: "3 H",
//         type: "measurement",
//         children: [],
//       },
//       {
//         key: "id9",
//         name: "6h",
//         label: "6 H",
//         type: "measurement",
//         children: [],
//       },
//     ],
//   },
// ];
// let measurementsGenerator = (i, n) => {
//   let datasets = [];
//   for (var d = 0; d < i; d++) {
//     var data = [];
//     let next = Math.floor(Math.random() * 150) - 50;
//     for (var j = 0; j < n; j++) {
//       data.push(next);
//       next += Math.floor(Math.random() * 150) - 50;
//     }
//     datasets.push({
//       data: data,
//       label: `label_${j}`,
//       // borderColor: ,
//       fill: false,
//     });
//   }
//   let labels = [];

//   var t = Date.now();
//   t = t - (t % 60000);
//   for (j = 0; j < n; j++) {
//     labels.push(t - 60000 * (n - j));
//   }
//   return { datasets: datasets, labels: labels };
// };

// export {
//   dummyList,
//   informationPanels,
//   heatmaps,
//   measurementAttributes,
//   measurementsGenerator,
//   dataGenerator,
//   currentMeasurementsGenerator,
// };
