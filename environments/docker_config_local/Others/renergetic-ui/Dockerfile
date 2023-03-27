
FROM nginxinc/nginx-unprivileged:1.20-alpine
USER root
COPY dist /usr/share/nginx/html
COPY certs/*.crt /etc/nginx/certs/nginx-certificate.crt
COPY certs/*.key /etc/nginx/certs/nginx.key
COPY nginx.conf /etc/nginx/conf.d/default.conf
USER 101