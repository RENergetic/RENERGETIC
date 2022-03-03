<template>
  <div class="p-fluid">
    <div v-for="s in schema" :key="s" :class="'field grid'">
      <label :for="s.key" class="col-12">{{ s.label }}</label>

      <!-- <ToggleButton
        v-model="mModel['key']"
        :on-label="s.label.true"
        :off-label="s.label.false"
        on-icon="pi pi-check"
        off-icon="pi pi-times"
      /> -->
      <div class="col-12">
        <!-- {{ s.ext.mode }} -->
        <SelectButton
          v-if="s.type == Boolean"
          :id="s.key"
          v-model="mModel[s.key]"
          option-label="name"
          option-value="value"
          :options="[
            { name: s.ext.true, value: true },
            { name: s.ext.false, value: false },
          ]"
        />
        <InputNumber
          v-else-if="s.type == Number"
          :id="s.key"
          v-model="mModel[s.key]"
          :min-fraction-digits="0"
          :max-fraction-digits="5"
          :mode="s.ext.mode"
          :use-grouping="false"
        />
        <div v-else-if="s.type == Array">
          <ListBox :id="s.key" v-model="mModel[s.key]" :options="s.ext.options" :option-label="s.ext.optionLabel" />
        </div>
        <InputText v-else :id="s.key" v-model="mModel[s.key]" />
      </div>
    </div>
  </div>
</template>

<script>
import SelectButton from "primevue/selectbutton";
import InputNumber from "primevue/inputnumber";
import ListBox from "primevue/listbox";
export default {
  name: "Settings",
  components: {
    SelectButton,
    InputNumber,
    ListBox,
    // ToggleButton
  },
  props: {
    model: {
      type: Object,
      default: () => ({}),
    },
    schema: {
      type: Object,
      default: () => ({}),
    },
  },
  emits: ["update:modelValue"],
  data() {
    return { mModel: this.model };
  },
  watch: {
    mModel: {
      handler: function (newVal) {
        this.$emit("update:modelValue", newVal);
      },
      deep: true,
    },
  },

  async created() {},
  methods: {
    toggle(event) {
      this.$refs.menu.toggle(event);
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss"></style>
