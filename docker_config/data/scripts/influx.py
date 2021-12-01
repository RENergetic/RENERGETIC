# To call this file you should use:
#   python influx.py
# Without parameters create new database called "renergetic"
# You can give to the command the following options (only one option):
#  - To import data from csv file
#       python influx.py -i file_path
#  - To delete a measurement
#       python influx.py -d myMeasurement

import importlib.util as check_import
import subprocess
from sys import argv, executable
from os.path import isfile

import re
from datetime import datetime

if check_import.find_spec("influxdb") == None:
    try:
        subprocess.check_call([executable, '-m', 'pip', 'install', 'influxdb'])
    except:
        print('Instala el modulo influxdb: python -m pip install influxdb')
        raise

from influxdb import InfluxDBClient as db

database = 'renergetic'
table = 'myMeasurement'
col = 'power'

client = db('localhost', 8086, None, None, 'renergetic')
client.create_database(database)

if (len(argv) > 1):
    if (argv[1] == '-i' and isfile(argv[2])):
        file = open(argv[1], 'r')

        json = list()
        for line in file:
            match = re.match('\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2},.+',line)
            if match != None:
                time, power = match.string.split(',')
                time = datetime.strptime(time, '%Y-%m-%d %H:%M:%S')

                json.append({
                    "measurement": table,
                    "time": time,
                    "fields": {
                        col: int(power)
                    }
                })
                
        client.write_points(json)
    elif (argv[1] == '-d'):
        client.drop_measurement(argv[2])
