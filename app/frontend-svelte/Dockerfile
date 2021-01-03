FROM node:lts-alpine
WORKDIR /frontend
COPY . .
RUN npm install
RUN npm run build

FROM nginx:stable-alpine
COPY --from=0 /frontend/public /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
