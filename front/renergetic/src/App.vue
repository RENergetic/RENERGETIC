<template>
  <header>
      <img src="./assets/logo.svg" id="logo"/>
      <h1 id="title"><span>REN</span>ergetic</h1>
  </header>
  <Main/>
</template>

<script>
import Main from './components/Main.vue'

export default {
  name: 'App',
  components: {
    Main
  }
}
</script>

<style scoped>
  header {
      padding: 2vh 1vw;
      background: black;
      border-bottom: 3px solid #a4ca4a;
      flex: 0 1 auto;

      display: flex;
      flex-direction: row;
  }

  #logo {
    margin: 0;
    padding: 0;
    width: 4vw;
    flex: 0 1 auto;
  }

  h1 {
      padding: 0;
      margin: 0;
      font-size: calc(0.8vw + 2.3em);
      font-weight: bold;
      text-align: center;
      color: white;
    flex: 1 0 auto;
  }

  h1 > span {
    font-weight: bolder;
      color: #94ba3a;
      text-shadow: 1px -1px 0px #a4ca4a, 
      0px -1px 0px #a4ca4a, 
      1px 0px 0px #a4ca4a;
  }
</style>
