<template>
    <article id="administration">
    </article>
</template>
        
<script>
    import Keycloak from '@/plugins/authentication.js'
    export default {
        data() {
            return {
                clientId:"",
                roles:[],
                users:[],
            }
        },
        methods: {
            getRoles(){
                if(this.clientId===""){
                    this.roles=Keycloak.getCliendId().then((res)=>{
                        console.log("resclie")
                        console.log(res)
                        this.clientId=res
                        this.getUserAssignedRoles();
                        this.assignRolesToUser();
                        return Keycloak.getRoles(res)
                    })
                }else{
                    this.roles=Keycloak.getRoles(this.clientId)
                }
            },
            getUsers(){
                this.users=Keycloak.getUsers()
            },
            getUserAssignedRoles(){
                Keycloak.getUserAssignedRoles("a826f1a4-0e06-42d2-b201-41fa94a53374",this.clientId); //user id
            },
            createUsers(){
                let body={
                    "enabled":true,
                    "attributes":{},
                    "groups":[],
                    "username":"testAPI2",
                    "emailVerified":""
                }
                this.users=Keycloak.createUsers(body)
            },
            assignRolesToUser(){
                let body=[{
                    id: "384a7e87-4dcf-42fa-9eac-8e6492a4efdb",
                    "name": "user",
                    "composite": false,
                    "clientRole": true,
                    "containerId": "2b21cd25-bcf9-47ec-a0b6-e1012ae686c9"
                }]
                this.users=Keycloak.createUsers("a826f1a4-0e06-42d2-b201-41fa94a53374",this.clientId,body)
            }
        },
        mounted() {
            this.getRoles();
            this.getUsers();
            //this.getUserAssignedRoles();
            //this.createUsers();
        },
    }
</script>

<style scoped>
    #administration{
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