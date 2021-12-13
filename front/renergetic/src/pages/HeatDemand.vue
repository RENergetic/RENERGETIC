<template>
    <article id="heatdemand">
        <iframe title='graph' :src="graphicUrl"></iframe>
    </article>
</template>
        
<script>
    export default {
        data() {
            return {
                graphicUrl: 'http://localhost:3000/d/cRQGpjpnk/consumption-forecasting?orgId=2&from=1606816726788&to=1638352726788&theme=light&panelId=2',
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