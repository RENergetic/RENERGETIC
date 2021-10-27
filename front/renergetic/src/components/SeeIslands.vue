<template>
    <section>
        <section>
            <table>
                <thead>
                    <tr>
                        <th>Datos del servidor</th>
                        <th>Isla</th>
                        <th>Ubicaci&oacute;n</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="isle of islands" :key="isle.id">
                        <td>{{isle}}</td>
                        <td>{{isle.name}}</td>
                        <td>{{isle.location}}</td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <button @click="listIslands">Actualizar tabla</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </section>
    </section>
</template>

<script>
import axios from 'axios';
export default {
    name: 'SeeIslands',

    props: {
        ip: String
    },
    
    data() {
        return{
            islands:[]
        }
    },

    methods: {
        listIslands(){
            axios
            .get(this.ip, {
                headers: {"Access-Control-Allow-Origin": "*"}
            })
            .then(response => (this.islands = response.data));
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

    table {
        border: 3px solid wheat;
        border-radius: 2vw;
        padding: 1em;
    }

    thead th{
        border-bottom: 3px solid wheat;
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

    button {
        padding: 0.6vh 1.5vw;
        margin: 0;
        width: 90%;
        margin-top: 1em;
        height: calc(0.5vh + 2em);
        font-weight: bold;
        text-align: center;
        border: 2px solid wheat;
        border-radius: 2em;
        outline: none;
        cursor: pointer;
    }

    button:hover {
        outline: 2px solid wheat;
        offset: 1px;
    }

    button:active {
        background: wheat;
    }
</style>
