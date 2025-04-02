FROM node:lts-alpine

# install simple http server for serving static content
RUN npm install -g http-server vite

# make the 'app' folder the current working directory
WORKDIR /app

# copy both 'package.json' and 'package-lock.json' (if available)
COPY package*.json ./

# set NODE_ENV to development to install devDependencies
ENV NODE_ENV=development

# install project dependencies
RUN npm install

EXPOSE 5173