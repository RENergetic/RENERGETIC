<template>
  <div>
    TODO: view roles
    <DataTable v-model:expandedRows="users.roles" :value="users" data-key="id" responsive-layout="scroll">
      <Column :expander="true" header-style="width: 3rem" />
      <Column field="username" header="Username" sortable></Column>
      <Column field="name" header="Name" sortable></Column>
      <Column field="email" header="Email" sortable></Column>
      <Column :exportable="false" style="min-width: 8rem">
        <template #body="user">
          <Button
            icon="pi pi-user-edit"
            class="p-button-rounded p-button-warning mr-2"
            @click="
              userToEdit = user.data;
              addUserDialog = true;
            "
          />
          <Button
            icon="pi pi-trash"
            class="p-button-rounded p-button-danger"
            @click="openDialog('deleteUser', user.data)"
          />
        </template>
      </Column>
      <template #expansion="user">
        <div
          v-if="user.data.roles != undefined && user.data.roles.length > 0"
          class="orders-subtable"
          style="margin: 0 2rem"
        >
          <DataTable :value="user.data.roles" responsive-layout="scroll">
            <Column :exportable="false" style="min-width: 8rem">
              <template #body="role">
                <Button
                  icon="pi pi-trash"
                  class="p-button-rounded p-button-danger"
                  @click="openDialog('deleteRole', user.data, role.data)"
                />
              </template>
            </Column>
            <Column field="name" header="Role Name" sortable></Column>
          </DataTable>
        </div>
        <Message v-else severity="info">{{ `${user.data.username} haven't assigned roles` }}</Message>
        <div v-if="availableRoles(user.data).length > 0" class="grid p-fluid" style="margin: 0 2rem">
          <div class="col-12 md:col-4">
            <div class="p-inputgroup">
              <select
                class="p-component"
                style="width: 100%; text-align: center"
                @change="selectRole($event, user.data)"
              >
                <option v-for="role of availableRoles(user.data)" :key="role.id" :value="JSON.stringify(role)">
                  {{ role.name }}
                </option>
              </select>
              <Button icon="pi pi-plus" class="p-button-success" @click="addRole(user.data)" />
            </div>
          </div>
        </div>
      </template>
    </DataTable>
    <Toolbar>
      <template #start>
        <Button
          label="Add new user"
          icon="pi pi-user-plus"
          class="mr-2 p-button-success"
          @click="
            userToEdit = undefined;
            addUserDialog = true;
          "
        />
      </template>
    </Toolbar>
    <Dialog
      v-model:visible="deleteDialog.visible"
      :style="{ width: '450px' }"
      :header="deleteDialog.header"
      :modal="true"
    >
      <div class="confirmation-content">
        <i class="pi pi-exclamation-circle mr-3" style="font-size: 2rem; margin: 0em 0.4em" />
        <span v-if="userToChange">{{ deleteDialog.text }}</span>
      </div>
      <template #footer>
        <Button
          label="No"
          icon="pi pi-times"
          class="p-button-text"
          style="color: var(--orange-300)"
          @click="deleteDialog.visible = false"
        />
        <Button label="Yes" icon="pi pi-check" class="p-button-text" @click="deleteResource()" />
      </template>
    </Dialog>
    <UserAdd :users="users" :edit-user="userToEdit" :visible="addUserDialog" @close="closeAddUserDialog"></UserAdd>
  </div>
</template>

<script>
import UserAdd from "@/components/admin/UserAdd.vue";

export default {
  name: "Users",
  components: {
    UserAdd,
  },
  data() {
    return {
      users: [],
      roles: [],
      userToEdit: undefined,
      userToChange: undefined,
      roleToChange: undefined,
      deleteDialog: {
        visible: false,
        header: "Delete",
        text: "Are you sure to delete this",
        delete: undefined, // user | role,
      },
      addUserDialog: false,
    };
  },
  async created() {
    this.users = await this.$keycloak.getUsers();
    this.roles = await this.$keycloak.getClientRoles();
  },
  mounted() {},
  methods: {
    availableRoles(user) {
      let availableRoles = this.roles.map((object) => ({ ...object }));
      for (let role of user.roles) {
        availableRoles.splice(availableRoles.map((r) => r.id).indexOf(role.id), 1);
      }
      if (availableRoles != undefined && availableRoles.length > 0) user.addRole = availableRoles[0];
      return availableRoles;
    },
    openDialog(dialogName, user, role = undefined) {
      this.userToChange = user;
      this.roleToChange = role;
      switch (dialogName) {
        case "deleteRole":
          this.deleteDialog.delete = "role";
          this.deleteDialog.header = "Delete Role";
          this.deleteDialog.text = `Are you sure to delete ${role.name} role from ${user.username}?`;
          this.deleteDialog.visible = true;
          break;
        case "deleteUser":
          this.deleteDialog.delete = "user";
          this.deleteDialog.header = "Delete User";
          this.deleteDialog.text = `Are you sure to delete ${user.username}? This action cannot be reversed`;
          this.deleteDialog.visible = true;
          break;
        default:
          this.deleteDialog.delete = undefined;
          break;
      }
    },
    async closeAddUserDialog(value) {
      if (value != undefined) {
        this.users = await this.$keycloak.getUsers();
        console.log(this.users);
      }
      this.addUserDialog = false;
    },
    addRole(user) {
      if (user.addRole != undefined) {
        console.error(user.addRole);
        this.$keycloak.assignRolesToUser(user.id, Array(user.addRole));
        user.roles.push(user.addRole);
      }
    },
    selectRole(event, user) {
      user.addRole = JSON.parse(event.target.value);
    },
    deleteResource() {
      if (this.deleteDialog.delete == "role" && this.userToChange && this.roleToChange) {
        let userFromList = this.users[this.users.indexOf(this.userToChange)];
        console.error(userFromList);
        this.$keycloak.unAssignRolesToUser(this.userToChange.id, Array(this.roleToChange));
        userFromList.roles.splice(userFromList.roles.indexOf(this.roleToChange), 1);
        this.deleteDialog.visible = false;
        this.deleteDialog.delete = undefined;
      } else if (this.deleteDialog.delete == "user" && this.userToChange) {
        this.$keycloak.deleteUser(this.userToChange.id);
        this.users.splice(this.users.indexOf(this.userToChange), 1);
        this.deleteDialog.visible = false;
        this.deleteDialog.delete = undefined;
      }
    },
    /*onRowExpand(event) {
            this.$toast.add({severity: 'info', summary: 'Product Expanded', detail: event.data.name, life: 3000});
        },
        onRowCollapse(event) {
            this.$toast.add({severity: 'success', summary: 'Product Collapsed', detail: event.data.name, life: 3000});
        }*/
  },
};
</script>

<style lang="scss" scoped>
.confirmation-content {
  display: flex;
  align-items: center;
  justify-content: center;
}
@media screen and (max-width: 960px) {
  ::v-deep(.p-toolbar) {
    flex-wrap: wrap;

    .p-button {
      margin-bottom: 0.25rem;
    }
  }
}
</style>
