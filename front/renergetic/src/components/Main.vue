<template>
  <article>
    <NewIsland ip='http://127.0.0.1:8082/api/islands'></NewIsland>
    <SeeIslands ip='http://127.0.0.1:8082/api/islands'></SeeIslands>
  </article>
</template>

<script>
import NewIsland from './NewIsland.vue'
import SeeIslands from './SeeIslands.vue'

export default {
  name: 'Main',
  props: {
    
  },
  components: {
    NewIsland,
    SeeIslands
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  article{
    width: fit-content;
    margin: 2vw auto;
  }
</style>
