## Project Structure

* .vscode - config files for visual studio code 
* public - static, resources files a
* src - [project sources](#src-directory) 
* src/assets - [project assets](#assets) 
* src/components - [project components](#components) 
* src/plugins - [project plugins](#plugins) 
* src/router - [vuejs router](#router) 
* src/store - [session data](#store) 
* .env - environmental config, [description](./env.md)

 
### Src directory 
#### Assets 
    
* locale/{language code}
  * index.js -> import file
  * menu.json -> menu headers
  * view.json -> miscellaneous ui keys
  * primveue.json -> primevue components localization
    * model.json -> object's properties/attributes
  * dummy local test data/api/generator
  * img -> img files
  * style -> additional styles   
  * logo.png -> main logo
  
#### Components
reused modules included in the views or other components
* admin -> admin components like: user management, privilages managements, config?
* area -> components related with heatmap/area, TODO: Openstreet map integration
* ev -> electric vehicles modules, TODO:
* dashboard -> data modules, grafana, information panel
  * heatreuse -> heatreuse components, TODO:
  * measurements -> measurements/sensors view, management
* management -> modules for the resource managers (those who have privilages to perform some action on the resource - reduce heatinh, close windows etc)
* miscellaneous - other
  * settings - settings templates
* user 

#### Plugins
* model -> classes/object definitions: [model](./model.md)
* renergetic -> renergetic js modules, accessed by `this.$ren`
  * ren_api + rest.js -> REST API, `this.$ren.{apiName}` , apiName: [] TODO: list apis
  * utils.js -> miscellaneous functions `this.$ren.utils`

#### Router
index.js contains references to:
* admin.js
* dashboard.js
* ev.js
* user.js

#### Store
session storage
TODO: