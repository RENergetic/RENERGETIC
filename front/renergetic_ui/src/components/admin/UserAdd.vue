<template>
  <!-- TODO add locale keys -->
  <Dialog
    :visible="visible"
    :style="{ width: '30rem' }"
    :header="$t('view.add_new_user')"
    :modal="true"
    @update:visible="cancelAdd"
  >
    <div class="field grid">
      <label for="username" class="col-fixed" style="width: 10rem">
        {{ $t("model.user.username") }}
      </label>
      <div class="col">
        <InputText id="username" v-model="user.username" type="text" :class="error.username ? 'p-invalid' : ''" />
      </div>
    </div>
    <div class="field grid">
      <label for="firstName" class="col-fixed" style="width: 10rem">
        {{ $t("model.user.firstName") }}
      </label>
      <div class="col">
        <InputText id="firstName" v-model="user.firstName" type="text" />
      </div>
    </div>
    <div class="field grid">
      <label for="lastName" class="col-fixed" style="width: 10rem">
        {{ $t("model.user.lastName") }}
      </label>
      <div class="col">
        <InputText id="lastName" v-model="user.lastName" type="text" />
      </div>
    </div>
    <div class="field grid">
      <label for="email" class="col-fixed" style="width: 10rem">
        {{ $t("model.user.email") }}
      </label>
      <div class="col">
        <InputText id="email" v-model="user.email" type="email" :class="error.email ? 'p-invalid' : ''" />
      </div>
    </div>

    <div class="field grid">
      <label for="password" class="col-fixed" style="width: 10rem">
        {{ $t("model.user.password") }}
      </label>
      <div class="col">
        <Password id="password" v-model="user.password" :class="error.password ? 'p-invalid' : ''" />
      </div>
    </div>
    <div class="field grid">
      <label for="temp_password" class="col-fixed" style="width: 10rem">
        {{ $t("model.user.temp_password") }}
      </label>
      <div class="col">
        <ToggleButton
          id="temp_password"
          v-model="user.credentials[0].temporary"
          on-label="Temporal Password"
          off-label="Permanent Password"
          on-icon="pi pi-check"
          off-icon="pi pi-times"
        />
      </div>
    </div>

    <template #footer>
      <Button
        label="No"
        icon="pi pi-times"
        class="p-button-text p-left"
        style="color: var(--orange-300)"
        @click="cancelAdd()"
      />
      <Button label="Yes" icon="pi pi-check" class="p-button-text" @click="confirmAdd()" />
    </template>
  </Dialog>
</template>

<script>
import Password from "primevue/password";

export default {
  name: "UserAdd",
  components: {
    Password,
  },
  props: {
    users: {
      type: Array,
      required: true,
    },
    editUser: {
      type: Object,
      required: false,
      default: undefined,
    },
    visible: Boolean,
  },
  emits: ["close"],
  data() {
    return {
      user: {
        enabled: true,
        attributes: {},
        groups: [],
        username: undefined,
        firstName: undefined,
        lastName: undefined,
        email: undefined,
        emailVerified: "false",
        credentials: [{ type: "password", value: undefined, temporary: true }],
      },
      error: {
        username: false,
        email: false,
        password: false,
      },
    };
  },
  watch: {
    visible: function (value) {
      if (value) {
        if (this.editUser != undefined) {
          this.user = this.editUser;
          this.user.credentials = [{ type: "password", value: undefined, temporary: true }];
          delete this.user.roles;
          delete this.user.name;
        }
      } else this.reset();
    },
  },
  methods: {
    confirmAdd() {
      let error = false;
      console.error(this.user);
      for (let user of this.users) {
        if (
          this.user.username === undefined ||
          (this.user.username === user.username &&
            (this.editUser == undefined || this.user.username != this.editUser.username))
        ) {
          error = true;
          this.error.username = true;
        }
        if (
          this.user.email != undefined &&
          this.user.email === user.email &&
          (this.editUser == undefined || this.user.email != this.editUser.email)
        ) {
          error = true;
          this.error.email = true;
        }
      }
      if (this.editUser != undefined && this.user.credentials[0].value === undefined) {
        delete this.user.credentials;
      } else if (this.user.credentials[0].value === undefined || this.user.credentials[0].value == 0) {
        error = true;
        this.error.password = true;
      }

      if (!error && this.editUser === undefined) {
        this.$keycloak.createUser(this.user).then(() => {
          this.$emit("close", this.user);
        });
        //TODO: catch posible errors
      } else if (!error) {
        this.$keycloak.updateUser(this.user).then(() => {
          this.$emit("close", this.user);
        });
      }
    },
    cancelAdd() {
      this.$emit("close", undefined);
    },
    reset() {
      Object.assign(this.$data, this.$options.data.apply(this));
    },
  },
};
</script>

<style lang="scss" scoped>
@media screen and (max-width: 960px) {
}
</style>
