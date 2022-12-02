FROM python:3.9.8-alpine

ARG PY_FILE=*.py
COPY ${PY_FILE} application.py

RUN pip3 install maya && \
    pip3 install requests

ENTRYPOINT ["python", "application.py"]
