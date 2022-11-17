TODO: restApiName - describe
### VUEJS api
* `this.$ren` -> renergetic plugins reference
  * `this.$ren.{restApiName}` -> REST API
  * `this.$ren.utils` -> utils
* `this.$t` -> i18n plugin
* `this.$store` -> session store
  * view 
  * auth 
  * settings
* `this.$confirm` -> confirm dialog (YES/NO)
* `v-tooltip`  -> tooltip attribute for templates
*  
*   TODO: describe this.$emit("UpdateMenu", null);

###  global events
```$emit("UpdateMenu", args)``` - emit event to menu

### colors
https://materialui.co/colors

### vue.config.js
Project imports
* scss variables import

### store:
$store.getters["view/wrapper"] - main getter for view data

### roles
```
user_role_flag  = user_roles .reduce((flag,user_role)=>flag |user_role,0x0)
flags = RenRoles.REN_ADMIN | RenRoles.REN_TECHNICAL_MANAGER ;
if ((flags & user_role_flag) > 0)
  //has acccess
  
```
