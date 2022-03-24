<template>
    <article id="heatdemand">
        <iframe title='graph' :src="graphicUrl"></iframe>
    </article>
</template>
        
<script>
    export default {
        data() {
            return {
                graphicUrl: process.env.VUE_APP_GRAFANA_URL,
            }
        }
    }
</script>

<style scoped>
    #heatdemand{
        display: flex;
        flex: 1 0 100%;
        flex-direction: row;
        background: var(--background);
        align-items: center;
    }
    iframe {
        height: 80%;
        flex: 0 0 80%;
        margin: auto;
    }
</style>