FROM node:13.8.0-alpine
WORKDIR /opt/app
COPY ./gate-simulator .
RUN npm install
CMD ["npm", "start"]
EXPOSE 9999