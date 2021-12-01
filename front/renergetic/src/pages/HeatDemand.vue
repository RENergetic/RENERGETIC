<template>
    <article id="heatdemand">
        <iframe title='graph' :src="graphicUrl"></iframe>
    </article>
</template>
        
<script>
    export default {
        data() {
            return {
                graphicUrl: 'http://localhost:3000/d-solo/VrjTEltnz/renergetic?orgId=2&from=1635753118065&to=1638345118065&theme=light&panelId=2',
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