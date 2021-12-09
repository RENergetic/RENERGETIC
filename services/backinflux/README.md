# API Documentation
This API allows get data and run queries to renergetic InfluxDB Database
You can access to various routes to get different information

Routes access with *GET* HTTP method
 [http://api_route:8084/api/power]
    Get 'power' field from 'myMeasurement' in renergetic database
    Accept two optional params
     - [from] --> Get a Date (YYYY-MM-DD hh:mm:ss) or a timestamp (The timestamp need get a time unit [ns|u|ms|s|m|h|d|M|Y])
                    Indicate from witch date you can get the power
     - [to] ----> Get a Date (YYYY-MM-DD hh:mm:ss) or a timestamp (The timestamp need get a time unit [ns|u|ms|s|m|h|d|M|Y])
                    Indicate from witch date you can get the power

 [http://api_route:8084/api/power/{function}]
    Get 'power' field from 'myMeasurement' processed with a InfluxQL function by time in renergetic database. Optionaly can grouped 
    Need one obligatory path param
     - [function] -> Indicates the operation to be performed on the power data
     -- Operations: sum | mean | median | max | min | count | distinct
    Accept three optional params
     - [group] -> Get a time as integer (The time need get a time unit [ns|u|ms|s|m|h|d|M|Y])
                    Indicate how group the data
     - [from] --> Get a Date (YYYY-MM-DD hh:mm:ss) or a timestamp (The timestamp need get a time unit [ns|u|ms|s|m|h|d|M|Y])
                    Indicate from witch date you can get the power
     - [to] ----> Get a Date (YYYY-MM-DD hh:mm:ss) or a timestamp (The timestamp need get a time unit [ns|u|ms|s|m|h|d|M|Y])
                    Indicate from witch date you can get the power
                    
 [http://api_route:8084/api/power/query/{query}]
    Execute the indicated query in renergetic database
    Need one obligatory URL param
     - [query] -> The query to execute, it must write in InfluxQL language
                    