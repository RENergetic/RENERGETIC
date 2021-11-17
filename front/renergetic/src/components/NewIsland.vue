<template>
    <div>
        <input type="text" v-model="inputName" placeholder="Nombre de la isla"/><br>
        <input type="text" v-model="inputLocation" placeholder="Ubicacion de la isla" @keyup.enter="addIsland"/><br>
        <button @click="addIsland">A&ntilde;adir</button>
    </div>
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
            if (this.inputName && this.inputLocation) {
                axios.defaults.headers.post['Access-Control-Allow-Origin'] = '*';
                axios.defaults.headers.post['Access-Control-Allow-Credentials'] = 'true';
                axios.defaults.headers.post['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS';
                axios.defaults.headers.post['Access-Control-Allow-Headers'] = 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type';

                axios
                .post(this.ip, 
                    {name:this.inputName, location:this.inputLocation})
                .then(() => {
                    this.inputName = "";
                    this.inputLocation = "";
                    this.$emit("event-add");
                    })
                .catch(/*error*/() => {
                    alert('Hola')
                    /*console.warn(error.message);
                    console.warn(`No se puede conectar a ${this.ip}`);*/
                });
            }
        }
    },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    div {
        display: flex;
        align-items: center;
        flex-direction: column;
    }

    input[type="text"] {
        padding: 0.2vh 1vw;
        margin: 1vh 0;
        width: 70%;

        border: 2px inset gray;
        border-radius: 10px;
        font-size: calc(0.8vw + 0.7em);
        font-weight: bolder;
        text-align: center;

        flex: 0 1 auto;
    }

    input[type="text"]::placeholder {
        color: #94ba3a;
    }

    input[type="text"]:focus, input[type="text"]:hover {
        outline: 0 !important;
        border: 2px inset #a4ca4a;

        transition: 0.5s;
    }

    button {
        padding: 0.4vh 1vw;
        margin: 0;
        width:80%;

        font-weight: bold;
        text-align: center;
        color: white;
        background: black;
        border: 2px inset gray;

        border-radius: 10px;
        font-size: calc(0.8vw + 0.5em);
        outline: none;
        cursor: pointer;
        
        flex: 0 1 auto;
    }

    button:hover {
        border-color: #a4ca4a;
        outline: 2px solid #a4ca4a;
        offset: 1px;
        transition: 0.15s;
    }

    button:active {
        color: #94ba3a;
        transition: 0.75s;
    }
</style>
