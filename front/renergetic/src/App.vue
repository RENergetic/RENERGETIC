<template>
    <router-view />
    <header id = 'lytheader'><Header/></header>
    <div>
        <nav id = 'lytleftmenu'><LeftMenu/></nav>
        <div>
            <nav id = 'lyttopmenu'><TopMenu :ip = 'ip' @changeip = 'onchangeip'/></nav>
            <article id = 'lytmain'><Main :ip = 'ip'/></article>
        </div>
    </div>
    <footer id = 'lytfooter'><Footer/></footer>
</template>

<script>
// IMPORT VIEW COMPONENTS
import Header from '@/layout/Header.vue'
import TopMenu from '@/layout/TopMenu.vue'
import LeftMenu from '@/layout/LeftMenu.vue'
import Main from '@/layout/Main.vue'
import Footer from '@/layout/Footer.vue'

// IMPORT EVENT LISTENER
import layoutBus from '@/events/layout.js';

export default {
    name: 'App',

    data() {
        return {
            ip: 'http://127.0.0.1/api/islands'
        };
    },

    components: {
        Header,
        TopMenu,
        LeftMenu,
        Main,
        Footer
    },

    mounted() {
        layoutBus.on('show', this.changeVisibility);
    },
    methods: {
        onchangeip(ip) {
            this.ip = ip;
        },

        changeVisibility(event){
                console.log(`#app .${event.view}`);
                document.querySelector(`#lyt${event.view.toLowerCase()}`).style = `display: ${event.show?'flex':'none'}`;
        }
    }
}
</script>

<style scoped>
    #app > #lytheader {
        flex: 0 1 10%;
        display: flex;
        flex-direction: row;
    }

    #app > div {
        flex: 1 1 auto;
        display: flex;
        flex-direction: row;
    }

    #app > div > #lytleftmenu {
        flex: 1 1 20%;
        display: flex;
        flex-direction: row;
    }

    #app > div > div {
        flex: 1 0 70%;
        display: flex;
        flex-direction: column;
    }

    #app > div > div > #lyttopmenu {
        flex: 0 1 0%;
        display: flex;
        flex-direction: row;
    }

    #app > div > div > #lytmain {
        flex: 1 0 90%;
        display: flex;
        flex-direction: row;
    }

    #app > .footer {
        flex: 0 1 0%;
        display: flex;
        flex-direction: row;
    }
</style>
