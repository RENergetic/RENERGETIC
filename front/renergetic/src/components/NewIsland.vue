<template>
    <section>
        <input type="text" v-model="inputName" placeholder="Nombre de la isla"/><br>
        <input type="text" v-model="inputLocation" placeholder="Ubicacion de la isla" @keyup.enter="addIsland"/><br>
    </section>
</template>

<script>
import axios from 'axios';
export default {
    name: 'NewIsland',

    props: {
        ip: String
    },

    data() {
        return{
            inputName: '',
            inputLocation: ''
        }
    },    
    methods: {
        addIsland(){
            console.log(`{"name":"${this.inputName}", "location":"${this.inputLocation}"}`);
            if (this.inputName && this.inputLocation) {
                axios
                .post(this.ip, 
                    {name:this.inputName, location:this.inputLocation},
                    {headers: {"Access-Control-Allow-Origin": "*"}})
                .then(() => {
                    this.inputName = "";
                    this.inputLocation = "";});
            }
        }
    },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    section {
        display: flex;
        align-items: center;
        flex-direction: column;
    }

    input[type="text"] {
        padding: 0.2vh 1vw;
        margin: 2vh auto;
        width: 70%;
        font-size: calc(0.8vw + 0.7em);
        font-weight: bolder;
        text-align: center;
    }
</style>
