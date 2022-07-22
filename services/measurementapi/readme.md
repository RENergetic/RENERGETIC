
- [MEASUREMENT API](#measurement-api)
  - [GET Requests](#get-requests)
  - [POST Requests](#post-requests)

# MEASUREMENT API 
Require PSNC VPN connection: [Swagger Docs](http://influx-api-swagger-ren-prototype.apps.paas-dev.psnc.pl/api/docs-ui)
 ## GET Requests
 The GET requests allow to get measurement registries
 - /api/measurement/{measurement_name}
   - PATH VARIABLES
     - measurement_name: measurement name to get
   - QUERY VARIABLES
     - bucket (optional): Bucket from which to obtain data, "renergetic" by default
     - from (optional): Since when to obtain the data
     - to (optional): Until when to obtain the data
     - NOT IMPLEMENTED | time_var (optional): field to use as time when set the from and to values, "time" by default
     - tag_key (optional): Filter the result by tag_name and his value. All variables that haven't the names "bucket", "from" or "to" are used to filter
   - The data returned have the format:
   [
    {
        "measurement": "measurement_name",
        "fields": {
            "field_key1": "field_value1",
            "field_key2": "field_value2",
            ...
        } 
    }
   ]
 - /api/measurement/{measurement_name}/{operation}
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
   - The data returned have the format:
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
 ## POST Requests
 The POST requests allow to insert new registries
 - /api/measurement/
   - Only needs a body
   - The body have a optional field:
     - "bucket": Influx bucket to save the registry, "renergetic" by default
   - The body have some required fields:
     - "measurement": Contains the measurement name
     - "fields": Contains the measurement fields, all values should be in quotation marks, if the field start with the word "time" the field save a date and must have the format "yyyy-MM-dd HH:mm:ss"
     - "tags": Contains the measurement tags, all are saved as String and can be used as filter when you make a GET request
   - Data sent example:
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