# INGESTION API

This API allow to insert information into Renergetic InfluxDB database

The requests to this API should meet many requirements to be allowed

## Requirements

All requirements can be found at path `api/requirements` with a `GET` request

 - The request should have a limited number of entries to the database (This number can be configured in [application.properties](../src/main/resources/application.properties) file)
 - The measurement and field names only can have certain values
 - The tags names only can have certain values and tags data should be according with its name

You can see a list of measurements, fields and tags allowed in our [Confluence page](https://atlassian.gfi.es/confluence/display/RENERGETIC/Time+Series+Database), but to use the API is recommended check the path `api/requirements`

## Data insertion

The data insertion should be done wit a `POST` request at path `api/ingest?bucket=[bucket_name]`

The *bucket* represents the InfluxDB bucket where you want to save the data, ask to Renergetic team to know what bucket have to you

Also you can ommit this variable, and the default bucket will be used

The body of request should look like this:
```
[
    {
        "measurement": "[measurement_name]",
        "fields": {
            "[field_name1]": "[field_value1]",
            "[field_name2]": "[field_value2]"
        },
        "tags": {
            "[tag_name1]": "[tag_value1]",
            "[tag_name2]": "[tag_value2]",
            "[tag_name3]": "[tag_value3]"
        }
    }
]
```

 - *measurement_name*: Is the measurement name, it can take only certain values shown in the requirements path
 - *field_name*: Is the field name, it can take only certain values shown in the requirements path
 - *field_value*: Is the field value, it can take any numeric value
 - *tag_name*: Is the tag name, it can take only certain values shown in the requirements path
 - *tag_value*: Is the tag value, it can match with a regular expression if it is specified in the requirements path

The API return a list of errors if it is necessary to allow check it and retry the data insertion