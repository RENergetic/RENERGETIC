/* eslint-disable vue/v-on-event-hyphenation */ /* eslint-disable vue/first-attribute-linebreak */ /* eslint-disable
vue/attribute-hyphenation */
<template>
  <Dialog
    :visible="visible"
    :style="{ width: '450px' }"
    header="Add new user"
    :modal="true"
    @update:visible="cancelAdd"
  >
    <div class="p-fluid grid">
      <span class="p-float-label p-my-4">
        <InputText id="username" v-model="user.username" type="text" :class="error.username ? 'p-invalid' : ''" />
        <label for="username">Username</label>
      </span>
      <span class="p-float-label p-my-4">
        <InputText id="firstname" v-model="user.firstName" type="text" />
        <label for="firstname">User First Name</label>
      </span>
      <span class="p-float-label p-my-4">
        <InputText id="lastname" v-model="user.lastName" type="text" />
        <label for="lastname">User Surname</label>
      </span>
      <span class="p-float-label p-my-4">
        <InputText id="email" v-model="user.email" type="text" :class="error.email ? 'p-invalid' : ''" />
        <label for="email">User E-mail</label>
      </span>
      <span class="p-float-label p-my-4">
        <Password id="password" v-model="user.credentials[0].value" :class="error.password ? 'p-invalid' : ''" />
        <label for="email">User Password</label>
      </span>
      <ToggleButton
        v-model="user.credentials[0].temporary"
        on-label="Temporal Password"
        off-label="Permanent Password"
        on-icon="pi pi-check"
        off-icon="pi pi-times"
      />
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
