
- [MEASUREMENT API](#measurement-api)
  - [GET Requests](#get-requests)
  - [POST Requests](#post-requests)

# MEASUREMENT API 
Require PSNC VPN connection: [Swagger Docs](http://influx-api-swagger-ren-prototype.apps.paas-dev.psnc.pl/api/docs-ui)
 ## GET Requests
 The GET requests allow to get measurement registries
 - /api/measurement
   - `Get a list of all measurements names`
   - QUERY VARIABLES
     - bucket (optional): Bucket from which to obtain data, "renergetic" by default
   - The data returned have the format:
  ```
   [
    "measurement-1",
    "measurement-2",
    ...
   ]
  ```
 - /api/measurement/tag
   - `Get all tags and them values`
   - QUERY VARIABLES
     - bucket (optional): Bucket from which to obtain data, "renergetic" by default
     - measurements (optional): A list with measurement names separated with commas to filter tags. If it's empty search tag from all measurements
       - Example: *measurements=heat_meter, energy_meter*
     - fields (optional): A list with fields names separated with commas to filter tags. If it's empty search entries with any field
       - Example: *fields=power, temperature*
     - tag_key (optional): Filter entries by tag and its values (to filter by many values separate it by commas). All variables that haven't the names "measurements", "fields", "bucket", "from" or "to" are used to filter
       - Example: *tag_key1=value1, value2 & tag_key2=value1*
     - hideNotFound: Boolean, if it's true this endpoint doesn't return codes 404, in its place returns code 200 with an empty map
   - The data returned have the format:
  ```
  {
      "tag_key1": [
        "tag_value1",
        "tag_value2",
        ...
      ],
      "tag_key2": [
        "tag_value1",
        "tag_value2",
        ...
      ],
      ...
  }
  ```
 - /api/measurement/tag/{tag_key}
   - `Get a single tag values`
   - QUERY VARIABLES
     - bucket (optional): Bucket from which to obtain data, "renergetic" by default
     - measurements (optional): A list with measurement names separated with commas to filter tags. If it's empty search tag from all measurements
       - Example: *measurements=heat_meter, energy_meter*
     - fields (optional): A list with fields names separated with commas to filter tags. If it's empty search entries with any field
       - Example: *fields=power, temperature*
     - tag_key (optional): Filter entries by tag and its values (to filter by many values separate it by commas). All variables that haven't the names "measurements", "fields", "bucket", "from" or "to" are used to filter
       - Example: *tag_key1=value1, value2 & tag_key2=value1*
     - hideNotFound: Boolean, if it's true this endpoint doesn't return codes 404, in its place returns code 200 with an empty list
   - The data returned have the format:
  ```
  {
      "tag_key": [
        "tag_value1",
        "tag_value2",
        ...
      ]
  }
  ```
 - /api/measurement/data
   - `Get all Influx entries, allow filter by many measurements names, fiels and tags`
   - QUERY VARIABLES
     - bucket (optional): Bucket from which to obtain data, "renergetic" by default
     - from (optional): Since when to obtain the data
     - to (optional): Until when to obtain the data
     - measurements (optional): Filter by a list with measurement names separated with commas. If it's empty search all measurements
       - Example: *measurements=heat_meter, energy_meter*
     - fields (optional): Filter by a list with fields names separated with commas. If it's empty search all fields
       - Example: *fields=power, temperature*
     - tag_key (optional): Filter entries by tag and its values (to filter by many values separate it by commas). All variables that haven't the names "measurements", "fields", "bucket", "from" or "to" are used to filter
       - Example: *tag_key1=value1, value2 & tag_key2=value1*
     - hideNotFound: Boolean, if it's true this endpoint doesn't return codes 404, in its place returns code 200 with an empty list
   - The data returned have the format:
  ```
   [
    {
        "measurement": "measurement_name",
        "fields": {
            "field_key1": "field_value1",
            "field_key2": "field_value2",
            ...
        },
        "tags": {
            "tag_key1": "tag_value1",
            "tag_key1": "tag_value1",
            ...
        }
    }
   ]
  ```
 - /api/measurement/data/{operation}
   - `Get all Influx entries and performs an operation on them, also it returns the operation results group by measurement name `
   - PATH VARIABLES
     - operation: performs an operation on the data. The allowed operations are "sum", "max", "min", "mean", "median", "distinct" and "count"
   - QUERY VARIABLES
     - bucket (optional): Bucket from which to obtain data, "renergetic" by default
     - from (optional): Since when to obtain the data
     - to (optional): Until when to obtain the data
     - group (optional): Groups data into a temporal window
       - Example: *group=1h*
     - by_measurement (optional): "true" to get the processed data grouped by measurement "false" to group all data together (false by default)
     - measurements (optional): A list with measurement names separated with commas. If it's empty search all measurements
       - Example: *measurements=heat_meter, energy_meter*
     - fields (optional): A list with fields names separated with commas. If it's empty search all fields
       - Example: *fields=power, temperature*
     - tag_key (optional): Filter entries by tag and its values (to filter by many values separate it by commas). All variables that haven't the names "measurements", "fields", "bucket", "group", "by_measurement", "from" or "to" are used to filter
       - Example: *tag_key1=value1, value2 & tag_key2=value1*
     - hideNotFound: Boolean, if it's true this endpoint doesn't return codes 404, in its place returns code 200 with an empty list
   - The data returned with *by_measurement* to false have the format:
  ```
   [
    {
        "measurement": null,
        "fields": {
            "operation": "operation_value1",
            "time": "yyyy-MM-dd HH:mm:ss",
            ...
        } 
    }
   ]
   - The data returned with *by_measurement* to false have the format:
    ...
   [
    {
        "measurement": "measurement_name1",
        "fields": {
            "operation": "operation_value1",
            "time": "yyyy-MM-dd HH:mm:ss",
            ...
        } 
    },
    {
        "measurement": "measurement_name2",
        "fields": {
            "operation": "operation_value2",
            "time": "yyyy-MM-dd HH:mm:ss",
            ...
        } 
    },
    ...
   ]
  ```
 - **DEPRECATED** /api/measurement/{measurement_name}
   - `Get Influx entries related with a measurement name and allow filter them`
   - PATH VARIABLES
     - measurement_name: measurement name to get
   - QUERY VARIABLES
     - bucket (optional): Bucket from which to obtain data, "renergetic" by default
     - from (optional): Since when to obtain the data
     - to (optional): Until when to obtain the data
     - NOT IMPLEMENTED | time_var (optional): field to use as time when set the from and to values, "time" by default
     - tag_key (optional): Filter the result by tag_name and his value. All variables that haven't the names "bucket", "from" or "to" are used to filter
     - hideNotFound: Boolean, if it's true this endpoint doesn't return codes 404, in its place returns code 200 with an empty list
   - The data returned have the format:
  ```
   [
    {
        "measurement": "measurement_name",
        "fields": {
            "field_key1": "field_value1",
            "field_key2": "field_value2",
            ...
        },
        "tags": {
            "tag_key1": "tag_value1",
            "tag_key1": "tag_value1",
            ...
        }
    }
   ]
  ```
 - **DEPRECATED** /api/measurement/{measurement_name}/{operation}
   - `Get Influx entries related with a measurement name and performs an operation on them, is necesary send the name of the field to perform the operation`
   - PATH VARIABLES
     - measurement_name: measurement name to get
     - operation: performs an operation on the data. The allowed operations are "sum", "max", "min", "mean", "median", "distinct" and "count"
   - QUERY VARIABLES
     - field (required): field on which to perform the operation
     - bucket (optional): Bucket from which to obtain data, "renergetic" by default
     - from (optional): Since when to obtain the data
     - to (optional): Until when to obtain the data
     - NOT IMPLEMENTED | time_var (optional): field to use as time when set the from and to values, "time" by default
     - tag_key (optional): Filter the result by tag_name and his value. All variables that haven't the names "bucket", "from" or "to" are used to filter
     - hideNotFound: Boolean, if it's true this endpoint doesn't return codes 404, in its place returns code 200 with an empty list
   - The data returned have the format:
  ```
   [
    {
        "measurement": "measurement_name",
        "fields": {
            "operation": "operation_value1",
            "time": "yyyy-MM-dd HH:mm:ss",
            ...
        }
    }
   ]
  ```
 ## POST Requests
 The POST requests allow to insert new registries
   - `Create a new measurement. It doesn't validate the data`
 - /api/measurement/
   - Only needs a body
   - The body have a optional field:
     - "bucket": Influx bucket to save the registry, "renergetic" by default
   - The body have some required fields:
     - "measurement": Contains the measurement name
     - "fields": Contains the measurement fields, all values should be in quotation marks, if the field start with the word "time" the field save a date and must have the format "yyyy-MM-dd HH:mm:ss"
     - "tags": Contains the measurement tags, all are saved as String and can be used as filter when you make a GET request
   - Data sent example:
  ```
   {
        "bucket": "renergetic",
        "measurement": "renewability",
        "fields": {
            "value": "50",
            "time_predicition": "2022-04-26 13:00:00"
        },
        "tags": {
            "typeRen": "fixed",
            "asset_name": "test"
        }
    }
  ```