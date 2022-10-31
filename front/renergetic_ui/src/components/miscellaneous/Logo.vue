<template>
  <router-link id="pagelogo" :to="{ path: '/' }" />
</template>

<script>
export default {
  name: "RenLogo",
};
</script>
<style scoped lang="scss">
#pagelogo {
  background-image: url("../../assets/logo.png");
  /* width: 7.5rem; */
  display: inherit;
  height: $sidemenu-logo-height;
  /* background-size: 100%; */
  background-repeat: no-repeat;
  background-position: center;
  background-size: contain;
}
</style>
