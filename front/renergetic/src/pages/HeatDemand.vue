<template>
    <article id="heatdemand">
        <iframe title='graph' :src="graphicUrl"></iframe>
    </article>
</template>
        
<script>
    export default {
        data() {
            return {
                graphicUrl: 'http://localhost:3000/d-solo/nq5m1s0nk/renergetic-forecast?orgId=1&from=1634427441815&to=1636724258711&panelId=2',
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