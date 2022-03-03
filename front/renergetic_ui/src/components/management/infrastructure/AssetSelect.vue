<template>
  <div>
    <Dialog
      v-model:visible="assetDialog"
      :style="{ width: '75vw' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="true"
    >
      <Card>
        <template #title> {{ $t("view.asset_list") }} </template>
        <template #content>
          <Listbox v-if="assetList" v-model="selectedAsset" :options="assetList" option-label="label">
            <template #option="slotProps">
              <div>{{ slotProps.option.label }}</div>
            </template>
          </Listbox>
          <div v-if="selectedAsset" class="field grid">
            <div class="col">
              <Button :label="$t('view.button.submit')" @click="submit" />
            </div>
            <div class="col">
              <Button :label="$t('view.button.cancel')" @click="cancel" />
            </div>
          </div>
        </template>
      </Card>
    </Dialog>
  </div>
</template>
<script>
import Listbox from "primevue/listbox";

import Card from "primevue/card";
export default {
  name: "AssetSelect",
  components: { Card, Listbox },
  props: { current: { type: Object, default: () => ({}) } },
  emits: { change: null },
  data() {
    return {
      assetList: [],
      selectedAsset: null,
      assetDialog: false,
    };
  },
  async mounted() {},
  methods: {
    // onChange(option) {},
    submit() {
      if (this.selectedAsset) {
        this.$emit("change", this.selectedAsset);
      }
    },
    async open() {
      await this.$ren.dashboardApi.listAsset().then((assetList) => {
        this.assetDialog = true;
        if (this.current) {
          if (assetList.find((it) => it.id == this.current.id) == null) {
            this.assetList = [this.current] + assetList;
          }
          this.selectedAsset = this.current;
        }
        this.assetList = assetList;
      });
    },
    cancel() {
      this.assetDialog = false;
    },
  },
};
</script>

<style lang="scss">
i.pi {
  margin-left: 0.25rem;
}
.flex > div {
  flex-grow: 1;
}
</style>
