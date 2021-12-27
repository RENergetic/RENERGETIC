/* eslint-disable no-unused-vars */
/* eslint-disable no-unused-vars */
<template>
    <article id='main'>
        <header>
            <h2>Bienvenido</h2>
        </header>
        <table>
            <thead>
                <tr>
                    <th scope='col'>Username</th>
                    <th scope='col'>User</th>
                    <th scope='col'>Roles</th>
                </tr>
                <tr>
                    <th colspan="3" scope='row'> <hr/> </th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="a of users" :key="a.id">
                    <td>{{a.username}}</td>
                    <td>{{`${a.name}`}}</td>
                    <td>{{a.roles}}</td>
                </tr>
                <tr>
                    <td colspan="3">
                        <button @click='prueba'>Go Back</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </article>
</template>

<script>
import Keycloak from '@/plugins/authentication.js'
export default {
    name: 'Roles',
    data() {
        return {
            users: []
        }
    },
    methods: {
        async getUsers(){
            this.users = await Keycloak.getUsers()
        },
        prueba(){this.users.push('perro')}
    },
    mounted(){
        this.getUsers();
    }
}
</script>
