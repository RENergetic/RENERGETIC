from dataclasses import dataclass
from requests import request, Response
from types import MappingProxyType
from threading import Thread
from time import sleep
import random
import maya

@dataclass
class WeatherAPI:
    country: str
    city: str
    __URL: str = "https://api.weatherbit.io/v2.0"
    __KEYS: tuple[str] = ("ff58bcc61d0445199d950a6fa6d15489", "41d8d19e64bc4e0c944ae392644056be")
    __PATHS: MappingProxyType = MappingProxyType({
        "usage": "/subscription/usage",
        "current": "/current"
    })
    __REQUEST_LIMIT: int = 50

    @property
    def url(self):
        return self.__URL

    def getValidKey(self):
        for key in self.__KEYS:
            if (self.keyUssage(key)["calls_count"] == None or int(self.keyUssage(key)["calls_count"]) < self.__REQUEST_LIMIT):
                return key
        return None

    def createPath(self, apiKey:str, pathKey: str):
        if pathKey == "current":
            return f'{self.__URL}{self.__PATHS[pathKey]}?country={self.country}&city={self.city}&key={apiKey}'
        elif pathKey == "usage":
            return f'{self.__URL}{self.__PATHS[pathKey]}?key={apiKey}'

    def keyUssage(self, key):
        return request("GET", self.createPath(key, "usage"), headers={}, data={}).json()

    def current(self):
        tries = 0
        key = self.getValidKey()
        while key != None:
            response = request("GET", self.createPath(key, "current"), headers={}, data={})
            if response.status_code < 300:
                print(' - Weather data obtained')
                return response.json()
            elif tries > 5:
                print(' - Error maximum attempts reached')
                return None
            else:
                print(' - Error getting weather data, retrying')
                tries += 1
        print(' - All Keys limits reached')

def insertInflux(url, payload):
    response = request("POST", url, headers={"Content-Type": "application/json"}, json=payload)
    if response.status_code >= 300: 
        print(f' - Error inserting data at {maya.when("now").iso8601()[0:19].replace("T", " ")}')
        print(payload)
        print(response.content)
    else: 
        print(f' - Inserted data at {maya.when("now").iso8601()[0:19].replace("T", " ")}')
        print(payload)


def generateRandomizedInfluxData(influxApiUrl: str, wheatherAPI: WeatherAPI, assetList: list[str]):
    print(' - Generating data at ' + maya.when("now").iso8601()[0:19].replace("T", " "))
    fields = {}

    dataWeather = wheatherAPI.current()["data"][0]
    for assetName in assetList:
        fields['temperature'] = dataWeather["temp"] + round(random.uniform(-1.5, 1.5),1)
        fields['sun_radiation'] = int(round(dataWeather["solar_rad"] * random.uniform(0.95, 1.05), 0))
        fields['wind_speed'] = dataWeather["wind_spd"] * random.uniform(0.95, 1.05)
        fields['wind_direction'] = dataWeather["wind_dir"] * random.uniform(0.95, 1.05)
        fields['global_irradiance'] = dataWeather["ghi"] * random.uniform(0.95, 1.05)

        time = maya.when("now")
        for field in fields:
            payload = {
                "bucket": "renergetic",
                "measurement": "weather_API",
                "fields":{
                    field: fields[field],
                    "time": time.iso8601()[0:19].replace("T", " ")
                },
                "tags":{
                    "domain": "weather",
                    "typeData": "simulated",
                    "direction": "None",
                    "prediction_window": "Oh",
                    "asset_name": assetName,
                    "time_prediction": time.epoch
                }
            }
            Thread(target=insertInflux, args=[influxApiUrl, payload]).start()

api = WeatherAPI('PL', 'Poznan')
assets = [
    'building1',
    'building2',
    'altair',
    'datacenter1',
    'eagle',
    'flat1',
    'hvac',
    'office',
    'psnc_garden',
    'solar_collector1',
    'pv_panel_1',
    'wind_farm_1',
    'cogenerator_1',
    'cogenerator_2'
]

while True:
    try:
        if maya.when("now").minute % 15 == 0:
            generateRandomizedInfluxData("http://influx-api-sv.ren-prototype.svc.cluster.local:8082/api/measurement", api, assets)
            sleep(300)
    except KeyboardInterrupt:
        print(" - The program has been stopped manually")
        break