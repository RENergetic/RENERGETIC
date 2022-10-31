export default [
  {
    id: 1,
    demand_definition: {
      action: "increase_temperature",
      id: 0,
      message: "increase",
      action_type: "increase",
      tile: {
        id: "3",
        state: "warning",
        label: null,
        type: "doughnut",
        measurements: [
          {
            id: "2",
            name: "e5",
            label: "e5",
            unit: "kWh",
            description: "avg",
            icon: "electricity",
            measurement_details: { color: "#5C6BC0" },
          },
          {
            id: "3",
            name: "e6",
            label: "e6",
            unit: "kWh",
            description: "total",
            icon: "electricity",
            measurement_details: { color: "#FF7043" },
          },
          {
            id: "4",
            name: "e6",
            label: "e6",
            unit: "kWh",
            description: "total",
            icon: "electricity",
            measurement_details: { color: "#1976D2" },
          },
        ],
        props: {},
      },
    },

    asset: { id: 1, name: "asset_name", label: "label" },
    demand_start: 1646082303,
    demand_stop: 1646182303,
  },

  {
    id: 2,
    demand_definition: {
      id: 0,
      action: "decrease_power",
      action_type: "decrease",
      message: "Decrease power usage",
    },

    asset: { id: 2, name: "asset_name", label: "label" },
    demand_start: 1646082303,
    demand_stop: 1646182303,
  },
  {
    id: 3,
    demand_definition: {
      id: 0,
      action: "increase_power",
      action_type: "increase",
      message: "increase power usage",
    },

    asset: { id: 2, name: "asset_name", label: "label" },
    demand_start: 1646082303,
    demand_stop: 1646182303,
  },
  {
    id: 4,
    demand_definition: {
      id: 0,
      action: "increase_heat",
      action_type: "increase",
      message: "increase heat usage",
    },

    asset: { id: 2, name: "asset_name", label: "label" },
    demand_start: 1646082303,
    demand_stop: 1646182303,
  },
  {
    id: 5,
    demand_definition: {
      id: 0,
      action: "decrease_heat",
      action_type: "Decrease",
      message: "Decrease heat usage",
    },

    asset: { id: 2, name: "asset_name", label: "label" },
    demand_start: 1646082303,
    demand_stop: 1646182303,
  },
];
