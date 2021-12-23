/* eslint-disable no-unused-vars */
/* eslint-disable no-unused-vars */
<template>
    <article id='main'>
        <header>
            <h2>Bienvenido {{user}}</h2>
        </header>
        <div v-for='path of paths' :key='path.name'>
            <router-link :to='path.url'>{{path.name}}</router-link>
        </div>
    </article>
</template>

<script>
import Keycloak from '@/plugins/authentication.js'
export default {
    name: 'Roles',
    data() {
        return {
            user: '',
            paths: []
        }
    },
    methods: {
        getUsers(){
        }
    },
    mounted(){
        if (Keycloak.ready) {
            this.getUsers();
        } else {
            setTimeout(()=>{
                this.getUsers();
            }, 200)
        } 
    }
}
</script>
