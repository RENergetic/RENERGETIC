<template>
  <InfoIcon :show-icon="false">
    <template #content>
      <!-- some info -->
    </template>
  </InfoIcon>

  <DataTable :value="assetList">
    <!-- <Column v-for="col of columns" :key="col" :field="col" :header="$t('model.asset.' + col)"></Column> -->
    <Column field="name" :header="$t('model.asset.name')"> </Column>
    <Column field="label" :header="$t('model.asset.label')"> </Column>
    <Column field="asset_type" :header="$t('model.asset.asset_type')"> </Column>
    <Column field="child" :header="$t('model.asset.child')">
      <template #body="slotProps">
        {{ slotProps.data.child }}
      </template>
    </Column>

    <Column field="parent" :header="$t('model.asset.parent')">
      <template #body="slotProps">
        <span v-if="slotProps.data.parent" @click="setParent">
          {{ slotProps.data.parent.label }}
        </span>
        <span v-else class="disabled" @click="setParent">
          {{ $t("view.no_parent") }}
        </span>
      </template>
    </Column>
    <Column field="geo_location" :header="$t('model.asset.geo_location')"> </Column>
  </DataTable>
  <Button :label="$t('view.button.add')" @click="assetAdd = true" />
  <Dialog
    v-model:visible="assetAdd"
    :style="{ width: '75vw' }"
    :maximizable="true"
    :modal="true"
    :dismissable-mask="true"
  >
    <AssetForm @update:model-value="onCreate($event, 0)"></AssetForm>
  </Dialog>
</template>

<script>
import InfoIcon from "../../miscellaneous/InfoIcon.vue";
import AssetForm from "./AssetForm.vue";
export default {
  name: "AssetList",
  components: { InfoIcon, AssetForm },
  props: {},
  data() {
    return { assetAdd: false, assetList: [], columns: [] };
  },
  computed: {},
  watch: {},
  async created() {
    this.assetList = await this.$ren.managementApi.listAsset();
    if (this.assetList != null && this.assetList.length > 0) {
      this.columns = Object.keys(this.assetList[0]);
    }
  },
  methods: {
    setParent() {
      console.info("TODO: Set parent");
    },
    onCreate(o, i) {
      alert(o);
      alert(i);
    },
    // async loadData() {
    //   await this.$ren.measurementApi.measurements(this.objects).then((data) => {
    //     this.data = data;
    //   });
    // },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
