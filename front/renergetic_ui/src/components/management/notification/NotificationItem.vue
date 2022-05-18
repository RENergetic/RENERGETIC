<template>
  <div class="grid col-12">
    <div class="col-2 flex align-items-center justify-content-center">
      <span
        v-if="notification.icon != null"
        id="demandicon"
        :style="'background-image: url(' + icons[notification.icon] + ');width:7.5rem;'"
      ></span>
      <span
        v-if="notification.tile != null"
        id="demandtile"
        @click="$router.push(`/panel/view/${notification.tile.panelId}`)"
      >
        <!-- TODO: load :pdata="pdata" -->
        <DoughnutTile
          :style="'height:7.5rem;width:7.5rem;margin-right: 1rem;'"
          :legend="false"
          class="block"
          :tile="notification.tile"
          :pdata="{ '2': 2, '3': 3, '4': 4 }"
        ></DoughnutTile>
        <InformationTile
          v-if="false"
          :key="notification.tile.id"
          :slot-props="{ tile: demand.tile, index: 0 }"
          :settings="{}"
        />
        {{ notification.area }}</span
      >
    </div>

    <div class="col-8 flex flex-column justify-content-center flex-wrap">
      <div class="flex align-content-end flex-wrap">
        <!-- <div class="message">{{ notification.msg }}</div> -->
      </div>
      <div class="flex align-content-start flex-wrap">
        <div class="flex align-items-center justify-content-center">
          {{ notification.message }}
          <div v-if="notification.dashboard" class="flex flex-grow-1">
            {{ $t("view.go_to_dashboard") }}
            <i
              v-if="notification.dashboard"
              v-tooltip="$t('view.go_to_dashboard')"
              class="pi pi-arrow-circle-right"
              @click="$router.push(`/dashboard/view/${notification.dashboard.id}`)"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="col-2 flex-none flex align-items-center justify-content-center">
      <i v-if="notification.type == 'warning'" class="pi pi-exclamation-triangle"></i>
      <i v-if="notification.type == 'info'" class="pi pi-info-circle"></i>
      <i v-if="notification.type == 'error'" class="pi pi-times-circle"></i>
      <!-- TODO: set empty icon ??? <i v-else class="pi pi-arrow-down-right"></i> -->
    </div>
  </div>
</template>
<script>
import InformationTile from "@/components/dashboard/informationtile/InformationTile.vue";
import DoughnutTile from "@/components/dashboard/informationtile/DoughnutTile.vue";

export default {
  name: "NotificationItem",
  components: { InformationTile, DoughnutTile },
  props: {
    notification: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      icons: {
        battery: require(`@/assets/img/tileicons/battery.png`),
        electricity: require(`@/assets/img/tileicons/electricity.png`),
        heat: require(`@/assets/img/tileicons/heat.png`),
      },
    };
  },
  computed: {},
  watch: {},

  methods: {},
};
</script>

<style lang="scss">
.heatdemand {
  i {
    font-size: 2rem;
    margin-left: 0.75rem;
  }
  .description {
    font-size: 1rem;
    font-style: italic;
  }
  .message {
    font-size: 1.5rem;
    font-weight: 600;
  }
}
#demandicon {
  width: 5rem;
  height: 5rem;
  // display: inherit;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center;
  margin-right: 1rem;
}
</style>
