<template>
  <div class="div-sign-in mx-auto p-grid">
    <TabView ref="signTable" v-model:activeIndex="activeTab">
      <TabPanel header="Your User Data">
        <div class="p-fluid p-grid">
          <span class="p-float-label mt-4">
            <InputText
              id="username"
              v-model="user.username"
              type="text"
              :class="error.username.value ? 'p-invalid' : ''"
              @focus="error.username.value = false"
            />
            <label for="username">Choose a Username</label>
          </span>
          <small id="username-help" :class="error.username.value ? 'p-error' : 'p-sr-only'">{{
            error.username.msg
          }}</small>

          <span class="p-float-label mt-4">
            <InputText
              id="firstname"
              v-model="user.firstName"
              type="text"
              :class="error.firstName.value ? 'p-invalid' : ''"
              @focus="error.firstName.value = false"
            />
            <label for="firstname">Your First Name</label>
          </span>
          <small id="firstname-help" :class="error.firstName.value ? 'p-error' : 'p-sr-only'"
            >First name is required</small
          >

          <span class="p-float-label mt-4">
            <InputText
              id="lastname"
              v-model="user.lastName"
              type="text"
              :class="error.lastName.value ? 'p-invalid' : ''"
              @focus="error.lastName.value = false"
            />
            <label for="lastname">Your Last Name</label>
          </span>
          <small id="lastname-help" :class="error.lastName.value ? 'p-error' : 'p-sr-only'"
            >Last name is required</small
          >

          <span class="p-float-label mt-4">
            <InputText
              id="email"
              v-model="user.email"
              type="text"
              :class="error.email.value ? 'p-invalid' : ''"
              @focus="error.email.value = false"
            />
            <label for="email">Your E-mail</label>
          </span>
          <small id="email-help" :class="error.email.value ? 'p-error' : 'p-sr-only'">{{ error.email.msg }}</small>

          <span class="p-float-label mt-4">
            <Password
              id="password"
              v-model="user.credentials[0].value"
              :class="error.password.value ? 'p-invalid' : ''"
              @focus="error.password.value = false"
            />
            <label for="email">Your Password</label>
          </span>
          <small id="password-help" :class="error.password.value ? 'p-error' : 'p-sr-only'">Password is required</small>
        </div>
      </TabPanel>
      <TabPanel header="Your Home">
        <div class="p-fluid p-grid">
          <span class="p-float-label mt-4">
            <InputText
              id="name"
              v-model="asset.name"
              type="text"
              :class="error.homeName.value ? 'p-invalid' : ''"
              @focus="error.homeName.value = false"
            />
            <label for="name">House Name</label>
          </span>
          <small id="name-help" :class="error.homeName.value ? 'p-error' : 'p-sr-only'">House name is required</small>

          <span class="p-float-label mt-4">
            <InputText id="description" v-model="asset.description" type="text" />
            <label for="description">Write a description (Optional)</label>
          </span>
          <span class="p-float-label mt-4">
            <InputText
              id="geo_location"
              v-model="asset.geoLocation"
              type="text"
              :class="error.geoLocation.value ? 'p-invalid' : ''"
              @focus="error.geoLocation.value = false"
            />
            <label for="geo_location">Location (Latitude, Longitude)</label>
          </span>
          <small id="geo_location-help" :class="error.geoLocation.value ? 'p-error' : 'p-sr-only'"
            >Geolocation is required</small
          >

          <span class="p-float-label mt-4">
            <Dropdown
              id="type"
              v-model="selectedType"
              :options="types"
              option-label="text"
              :class="error.type.value ? 'p-invalid' : ''"
              @focus="error.type.value = false"
            />
            <label for="type">House's Type</label>
          </span>
          <small id="type-help" :class="error.type.value ? 'p-error' : 'p-sr-only'">House type is required</small>
        </div>
      </TabPanel>
    </TabView>
    <Toolbar>
      <template #start>
        <Button
          label="Clear"
          icon="pi pi-undo"
          class="p-button-text p-left"
          style="color: var(--orange-300)"
          @click="clear()"
        />
      </template>
      <template #end>
        <Button
          label="Sign in"
          icon="pi pi-user-plus"
          class="p-button-sucess"
          style="width: 10vw"
          @click="signIn()"
        ></Button>
      </template>
    </Toolbar>
  </div>
</template>
<script>
export default {
  name: "SignIn",
  data() {
    return {
      activeTab: 0,
      user: {
        enabled: true,
        attributes: {},
        groups: [],
        username: undefined,
        firstName: undefined,
        lastName: undefined,
        email: undefined,
        emailVerified: "false",
        credentials: [{ type: "password", value: undefined, temporary: false }],
      },
      asset: {
        name: undefined,
        description: undefined,
        geoLocation: undefined,
        type: "flat",
        ownerUserId: undefined,
      },
      error: {
        username: {
          value: false,
          msg: "",
        },
        firstName: {
          value: false,
          msg: "",
        },
        lastName: {
          value: false,
          msg: "",
        },
        email: {
          value: false,
          msg: "",
        },
        password: {
          value: false,
          msg: "",
        },

        homeName: {
          value: false,
          msg: "",
        },
        geoLocation: {
          value: false,
          msg: "",
        },
        type: {
          value: false,
          msg: "",
        },
      },
      selectedType: undefined,
      types: [
        { text: "Building", value: "building" },
        { text: "Flat", value: "flat" },
      ],
    };
  },
  methods: {
    signIn() {
      for (let key in this.error) this.error[key].value = false;
      // CHECK USER PARAMETERS
      if (!this.user.username) {
        this.error.username.value = true;
        this.error.username.msg = "Username is required";
      } else this.error.username.value = false;
      if (!this.user.firstName) this.error.firstName.value = true;
      else this.error.firstName.value = false;
      if (!this.user.lastName) this.error.lastName.value = true;
      else this.error.lastName.value = false;
      if (!this.user.email) {
        this.error.email.value = true;
        this.error.email.msg = "Email is required";
      } else this.error.email.value = false;
      if (!this.user.credentials[0].value) this.error.password.value = true;
      else this.error.password.value = false;

      let error = false;
      for (let key in this.error)
        if (this.error[key].value) {
          error = true;
          break;
        }
      // CHECK ASSET PARAMETERS
      if (!this.asset.name) this.error.homeName.value = true;
      else this.error.homeName.value = false;
      if (!this.asset.geoLocation) this.error.geoLocation.value = true;
      else this.error.geoLocation.value = false;
      if (!this.selectedType) this.error.type.value = true;
      else this.error.type.value = false;

      if (!error) {
        for (let key in this.error)
          if (this.error[key].value) {
            error = true;
            this.activeTab = 1;
            break;
          }
      } else {
        this.activeTab = 0;
      }
      if (!error) {
        this.user.asset = this.asset;
        this.$ren.userApi
          .register(this.user)
          .then(() => {
            this.$router.replace({ name: "Home" });
          })
          .catch((err) => {
            console.error(err.response.status);
            //if (response.status)
          });
      }
    },
    clear() {
      // CHECK USER PARAMETERS
      this.user.username = undefined;
      this.error.username.value = false;
      this.user.firstName = undefined;
      this.error.firstName.value = false;
      this.user.lastName = undefined;
      this.error.lastName.value = false;
      this.user.email = undefined;
      this.error.email.value = false;
      this.user.credentials.password = undefined;
      this.error.password.value = false;
      // CHECK ASSET PARAMETERS
      this.asset.name = undefined;
      this.error.homeName.value = false;
      this.asset.description = undefined;
      this.asset.geoLocation = undefined;
      this.error.geoLocation.value = false;
      this.asset.type = undefined;
      this.error.type.value = false;
    },
  },
};
</script>

<style lang="scss" scoped>
.div-sign-in {
  padding: 2vh 3vw;
  background-color: white;
  border-radius: 2em;
  width: 80%;
  vertical-align: middle;
}
</style>
