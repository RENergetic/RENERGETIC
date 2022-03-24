<template>
  <div class="card ren">
    <!--description -->
    <div class="field grid">
      <label for="usernameInput" class="col-fixed">
        {{ $t("model.user.username") }}
      </label>
      <div class="col">
        <InputText id="usernameInput" v-model="username" />
      </div>
    </div>

    <div class="field grid">
      <label for="passwordInput" class="col-fixed">
        {{ $t("model.user.password") }}
      </label>
      <div class="col">
        <Password id="passwordInput" v-model="password" :feedback="false" toggle-mask />
      </div>
    </div>
    <div class="field grid">
      <div class="col">
        <!-- <Button  :label="$t('view.button.cancel')" /> -->
      </div>
      <div class="col">
        <Button :label="$t('view.button.login')" @click="login" />
      </div>
    </div>
  </div>
</template>

<script>
import Password from "primevue/password";
export default {
  name: "UserLogin",
  components: {
    Password,
  },
  props: {
    msg: String,
  },
  data() {
    return {
      password: null,
      username: null,
    };
  },
  methods: {
    login: async function () {
      await this.$ren.rest.auth.login(this.username, this.password).then(() => {
        console.info("todo");
      });
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
$labelWidth: 8rem;
label {
  width: $labelWidth;
}
textarea {
  width: 100%;
  max-width: 100%;
  resize: vertical;
  max-height: 15re;
}
button {
  width: 100%;
}
</style>
