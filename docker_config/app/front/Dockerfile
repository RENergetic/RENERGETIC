
FROM nginxinc/nginx-unprivileged:1.20-alpine
USER root
COPY dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
USER 101