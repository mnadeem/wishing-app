# Wishing Application
Application to send birthday and anniversary wish emails

## Why

1. Does it job (Sending Birthday and anniversary email wishes) very elegantly
2. Highly Configurable : Load multiple workbooks, specify columns to load, different email details (from, cc) for each workbook, supports multiple LOBs within org
3. Randomly choose images from different buckets (birthday and anniversary, anniversary duration)
4. Support multiple SMTP servers: Gmail, hotmail, yahoo even corporate
5. Run anywhere in local and in cloud, as docker container or as plain app
6. Low footprint does not require database, S3, Nas storage or Web server
7. Good Coding practices have been followed to allow for extension easily
8. Sufficient document to kick started easily
9. Download Configure and Run

# Configuration

# Running

## Eclipse

Right click on ``com.github.nadeem.BootAppApplication`` ==> **Run as** ==> **Java Application**


## Spring Boot Command Line

```Powershell
mvn package spring-boot:run
```

## Docker

Build the image

```Powershell
mvn package docker:build
```
Image built

```Powershell
E:\githubRepos\wishing-app>docker images
REPOSITORY              TAG                  IMAGE ID            CREATED             SIZE
mnadeem/wishing-app     latest               c48f66bdb72c        31 seconds ago      148MB
openjdk                 8-jre-alpine         f7a292bbb70c        13 months ago       89.7MB
openjdk                 8-jdk-alpine         a3562aa0b991        13 months ago       110MB

E:\githubRepos\wishing-app>
```

No containers running yet.


```Powershell
E:\githubRepos\wishing-app>docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES


```
Start the container

```Powershell
E:\githubRepos\wishing-app>mvn docker:start
[INFO] Scanning for projects...
[INFO]
[INFO] -------------------< com.github.mnadeem:wishing-app >-------------------
[INFO] Building wishing-app 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- docker-maven-plugin:0.33.0:start (default-cli) @ wishing-app ---
[INFO] DOCKER> [docker.io/mnadeem/wishing-app:latest]: Start container 98651db65ded
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.554 s
[INFO] Finished at: 2020-06-05T08:43:09+05:30
[INFO] ------------------------------------------------------------------------

E:\githubRepos\wishing-app>

```

Container started


```Powershell
E:\githubRepos\wishing-app>docker ps
CONTAINER ID        IMAGE                 COMMAND                  CREATED             STATUS              PORTS               NAMES
98651db65ded        mnadeem/wishing-app   "java -jar wishing-a…"   29 seconds ago      Up 28 seconds                           wishing-app-1

E:\githubRepos\wishing-app>

```

View logs 

```Powershell
E:\githubRepos\wishing-app>docker logs 98651db65ded

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.3.0.RELEASE)

2020-06-05 03:13:11.239  INFO 1 --- [           main] c.g.mnadeem.wishing.WishingApplication   : Starting WishingApplication v0.0.1-SNAPSHOT with PID 1 (/app/wishing-app.jar started by root in /app)
2020-06-05 03:13:11.242  INFO 1 --- [           main] c.g.mnadeem.wishing.WishingApplication   : No active profile set, falling back to default profiles: default
2020-06-05 03:13:12.368  INFO 1 --- [           main] o.s.s.c.ThreadPoolTaskScheduler          : Initializing ExecutorService 'taskScheduler'
2020-06-05 03:13:12.383  INFO 1 --- [           main] c.g.mnadeem.wishing.WishingApplication   : Started WishingApplication in 2.021 seconds (JVM running for 2.564)
2020-06-05 03:13:17.888 ERROR 1 --- [   scheduling-1] o.s.s.s.TaskUtils$LoggingErrorHandler    : Unexpected error occurred in scheduled task


```

Stop the container

```Powershell
E:\githubRepos\wishing-app>mvn docker:stop
[INFO] Scanning for projects...
[INFO]
[INFO] -------------------< com.github.mnadeem:wishing-app >-------------------
[INFO] Building wishing-app 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- docker-maven-plugin:0.33.0:stop (default-cli) @ wishing-app ---
[INFO] DOCKER> [docker.io/mnadeem/wishing-app:latest]: Stop and removed container 98651db65ded after 0 ms
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  05:01 min
[INFO] Finished at: 2020-06-05T08:51:44+05:30
[INFO] ------------------------------------------------------------------------

```

Refer [fabric8 home](http://dmp.fabric8.io/) for details on commands

```Powershell


```
