<template>
  <div>
    <Dialog
      v-model:visible="assetDialog"
      :style="{ minWidth: '25rem' }"
      :maximizable="true"
      :modal="true"
      :dismissable-mask="true"
    >
      <Card>
        <template #title> {{ $t("view.asset_select") }} </template>
        <template #content>
          <div class="grid">
            <div class="col">
              <AutoComplete
                v-model="selectedAsset"
                :placeholder="$t('view.find_asset')"
                :suggestions="assetList"
                field="label"
                class="col-12"
                @complete="searchAsset($event)"
              />
            </div>
          </div>
          <!-- <Listbox v-if="assetList" v-model="selectedAsset" :options="assetList" option-label="label">
            <template #option="slotProps">
              <div>{{ slotProps.option.label }}</div>
            </template>
          </Listbox> -->
          <div class="grid">
            <div class="col">
              <Button :label="$t('view.button.submit')" @click="submit" />
            </div>
            <div class="col">
              <Button :label="$t('view.button.clear')" :disabled="selectedAsset == null" @click="clear" />
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
export default {
  name: "MeasurementSelect",
  components: {},
  props: { current: { type: Object, default: () => null }, modelValue: { type: Object, default: () => null } },
  emits: ["change", "update:modelValue"],
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
      // if (this.selectedAsset) {
      this.$emit("change", this.selectedAsset);
      this.$emit("update:modelValue", this.selectedAsset);
      this.assetDialog = false;
      // }
    },
    async searchAsset(event) {
      let q = event.query.trim();
      if (q.length > 0)
        await this.$ren.managementApi.searchAsset(q).then((assetList) => {
          // if (this.current) {
          //   if (assetList.find((it) => it.id == this.current.id) == null) {
          //     this.assetList = [this.current] + assetList;
          //   }
          //   this.selectedAsset = this.current;
          // }
          this.assetList = assetList;
        });
      else {
        await this.$ren.managementApi.listAsset().then((assetList) => {
          this.assetList = assetList;
        });
      }
    },
    async open(current = null) {
      this.assetDialog = true;
      if (current == null) this.selectedAsset = this.modelValue;
      else this.selectedAsset = current;

      // await this.$ren.managementApi.listAsset().then((assetList) => {
      //   this.assetDialog = true;
      //   if (this.current) {
      //     if (assetList.find((it) => it.id == this.current.id) == null) {
      //       this.assetList = [this.current] + assetList;
      //     }
      //     this.selectedAsset = this.current;
      //   }
      //   this.assetList = assetList;
      // });
    },
    cancel() {
      this.assetDialog = false;
    },
    clear() {
      this.selectedAsset = null;
    },
  },
};
</script>

<style lang="scss">
input {
  width: 100%;
}
</style>
