<template>
    <div>
        <input type="text" v-model="username" placeholder="Username*"/><br>
        <input type="text" v-model="firstName" placeholder="First Name"/><br>
        <input type="text" v-model="lastName" placeholder="Last Name"/><br>
        <input type="text" v-model="email" placeholder="Email"/><br>
        <input type="text" v-model="password" placeholder="Password*" @keyup.enter="addUser"/><br>
        <button @click="addUser">Create</button>
    </div>
</template>

<script>
import Keycloak from '@/plugins/authentication.js'
export default {
    name: 'NewUser',
    data() {
        return{
            username: '',
            firstName: '',
            lastName: '',
            email: '',
            password: ''
        }
    },    
    methods: {
        addUser(){
            if(this.username != '' && this.password != '') {
                let body = {
                    enabled:true,
                    attributes:{},
                    groups:[],
                    username:this.username? this.username : '',
                    firstName: this.firstName? this.firstName : '',
                    lastName: this.lastName? this.lastName : '',
                    email: this.email? this.email : '',
                    emailVerified:"false",
                    credentials:[{type:"password", value:this.password, temporary:false}]
                };

                Keycloak.createUser(body).then(()=> {
                    this.username = '';
                    this.firstName = '';
                    this.lastName = '';
                    this.email = '';
                    this.password = '';
                    this.$emit("event-add");
                });
            }
        }
    },
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    div {
        display: flex;
        align-items: center;
        flex-direction: column;
    }

    input[type="text"] {
        padding: 0.2vh 1vw;
        margin: 1vh 0;
        width: 70%;

        border: 2px inset gray;
        border-radius: 10px;
        font-size: calc(0.8vw + 0.7em);
        font-weight: bolder;
        text-align: center;

        flex: 0 1 auto;
    }

    input[type="text"]::placeholder {
        color: #94ba3a;
    }

    input[type="text"]:focus, input[type="text"]:hover {
        outline: 0 !important;
        border: 2px inset #a4ca4a;

        transition: 0.5s;
    }

    button {
        padding: 0.4vh 1vw;
        margin: 0;
        width:80%;

        font-weight: bold;
        text-align: center;
        color: dimgray;
        background: var(--background);
        border: 2px inset gray;

        border-radius: 10px;
        font-size: calc(0.8vw + 0.5em);
        outline: none;
        cursor: pointer;
        
        flex: 0 1 auto;
    }

    button:hover {
        border-color: #a4ca4a;
        outline: 2px solid #a4ca4a;
        offset: 1px;
        transition: 0.15s;
    }

    button:active {
        color: #94ba3a;
        transition: 0.75s;
    }
</style>
