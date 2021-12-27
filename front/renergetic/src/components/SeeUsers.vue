<template>
    <div>
        <table v-if="!edit_user">
            <caption style='display: none'>Listado de islas</caption>
            <thead>
                <tr>
                    <th scope='col'>Username</th>
                    <th scope='col'>Name</th>
                    <th scope='col'>Roles</th>
                </tr>
                <tr>
                    <th colspan="3" scope='row'> <hr/> </th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="user of users" :key="user.id" :id="'user'+user.id">
                    <td>{{user.username}}</td>
                    <td>{{user.name}}</td>
                    <td>{{user.roles.join(', ')}}</td>
                    <td><button class="edit_user" @click="editUser(user)">Edit</button></td>
                </tr>
                <tr>
                    <td colspan="4">
                        <button @click="getUsers">Update table</button>
                    </td>
                </tr>
            </tbody>
        </table>
        <table v-else>
            <thead>
                <th colspan="2">{{`${user.username}'s roles`}}</th>
            </thead>
            <tbody>
                <tr v-for="role of user.roles" :key="role" >
                    <td colspan="2">{{role}}</td>
                </tr>
                <tr v-if="available_roles.length > 0">
                    <td>
                        <select name="role_select" id='role' @change='selectRole($event.target.value)'>
                            <option :value="role.name" v-for='role of available_roles' :key='role.name'>{{role.name}}</option>
                        </select>
                    </td>
                    <td>
                        <button @click='addRole'>Add</button>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <button @click="editUser(undefined)">Back</button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
import Keycloak from '@/plugins/authentication.js'
export default {
    name: 'SeeUsers',    
    data() {
        return{
            edit_user: false,
            user: undefined,
            available_roles: [],
            selected_role: undefined,
            users:[]
        }
    },

    methods: {
        async getUsers(){
            this.users = [];
            this.users = await Keycloak.getUsers()
            for(let user of this.users) {
                let roles = [];
                for(let role of user.roles)
                    roles.push(role.name);
                user.roles = roles;
            }
        },
        editUser(user = undefined) {
            this.user = user;

            if (this.user != undefined) {
                this.getAvailableRoles();
                this.edit_user = true;
            } else {
                this.getUsers();
                this.edit_user=false;
            }
        },
        getUser(id) {
            for(let user of this.users)
                if (user.id == id)
                    return user;
        },
        async getAvailableRoles(){
            this.available_roles = [];
            let roles = await Keycloak.getClientRoles();

            for (let role of roles.data)
                if(!this.user.roles.includes(role.name))
                    this.available_roles.push(role);

            if (this.available_roles.length > 0)
                this.selected_role = this.available_roles[0].name;
        },
        addRole(){
            for (let role of this.available_roles)
                if (role.name == this.selected_role) {
                    console.log(role);
                    Keycloak.assignRolesToUser(this.user.id, Array(role));
                    this.user.roles.push(role.name);
                    this.available_roles.splice(this.available_roles.indexOf(role), 1);
                    break;
                }
        },
        selectRole(value) {
            this.selected_role = value;
        }
    },
    mounted(){
        this.getUsers();
    }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    div {
        display: flex;
        align-items: center;
        flex-direction: column;

        padding: 2vh 2vw;
        overflow: auto;
        flex: 0 1 auto;
        flex-wrap: nowrap;
    }

    table {
        flex: 1 0 auto;
        border: 3px inset dimgray;
        background: var(--background);
        border-radius: 2vw;
        border-collapse: separate;
        padding: 1em;
    }

    table hr {
        border-top: 2px inset dimgray;
    }

    table:hover th {
        color: #94ba3a;
        transition: 0.75s;
    }

    table:hover, table:hover hr{
        border-color: #a4ca4a;
        transition: 0.75s;
    }

    table *{
        color: dimgray;
    }

    tr, td {
        text-align: center;
        padding: 0.4vh 1.5vw;
    }

    tbody tr:first-child td {
        padding-top: 1em;
    }

    tr:last-child,tr:last-child td:last-child {
        text-align: center;
        padding: 0;
        margin: 0;
    }

    .edit_user {
        width: max-content;
        margin-top: 0.3em;
    }

    button {
        padding: 0.6vh 1.5vw;
        margin: 0;
        width: 90%;
        margin-top: 1em;
        height: calc(0.5vh + 2em);
        font-weight: bold;
        text-align: center;
        background: var(--background);
        border: 2px inset gray;
        border-radius: 0.75em;
        outline: none;
        cursor: pointer;
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
