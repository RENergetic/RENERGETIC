<template>
    <div>
        <table v-if="show_builds == -1">
            <caption style='display: none'>Listado de islas</caption>
            <thead>
                <tr>
                    <th scope='col'>Server data</th>
                    <th scope='col'>Island</th>
                    <th scope='col'>Location</th>
                </tr>
                <tr>
                    <th colspan="3" scope='row'> <hr/> </th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="isle of islands" :key="isle.id" :id="'isle'+isle.id">
                    <td><button class="show_builds" @click="showBuilds(isle.id)">Show Buildings</button></td>
                    <td>{{isle.name}}</td>
                    <td>{{isle.location}}</td>
                </tr>
                <tr>
                    <td colspan="3">
                        <button @click="listIslands">Update table</button>
                    </td>
                </tr>
            </tbody>
        </table>
        <table v-else>
            <caption style='display: none'>Listado de islas</caption>
            <thead>
                <tr>
                    <th scope='col'>Building</th>
                    <th scope='col'>Description</th>
                    <th scope='col'>Location</th>
                </tr>
                <tr>
                    <th colspan="3" scope='row'> <hr/> </th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="build of builds" :key="build.id">
                    <td>{{build.name}}</td>
                    <td>{{build.description}}</td>
                    <td>{{build.idIsland}}</td>
                </tr>
                <tr>
                    <td colspan="3">
                        <button @click="hideBuilds()">Go Back</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
import axios from 'axios';
export default {
    name: 'SeeIslands',

    props: {
        ip: String,
        show_builds: Number
    },
    
    data() {
        return{
            islands:[],
            builds:[]
        }
    },

    methods: {
        
        listIslands(){
            axios.defaults.headers.get['Access-Control-Allow-Origin'] = '*';
            axios.defaults.headers.get['Access-Control-Allow-Credentials'] = 'true';
            axios.defaults.headers.get['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS';
            axios.defaults.headers.get['Access-Control-Allow-Headers'] = 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,apikey';
            //axios.defaults.headers.get['apikey'] = process.env.VUE_APP_API_KEY;

            if (this.show_builds == -1){
                axios
                .get(this.ip + process.env.VUE_APP_API_ISLANDS_PATH)
                .then(response => (this.islands = response.data))
                .catch(error => {
                    console.warn(error.message);
                    console.warn(`No se puede conectar a ${this.ip}`);
                    this.islands = [];
                });
            } else{
                axios
                .get(this.ip + process.env.VUE_APP_API_BUILDINGS_PATH, {params: {islandId:this.show_builds}})
                .then(response => (this.builds = response.data))
                .catch(error => {
                    console.warn(error.message);
                    console.warn(`No se puede conectar a ${this.ip}`);
                    this.builds = [];
                });
            }
        },
        showBuilds(island_id) {
            axios.defaults.headers.get['Access-Control-Allow-Origin'] = '*';
            axios.defaults.headers.get['Access-Control-Allow-Credentials'] = 'true';
            axios.defaults.headers.get['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS';
            axios.defaults.headers.get['Access-Control-Allow-Headers'] = 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,apikey';
            //axios.defaults.headers.get['apikey'] = process.env.VUE_APP_API_KEY;

            this.$emit('event-builds', island_id);
            
            axios
            .get(this.ip + process.env.VUE_APP_API_BUILDINGS_PATH, {params: {islandId: island_id}})
            .then(response => (this.builds = response.data))
            .catch(error => {
                console.warn(error.message);
                console.warn(`No se puede conectar a ${this.ip}`);
                this.builds = [];
            });
        },
        hideBuilds() {
            this.$emit('event-builds', -1);
        }
    },

    mounted() {
        this.listIslands();
    },

    watch: {
        ip: function() {this.listIslands();}
    }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    div {
        display: flex;
        align-items: center;
        flex-direction: column;

        padding: 2vh 2vw;
        overflow: auto;
        flex: 0 1 auto;
        flex-wrap: nowrap;
    }

    table {
        flex: 1 0 auto;
        border: 3px inset dimgray;
        background: var(--background);
        border-radius: 2vw;
        border-collapse: separate;
        padding: 1em;
    }

    table hr {
        border-top: 2px inset dimgray;
    }

    table:hover th {
        color: #94ba3a;
        transition: 0.75s;
    }

    table:hover, table:hover hr{
        border-color: #a4ca4a;
        transition: 0.75s;
    }

    table *{
        color: dimgray;
    }

    tr, td {
        text-align: center;
        padding: 0.4vh 1.5vw;
    }

    tbody tr:first-child td {
        padding-top: 1em;
    }

    tr:last-child,tr:last-child td:last-child {
        text-align: center;
        padding: 0;
        margin: 0;
    }

    .show_builds {
        width: max-content;
        margin-top: 0.3em;
    }

    button {
        padding: 0.6vh 1.5vw;
        margin: 0;
        width: 90%;
        margin-top: 1em;
        height: calc(0.5vh + 2em);
        font-weight: bold;
        text-align: center;
        background: var(--background);
        border: 2px inset gray;
        border-radius: 0.75em;
        outline: none;
        cursor: pointer;
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
