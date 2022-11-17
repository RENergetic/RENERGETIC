<template>
  <div>Auth data {{ data }}aa</div>
  <div>Logged user {{ user }}</div>
</template>
<script>
export default {
  name: "Profile",
  components: {},
  data() {
    return {
      data: this.$store.getters["auth/current"],
      user: undefined,
    };
  },
  watch: {},
  async mounted() {
    try {
      this.user = await this.$ren.userApi.getByUsername((await this.$keycloak.get()).tokenParsed.preferred_username);
    } catch (err) {
      alert(err);
    }
  },
  methods: {},
};
</script>
<style lang="scss"></style>
