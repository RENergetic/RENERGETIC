current=$(pwd -W tr / \\)

# INSTALL APP, POSTGRES AND POSTGRES APIS
cd "${current}\\app"
clear
./installerWindows.sh

# INSTALL GRAFANA, INFLUX, AND INFLUX APIS
cd "${current}\\data"
clear
./installerWindows.sh
cd $current
