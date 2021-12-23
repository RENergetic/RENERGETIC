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
    name: 'Login',
    data() {
        return {
            user: '',
            paths: []
        }
    },
    methods: {
        getName(){
            if(Keycloak.data != undefined && Keycloak.data.idTokenParsed != undefined){
                console.log(Keycloak.data)
                this.user = Keycloak.data.idTokenParsed.name;
            }
        },
        getPaths(){
            for (const route of this.$router.getRoutes())
                for (let role of Keycloak.data.resourceAccess['renergetic-app'].roles){
                    if ((route.meta.roles == undefined || route.meta.roles.includes(role)) && route.meta.requiresAuth != undefined){
                        this.paths.push({name: route.name, url: route.path});
                        break;
                    }
                }
        }
    },
    mounted(){
        if (Keycloak.ready) {
            this.getName();
            this.getPaths();     
        } else {
            setTimeout(()=>{
                this.getName();
                this.getPaths();
            }, 200)
        } 
    }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    article {
        margin: 0;
        padding: 0;

        display: flex;
        flex-direction: column;
        flex: 1 1 auto;
        flex-wrap: wrap;
    }

    header {
        padding: 0vh 0.2vw;
        margin-bottom: 3vh;
        background: var(--background);
        border-bottom: 3px solid dimgray;
        flex:    1 0 10%;
        max-height: 10%;

        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
    }

    article:hover > header {
        border-bottom: 3px solid #a4ca4a;
        transition: 2s;
    }

    header h2 {
        padding: 1px;
        margin: 0;
        color: #a4ca4a;

        display: flex;
        flex-direction: row;
        align-items: center;
    }

    article > div{
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    div > a {
        display: flex;
        text-decoration: none;
        padding: 0.6vh 1.5vw;
        margin: 0;
        width: 90%;
        margin-top: 1em;
        height: calc(0.5vh + 2em);
        font-weight: bold;
        align-items: center;
        justify-content: center;
        color: gray;
        background: var(--background);
        border: 2px solid #a4ca4a;
        border-radius: 0.75em;
        outline: none;
        cursor: pointer;
    }

    div > a:hover {
        color: #a4ca4a;
        transition: 0.5s;
    }

    .router-link-active {
        display: none;
    }

</style>
