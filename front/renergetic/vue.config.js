// Opciones de configuración de Vue.config.js

module.exports = {

    // opciones
   
    // ruta base
   
   publicPath: "./",
   
    // Directorio de salida al construir
   
   outputDir: "dist",
   
    // El directorio donde se colocan los recursos estáticos
   
   assetsDir: "static",
   
    // ruta de salida html
   
   indexPath: "index.html",
   
    // Hash de nombre de archivo
   
   filenameHashing: true,
   
    // Utilizado para la configuración de varias páginas, el valor predeterminado es indefinido
   
   pages: {
   
       index: {
   
                    // archivo de entrada de página
   
           entry: 'src/main.js',
   
                    // archivo de plantilla
   
           template: 'public/index.html',
   
                    // archivo de salida en dist / index.html
   
           filename: 'index.html',
   
                    // Al usar la opción de título de página,
   
                    // La etiqueta del título en la plantilla debe ser <title> <% = htmlWebpackPlugin.options.title%> </title>
   
           title: 'RENergetic',
   
                    // Los bloques incluidos en esta página contendrán por defecto
   
                    // El fragmento general extraído y el fragmento del proveedor.
   
           chunks: ['chunk-vendors', 'chunk-common', 'index']
   
       },
   
            // Cuando se usa un formato de cadena de solo entrada,
   
            // El archivo de plantilla predeterminado es `public / subpage.html`
   
            // Si no existe, recurra a `public / index.html`.
   
            // El archivo de salida predeterminado es `subpage.html`.
   
       //subpage: 'src/subpage/main.js'
   
   },
   
    // Si se debe usar `eslint-loader` para verificar al guardar.
   
   lintOnSave: true,
   
    // Si usar la versión de compilación completa con el compilador en el navegador
   
   runtimeCompiler: false,
   
    // babel-loader omitirá la dependencia node_modules por defecto.
   
   //transpileDependencies: [ /* string or regex */ ],
   
    // ¿Se genera un mapa fuente para el entorno de producción?
   
   productionSourceMap: false,
   
    // Establezca el atributo de origen cruzado de las etiquetas <link rel = "stylesheet"> y <script> en el HTML generado.
   
   //crossorigin: "",
   
    // Habilite la integridad de los recursos secundarios (SRI) en las etiquetas <link rel = "stylesheet"> y <script> en el HTML generado.
   
   integrity: false,
   
    // Ajustar la configuración del paquete web interno
   
   /*configureWebpack: () => {}, //(Object | Function)
   
   chainWebpack: () => {},*/
   
    // Configure el comportamiento de webpack-dev-server.
   
   /*devServer: {
   
       open: process.platform === 'darwin',
   
       host: '0.0.0.0',
   
       port: 8080,
   
       https: false,
   
       hotOnly: false,
   
            // Ver https://github.com/vuejs/vue-docs-zh-cn/blob/master/vue-cli/cli-service.md#Configure proxy
   
       proxy: {
   
           '/api': {
   
               target: "http://app.rmsdmedia.com",
   
               changeOrigin: true,
   
               secure: false,
   
               pathRewrite: {
   
                   "^/api": ""
   
               }
   
           },
   
           '/foo': {
   
               target: '<other_url>'
   
           }
   
       }, // string | Object
   
       before: app => {}
   
   },*/
   
    // Opciones relacionadas con CSS
   
   css: {
   
            // Extraiga el CSS del componente en un archivo CSS separado (solo se usa en el entorno de producción)
   
            // También puede ser un objeto de opción pasado a `extract-text-webpack-plugin`
   
       extract: true,
   
            // ¿Abrir el mapa fuente CSS?
   
       sourceMap: false,
   
            // Pasar opciones personalizadas para el cargador del preprocesador. Como pasar a
   
            // Para Css-loader, use `{Css: {...}}`.
   
       /*loaderOptions: {
   
           css: {
   
                            // Las opciones aquí se pasarán a css-loader
   
           },
   
           postcss: {
   
                            // Las opciones aquí se pasarán a postcss-loader
   
           }
   
       },*/
   
            // Active los módulos CSS para todos los archivos CSS y preprocesados.
   
            // Esta opción no afecta a los archivos `* .vue`.
   
       modules: false
   
   },
   
    // Usa `thread-loader` para Babel y TypeScript en producción
   
    // Estará habilitado por defecto en máquinas multi-core.
   
   //parallel: require('os').cpus().length > 1,
   
    // Opciones del complemento PWA.
   
    // Ver https://github.com/vuejs/vue-docs-zh-cn/blob/master/vue-cli-plugin-pwa/README.md
   
   //pwa: {},
   
    // Opciones del complemento de terceros
   
   /*pluginOptions: {
   
       // ...
   
   }*/
}   