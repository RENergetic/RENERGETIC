<template>
    <article id='main'>
        <section>
            <header>
                <h2>{{(show_builds == -1)? 'Create island':'Add build'}}</h2>
            </header>
            <NewIsland :ip='ip' @event-add='eventAddTag' :show_builds='show_builds'></NewIsland>
        </section>

        <section>
            <header>
                <h2>List islands</h2>
            </header>
            <SeeIslands :ip='ip' @event-builds='changeShowBuilds' ref='seeTag' :show_builds='show_builds'></SeeIslands>
        </section>
    </article>
</template>
<script>
import NewIsland from '@/components/NewIsland.vue'
import SeeIslands from '@/components/SeeIslands.vue'

export default {
    name: 'Islands',
    data() {
        return {
            ip: 'http://127.0.0.1/api/',
            show_builds: -1
        }
    },
    components: {
        NewIsland,
        SeeIslands
    },
    methods: {
        eventAddTag() {
            this.$refs.seeTag.listIslands();
        },

        changeShowBuilds(islandId){
            this.show_builds = islandId;
        },

        resizeMain(){ 
            /*const seeIslandsTag = this.$refs.seeTag;
            seeIslandsTag.style.maxHeight = `${seeIslandsTag.clientHeight}px` 
            for (const tag of mainTag.getElementsByTagName('section')) {
                tag.style.maxHeight = `${mainTag.clientHeight}px`;
            }*/
        },
    },
    mounted() {
        this.resizeMain();
        window.addEventListener("resize", this.resizeMain);
    },
    unmounted() {
        window.removeEventListener("resize", this.resizeMain);
    },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    article {
        margin: 0;
        padding: 0;

        display: flex;
        flex-direction: row;
        flex: 1 1 auto;
        flex-wrap: wrap;
    }

    section {
        background: linear-gradient(var(--background), #cde29acc);
        display: flex;
        flex-direction: column;
    }
    
    section:first-child {
        flex: 1 0 auto;
    }

    section:last-child {
        flex: 0 1 70%;
        height: 100%;
    }

    section > header {
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

    section:hover > header {
            border-bottom: 3px solid #a4ca4a;
            transition: 2s;
    }

    section > header h2 {
        padding: 1px;
        margin: 0;
        color: #a4ca4a;

        display: flex;
        flex-direction: row;
        align-items: center;
    }

</style>
