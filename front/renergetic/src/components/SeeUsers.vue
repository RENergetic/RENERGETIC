<template>
    <div>
        <table>
            <caption style='display: none'>Listado de islas</caption>
            <thead>
                <tr>
                    <th scope='col'>UserName</th>
                    <th scope='col'>Name</th>
                    <th scope='col'>Roles</th>
                </tr>
                <tr>
                    <th colspan="3" scope='row'> <hr/> </th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="user of users" :key="user.id" :id="'user'+user.id">
                    <td>{{user.username}}</td>
                    <td>{{user.name}}</td>
                    <td>{{user.roles}}</td>
                </tr>
                <tr>
                    <td colspan="3">
                        <button @click="listUsers">Update table</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
import Keycloak from '@/plugins/authentication.js'
export default {
    name: 'SeeUsers',    
    data() {
        return{
            users:[]
        }
    },

    methods: {
        async getUsers(){
            this.users = await Keycloak.getUsers()
            for(let user of this.users) {
                let roles = [];
                for(let role of user.roles)
                    roles.push(role.name);
                user.roles = roles.join(', ');
            }
        }
    },
    mounted(){
        this.getUsers();
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
