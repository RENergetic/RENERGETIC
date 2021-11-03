<template>
  <div id="changeip" @click='changeip'>{{ip}}</div>
    <article id='main'>
      <section>
        <header>
          <h2>Island Management</h2>
        </header>
        <NewIsland :ip='ip' @event-add='eventAddTag'></NewIsland>
      </section>

      <section>
        <header>
          <h2>Islands</h2>
        </header>
        <SeeIslands :ip='ip' ref='seeTag'></SeeIslands>
      </section>
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
  },
  methods: {
    eventAddTag() {
      console.log("Funciona");
      this.$refs.seeTag.listIslands();
    },

    changeip(){
      if (this.ip === 'http://127.0.0.1:8082/api/islands')
        this.ip = 'http://127.0.0.1/api/islands';
      else this.ip = 'http://127.0.0.1:8082/api/islands';
    }
  },
  data() {
    return {
      ip: /*'http://127.0.0.1:8082/api/islands'*/'http://127.0.0.1/api/islands'
    }
  },
  mounted() {
    const mainTag = document.getElementById('main');
    for (const tag of mainTag.childNodes) {
      tag.style.maxHeight = `${mainTag.offsetHeight}px`;
    }
  },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  #changeip {
    height: 20px;
    flex: 0 1 auto;
    text-align: center;
    background: black;
    color: #a4ca4a;
  }
  article {
    margin: 0;
    padding: 0;

    display: flex;
    flex-direction: row;
    flex: 1 1 auto;
    flex-wrap: wrap;
  }

  section {
    flex: 1 1 70%;
    background: linear-gradient(black, dimgray);

    display: flex;
    flex-direction: column;
  }
  
  section:first-child {
    flex: 1 0 auto;
  }

  section > header {
      padding: 0vh 0.2vw;
      margin-bottom: 3vh;
      background: black;
      border-bottom: 3px solid black;
      flex:  1 0 10%;
      max-height: 10%;

      display: flex;
      flex-direction: column;
      align-items: center;
  }

  section:hover > header {
      border-bottom: 3px solid #a4ca4a;
      transition: 2s;
  }

  section > header h2 {
    padding: 1px;
    margin: 0;
    color: white;

    display: flex;
    flex-direction: row;
    align-items: center;
  }


</style>
