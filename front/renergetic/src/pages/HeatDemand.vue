<template>
    <article id="heatdemand">
        <iframe title='graph' :src="graphicUrl"></iframe>
    </article>
</template>
        
<script>
    export default {
        data() {
            return {
                graphicUrl: 'http://localhost:3000/d-solo/dAXoahpnz/renergetic?orgId=2&from=1635579454128&to=1638175054128&panelId=2',
            }
        }
    }
</script>

<style scoped>
    #heatdemand{
        display: flex;
        flex: 1 0 100%;
        flex-direction: row;
        background: black;
        align-items: center;
    }
    iframe {
        height: 80%;
        flex: 0 0 80%;
        margin: auto;
    }
</style>