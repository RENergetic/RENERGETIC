current=$(pwd -W tr / \\)

if test -f "${current}\\application.jar";
then
    java -jar application.jar
else
    echo "application.jar doesn't exist, compile it using 'compileAndRun.sh'"
fi