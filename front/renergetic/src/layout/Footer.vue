<template>
    <div  @click="showLeftMenu">Click to {{!showMenu?'show':'hide'}} left menu</div>
</template>

<script>
import layoutBus from '@/events/layout.js';

export default {
    name: 'Footer',
    data(){
        return {showMenu: false}
    },
    methods: {
        showLeftMenu(){
            this.showMenu = !this.showMenu;
            
            layoutBus.emit('show', {view: 'leftmenu', show: this.showMenu});
        }
    },
    mounted(){
        window.addEventListener('DOMContentLoaded',()=>{
            console.log('borrado | '+this.showMenu);
            layoutBus.emit('show', {view: 'leftmenu', show: this.showMenu});
        });
    }
}
</script>

<style scoped>
    div {
        flex: 1 1 100%;
        text-align: center;
        background:black;
        color: #a4ca4a;
        border-top: 2px solid #a4ca4a;
    }
</style>
