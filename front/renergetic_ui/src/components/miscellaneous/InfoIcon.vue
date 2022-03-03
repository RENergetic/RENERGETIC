<template>
  <!-- <div> -->
  <OverlayPanel ref="InfoPanel" style="max-width: 25rem" append-to="body" :show-close-icon="showCloseIcon">
    <!-- <content> totdo optional slots
      
    </content> -->
    <slot name="content" />
    {{ label }}
  </OverlayPanel>
  <div v-if="!showIcon" @mouseleave="$refs.InfoPanel.toggle" @mouseover="$refs.InfoPanel.toggle">
    <slot name="body" />
  </div>
  <i
    v-if="showIcon"
    id="infoicon"
    class="pi pi-info-circle"
    style="fontsize: 2rem"
    @mouseleave="$refs.InfoPanel.toggle"
    @mouseover="$refs.InfoPanel.toggle"
    @click="$refs.InfoPanel.toggle"
  />
  <!-- </div> -->
</template>

<script>
import OverlayPanel from "primevue/overlaypanel";

export default {
  name: "InfoIcon",
  components: { OverlayPanel },

  props: {
    showCloseIcon: { type: Boolean, default: false },
    label: String,

    showIcon: { type: Boolean, default: true },
  },
  data() {
    return {};
  },
  methods: {},
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
#infoicon {
  vertical-align: top;
  margin-left: 0.2rem;
}
</style>
