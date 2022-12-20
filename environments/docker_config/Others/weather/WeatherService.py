from dataclasses import dataclass
from requests import get, request, Response
from lxml.html import fromstring
from types import MappingProxyType
from threading import Thread
from time import sleep
import random
import maya

@dataclass
class WeatherAPI:
    country: str
    city: str
    __URL: str = "http://api.weatherbit.io/v2.0"
    __KEYS: tuple[str] = ("ff58bcc61d0445199d950a6fa6d15489", "41d8d19e64bc4e0c944ae392644056be", "f6d8ac05e90c4b48a7382a39918a8eca")
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
                print(key)
                return key
        return None

    def createPath(self, apiKey:str, pathKey: str):
        if pathKey == "current":
            return f'{self.__URL}{self.__PATHS[pathKey]}?country={self.country}&city={self.city}&key={apiKey}'
        elif pathKey == "usage":
            return f'{self.__URL}{self.__PATHS[pathKey]}?key={apiKey}'

    def keyUssage(self, key):
        return request("GET", self.createPath(key, "usage"), headers={}, data={}).json()

    def current(self, proxies = []):
        tries = 0
        key = self.getValidKey()
        if len(proxies) == 0: proxy = None
        else: proxy = {'http': proxies[tries], 'https': proxies[tries]}
        while key != None:
            response = request("GET", self.createPath(key, "current"), headers={}, data={}, proxies=proxy)
            if response.status_code < 300:
                print(' - Weather data obtained')
                return response.json()
            elif tries >= len(proxies):
                print(f' - Error maximum attempts reached: {response.url}')
                return None
            else:
                tries += 1
                proxy = {'http': proxies[tries], 'https': proxies[tries]}
                print(f' - Error getting weather data ({response.status_code}), retrying with proxy {proxies[tries]}')
        print(' - All Keys limits reached')

def insertInflux(url, payload):
    response = request("POST", url, headers={"Content-Type": "application/json"}, json=payload)
    if response.status_code >= 300: 
        print(f' - Error inserting data at {maya.when("now").iso8601()[0:19].replace("T", " ")}')
        print(payload)
        print(response.content)
    else: 
        print(f' - Inserted data at {maya.when("now").iso8601()[0:19].replace("T", " ")} to asset {payload["tags"]["asset_name"]}')

def generateRandomizedInfluxData(influxApiUrl: str, wheatherAPI: WeatherAPI, assetList: list[str], proxies = []):
    fields = {}
    data = wheatherAPI.current(proxies)
    if (data != None):
        dataWeather = data["data"][0]
    else:
        dataWeather = {"temp": 15, "solar_rad": 0, "wind_spd": 5, "wind_dir": 215, "ghi": 0}
    for assetName in assetList:
        fields['temperature'] = dataWeather["temp"] + round(random.uniform(-1.5, 1.5), 1)
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

def getProxies():
    url = 'https://free-proxy-list.net/'
    response = get(url)
    parser = fromstring(response.text)
    proxies = []
    for i in parser.xpath('//tbody/tr')[:10]:
        if i.xpath('.//td[7][contains(text(),"yes")]'):
            proxy = ":".join([i.xpath('.//td[1]/text()')[0], i.xpath('.//td[2]/text()')[0]])
            proxies.append(proxy)
    return proxies

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

print('WEATHER DATA TO INFLUX')
proxies = []
searchProxies = True
while True:
    try:
        if maya.when("now").minute % 14 == 0 and searchProxies:
            proxies = getProxies()
            random.shuffle(proxies)
            searchProxies = False
        if maya.when("now").minute % 15 == 0:
            print(' - Generating data at ' + maya.when("now").iso8601()[0:19].replace("T", " "))
            generateRandomizedInfluxData("http://influx-api-sv.ren-prototype.svc.cluster.local:8082/api/measurement", api, assets, proxies)
            sleep(300)
            searchProxies = True
    except KeyboardInterrupt:
        print(" - The program has been stopped manually")
        break