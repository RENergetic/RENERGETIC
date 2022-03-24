<template>
  <div :class="'grid-stack-item ren'" v-bind="gridStackAttributes">
    <div :class="'grid-stack-item-content ' + state" style="padding: 0">
      <!-- <template v-if="tile != null && tile.title != null" #title> {{ tile.title }} </template> -->
      <!-- <template v-if="tile != null" #content> -->
      <!-- state: {{ state }}  -->

      <div style="position: absolute; left: 0.3rem; top: 0.3rem">
        <Button
          v-if="edit"
          id="menu-toggle"
          :class="'p-button-rounded p-button-text edit-button'"
          aria-haspopup="true"
          icon="pi pi-pencil"
          @click="$emit('edit', slotProps)"
        />
        <Button
          v-if="notificationVisible"
          id="notification"
          :class="'p-button-rounded p-button-text bell-button '"
          aria-haspopup="true"
          icon="pi pi-bell"
          @click="$emit('notification', slotProps)"
        />
      </div>
      <div class="card-container">
        <h3 v-if="tile != null && tile.label != null" class="block">{{ tile.label }}</h3>

        <KnobTile v-if="tile.type == 'knob'" class="block" :tile="tile" :pdata="pdata.data"></KnobTile>

        <DoughnutTile
          v-else-if="tile.type == 'doughnut'"
          class="block"
          :tile="tile"
          :pdata="pdata.data"
          :legend="true"
        ></DoughnutTile>

        <!-- tile list-->
        <InformationListTile
          v-else
          class="block"
          :tile="tile"
          :pdata="pdata.data"
          :font-size="fontSize"
        ></InformationListTile>
        <!-- </template> -->
      </div>
    </div>
  </div>
</template>
<script>
import InformationListTile from "./InformationListTile.vue";
import KnobTile from "./KnobTile.vue";
import DoughnutTile from "./DoughnutTile.vue";
// import Card from "primevue/card";
export default {
  name: "InformationTile",
  components: { InformationListTile, KnobTile, DoughnutTile },
  props: {
    edit: { type: Boolean, default: false },
    slotProps: {
      type: Object,
      default: () => ({}),
    },
    // index: {
    //   type: Number,
    //   default: null,
    // },
    pdata: {
      type: Object,
      default: () => ({}),
    },
    settings: {
      type: Object,
      default: () => ({}),
    },
  },
  emits: ["edit", "notification"],
  // data() {
  //   return {};
  // },
  computed: {
    fontSize: function () {
      let size = this.settings != null && this.settings.fontSize != null ? this.settings.fontSize : 2.0;
      return `${size}rem`;
    },
    tile: function () {
      return this.slotProps.tile;
    },
    notificationVisible: function () {
      //default visible
      return !(this.settings != null && !this.settings.notificationVisibility);
    },
    state: function () {
      // return state class
      // let state = this.tile == null || this.tile.state == null ? "unknown" : this.tile.state;
      let state = this.pdata && this.pdata.state ? this.pdata.state[this.tile.id] : "unknown";
      if (state) return ` state ${state.toLowerCase()}`;
      //state not provided to the exists tile (e.g. tile not saved yet in the backend)
      return ` state unknown`;
    },
    col: function () {
      return this.tile == null || this.tile.col == null ? 2 : this.tile.col;
    },
    layout: function () {
      // console.info(this.tile.layout);
      return this.tile != null && this.tile.layout != null ? this.tile.layout : (() => ({}))();
    },
    gridStackAttributes() {
      //TODO: other attributes?
      //https://github.com/gridstack/gridstack.js/tree/master/doc#grid-attributes
      // example with an old version https://codesandbox.io/s/grid-stack-js-integration-with-vuejs-72mrn?file=/src/App.vue
      return {
        id: this.tile.id,
        "gs-id": this.tile.id,
        "gs-x": this.layout.x,
        "gs-y": this.layout.y,
        "gs-w": this.layout.w,
        "gs-h": this.layout.h,
      };
    },
  },

  mounted() {},
  methods: {},
};
</script>

<style lang="scss">
.edit-button {
  position: absolute;
  right: 0;
  top: 0;
}
.bell-button {
  position: absolute;
  left: 0;
  top: 0;
}
.grid-stack-item {
  margin: 10px;
}
.grid-stack-item-content {
  display: flex;
  justify-content: center;
  align-items: center;
  color: #3182ce;
  background-color: #bee3f8;
  font-weight: 600;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
}
</style>
