<template>
    <div id="changeip" @click='changeip'>{{ip}}</div>
</template>

<script>
export default {
    name: 'TopMenu',
    props: {
        ip: String
    },
    methods: {
        changeip(){
            if (this.ip === 'http://127.0.0.1/api/islands')
                this.$emit('changeip', 'http://backdb-np/api/islands');
            else if (this.ip === 'http://backdb-np/api/islands') 
                this.$emit('changeip', 'http://backdb/api/islands');
            else
                this.$emit('changeip', 'http://127.0.0.1/api/islands');
        }
    }
}
</script>

<style scoped>
    #changeip {
        flex: 1 1 100%;
        text-align: center;
        background:black;
        color: #a4ca4a;
    }
</style>
