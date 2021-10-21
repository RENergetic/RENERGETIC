
Ejecutar installerWindows.sh desde una consola que acepte comandos Linux y pueda acceder a las variables de entorno de Windows como puede ser la de Git
Se debe ejecutar estando en el Directorio del archivo
Al inicio del Script hay una serie de variables que hay que establecer:
 - java -> Se ha de guardar el Path en el que se encuentra el proyecto de la API en Spring
 - javafile -> Nombre del jar que se genera al ejecutar Maven
 - installdb -> Valor 'true' para que instale la base de datos de postgresql, 'false' si no se quiere instale
 - installapi -> Valor 'true' para compilar, crear la imagen y configurar en kubecl la API