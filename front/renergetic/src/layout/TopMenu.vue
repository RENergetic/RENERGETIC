<template>
    <select name="screen" id='screen' @change="goToScreen($event)">
        <option value="islands">Administrar islas energ&eacute;ticas</option>
        <option value="graphs">Ver estad&iacute;siticas</option>
    </select>
</template>

<script>
export default {
    name: 'TopMenu',
    props: {
        ip: String
    },
    methods: {
        goToScreen(event){
            console.log(this.$router)
            if (event.target.value == 'islands'){
                this.$router.replace({ name: "Islands" });
            }else if (event.target.value == 'graphs'){
                this.$router.replace({ name: "HeatDemand" });
            }
        }
    }
}
</script>

<style scoped>
    #screen {
        flex: 1 1 100%;
        text-align: center;
        background:black;
        color: #a4ca4a;
        border: #a4ca4a;
    }
</style>
