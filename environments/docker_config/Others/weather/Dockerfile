FROM python:3.9.8-alpine

ARG PY_FILE=*.py

RUN pip3 install maya && \
    pip3 install requests && \
    pip3 install lxml

COPY ${PY_FILE} application.py

ENTRYPOINT ["python", "-u", "application.py"]
