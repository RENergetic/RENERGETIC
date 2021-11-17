<template>
    <Islands :ip='ip'></Islands>
</template>

<script>
import Islands from '@/pages/Islands.vue'

export default {
    name: 'Main',
    props: {
        ip: String
    },
    components: {
        Islands
    }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
