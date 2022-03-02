current=$(pwd -W tr / \\)

token=$(grep -ioP "(?<=token=).+" _credentials.properties)

buildimages='true'
compileapps='true'

option=-1
automatic=-1

while getopts "a:b:c:h" flag
do
    case "${flag}" in
        a) automatic=${OPTARG};;
        b) buildimages=${OPTARG};;
        c) compileapps=${OPTARG};;
        h) 
            automatic=-2
            sed -n '/^## INSTALL.SH/,${p;/## PORT/q}' README.md | sed '$d'
            #grep -ioP "(?<=INSTALL.SH =+).+(?<==+)" README.md
        ;;
    esac
done

read -r -d '' menu <<- EOM
What resources do you want to install?
    1. App (Relational Database and APIs to access it, front and WSO2)
    2. Data (InfluxDB and APIs to access it, Grafana and Nifi)
    3. Devops (Tools to developers as Sonarqube)
0. Exit
EOM

echo "Before to start the installation, check the installation scripts configuration"
echo
while [ $option -ne 0 ] && [ $automatic -eq -1 ]
do
    echo "$menu"
    echo 'Select an option: '
    read option
    case $option in
    1)
        cd "${current}\\app"
        clear
        ./installerWindows.sh -b $buildimages -c $compileapps
    ;;
    2)
        cd "${current}\\data"
        clear
        ./installerWindows.sh -b $buildimages -c $compileapps
        cd $current
    ;;
    3)
        cd "${current}\\devops"
        clear
        ./devopsWindows.sh -b $buildimages
        cd $current
    ;;
    esac
    clear
done

if [ $automatic -gt 0 ] && [ $automatic -lt 5 ]
then
    echo 'Automatic installation'
    case $automatic in 
        1)
            cd "${current}\\app"
            ./installerWindows.sh -a -b $buildimages -c $compileapps
        ;;
        2)
            cd "${current}\\data"
            ./installerWindows.sh -a -b $buildimages -c $compileapps
            cd $current
        ;;
        3)
            cd "${current}\\devops"
            ./devopsWindows.sh -a -b $buildimages
            cd $current
        ;;
        4)
            cd "${current}\\app"
            ./installerWindows.sh -a -b $buildimages -c $compileapps
            cd "${current}\\data"
            ./installerWindows.sh -a -b $buildimages -c $compileapps
            cd $current
        ;;
    esac
fi