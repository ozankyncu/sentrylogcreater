# Sentry Log Creater

### Info
Based on the documentation Sentry API's do not allow log creation directly. If you want to test the performance of Sentry, you can create sample logs directly with this.

### Usage
```mvn clean package```
* After that in the target folder sentrylogcreater-0.0.1-SNAPSHOT.jar will be generated

### Passing loopCount
Default value = 100
* ```java -DloopCount=100  -jar sentrylogcreater-0.0.1-SNAPSHOT.jar```

### Passing userCount
Default value = 5
* ```java -DuserCount=100  -jar sentrylogcreater-0.0.1-SNAPSHOT.jar```

### Passing DSN Info
You can pass DSN with 2 options. One with application.properties and other with comment line. Example:

* ```java -Dsentry.dsn=https://examplePublicKey@o0.ingest.sentry.io/0 -jar sentrylogcreater-0.0.1-SNAPSHOT.jar```

