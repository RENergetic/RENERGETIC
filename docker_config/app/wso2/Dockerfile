FROM wso2/wso2am:4.0.0-alpine

RUN chown 802:wso2 /home/wso2carbon/wso2am-4.0.0 && \
    chown -R 802:wso2 /home/wso2carbon/wso2am-4.0.0 && \
    chmod -R a+rwx /home/wso2carbon/wso2am-4.0.0

COPY libs/*.jar /home/wso2carbon/wso2am-4.0.0/lib/
COPY deployment.toml /home/wso2carbon/wso2am-4.0.0/repository/conf/deployment.toml
