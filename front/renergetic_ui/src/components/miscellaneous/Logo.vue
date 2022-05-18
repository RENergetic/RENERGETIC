<template>
  <router-link id="pagelogo" :to="{ path: '/' }" />
</template>

<script>
export default {
  name: "RenLogo",
};
</script>
<style scoped>
#pagelogo {
  background-image: url("../../assets/logo.svg");
  width: 7.5rem;
  height: 3rem;
  display: inherit;
  background-size: 100% 3rem;
  background-repeat: no-repeat;
  background-position: center;
}
</style>
