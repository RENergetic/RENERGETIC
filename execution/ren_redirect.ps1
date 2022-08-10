Start-Process -NoNewWindow  powershell { kubectl port-forward service/postgresql-db-sv 5432:5432 --namespace app }
Start-Process -NoNewWindow  powershell { kubectl port-forward service/frontvue-sv 80:8080 --namespace app }
Start-Process -NoNewWindow  powershell { kubectl port-forward service/grafana-sv 3000:3000 --namespace data }
Start-Process -NoNewWindow  powershell { kubectl port-forward service/influx-db-sv 8086:8086 --namespace data }
Start-Process -NoNewWindow  powershell { kubectl port-forward service/backinflux-sv 8084:8084 --namespace data }
PAUSE