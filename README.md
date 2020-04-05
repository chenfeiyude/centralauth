# Versions
1. Java 8

2. Mysql 5.7

3. tomcat 8

# Prepare

1. Install lombok for eclipse see https://stackoverflow.com/questions/45461777/lombok-problems-with-eclipse-oxygen/46034044  
1). Download lombok from the site Project Lombok https://projectlombok.org/download;  
2). Close your Eclipse IDE if it is open;  
3). Trigger lombok installation either by following the official installation steps or by executing the command:   

    ```
    java -jar lombok.jar;
    ```
4). Restart IDE and rebuild projects

2. for testing api, using postman or install httpie

```
brew install httpie
```


# Build
build to war and deployed to docker container

## Build project 

```
gradle clean build
```

## Build && Run docker locally

```
gradle clean build && docker-compose build && docker-compose up
```

# Stop and Clean

## Stop docker 

```
docker-compose stop
```

## Remove docker and volumns

```
docker-compose down -v
```

## Remove image

```
docker image ls

REPOSITORY                 TAG                 IMAGE ID            CREATED             SIZE
centralauth_app           latest              868b9adb4f52        20 minutes ago      514MB


docker image rm centralauth_app -f
```

## Remove all stopped containers, dangling images and all unused networks
```
docker system prune
```

## Remove all unused volumns
```
docker system prune --volumes
```

# URL

http://localhost:8080/centralauth/

e.g.
http://localhost:8080/centralauth/api/auth/login


# Sample nginx 
This is working on feiyu4fun.com server
```

server {
    listen 80;
    server_name feiyu4fun.com;

    location /centralauth {
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Server $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_pass http://127.0.0.1:8080/centralauth/;
        # add_header Access-Control-Allow-Origin '*';
        add_header Access-Control-Allow-Origin 'http://www.feiyu4fun.com';
        add_header Access-Control-Allow-Methods 'GET, POST, OPTIONS';
        # add_header Access-Control-Allow-Headers 'DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization';
        add_header Access-Control-Allow-Headers 'DNT,X-Mx-ReqToken,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization,request-from,token,username';
        add_header Access-Control-Allow-Credentials 'true';
        # add_header Host 'feiyu4fun.com';

        if ($request_method = 'OPTIONS') {
           return 204;
        }
    }

    access_log /home/ec2-user/centralauth/logs/access.log;
    error_log /home/ec2-user/centralauth/logs/error.log;
}

```