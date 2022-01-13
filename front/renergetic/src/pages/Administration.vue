<template>
    <article id='main'>
        <section>
            <header>
                <h2>Create user</h2>
            </header>
            <NewUser @event-add='eventAddTag'></NewUser>
        </section>
        <section>
            <header>
                <h2>List users</h2>
            </header>
            <SeeUsers ref='seeTag'></SeeUsers>
        </section>
    </article>
</template>

<script>
import SeeUsers from '@/components/SeeUsers.vue'
import NewUser from '../components/NewUser.vue';
export default {
    name: "Administration",
    components: { SeeUsers, NewUser },
    data() {
        return {
        };
    },
    methods: {
        eventAddTag(){
            this.$refs.seeTag.getUsers();
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