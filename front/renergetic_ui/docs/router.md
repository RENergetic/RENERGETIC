### Meta parameter description:
* isAuthenticated: **bool** - true=enable authentication
* layout: **enum** - *standard*, *fullscr*
* roles: **array** - ["ren-manager","ren-technical-manager", "ren-admin", "ren-guest","ren-user","ren-dev","ren-staff","ren-visitor"]. Only allow access to users with one of write roles. TODO:  define default path role
  
  * manager  - TODO: description
  * administrator - TODO: description
  * ~public -  no authorization is required, anyone can view it~ -> can be done by isAuthenticated flag
  * user - default path role (any authenticated user). 
  * ??? 


