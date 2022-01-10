docker pull bitnami/kafka:latest
docker network create app-tier --driver bridge
docker run -d --name zookeeper-server --network app-tier -e ALLOW_ANONYMOUS_LOGIN=yes bitnami/zookeeper:latest
docker run -d --name kafka-server --network app-tier -e ALLOW_PLAINTEXT_LISTENER=yes -e KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper-server:2181 bitnami/kafka:latest
docker run -it --rm --network app-tier -e KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper-server:2181 bitnami/kafka:latest kafka-topics.sh --list  --bootstrap-server kafka-server:9092
docker run --name nifi --network app-tier -p 8443:8443 -d apache/nifi:latest
PAUSE

REM kafka-topics.sh --create --bootstrap-server kafka-server:9092 --replication-factor 1 --partitions 1 --topic renergetic
REM kafka-console-producer.sh --topic=renergetic --bootstrap-server kafka-server:9092