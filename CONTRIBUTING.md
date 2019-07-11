# How to test this Maven plugin locally

## Build it

Run:
```
mvn clean package install
```
This will create the `saagie-maven-plugin-x.x.x.jar` file and install it to your local Maven cache (`~/.m2`) 

## Create a test project
This project will generate a Jar file we'll try to deploy to Saagie using our Maven plugin.

To create this project, run:
```
mvn archetype:generate -DgroupId=com.mycompany.app -DartifactId=saagie-maven-plugin-usage-example -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
```

### Update the test project's pom.xml

Add your locally built version of `saagie-maven-plugin` to your test project `pom.xml` <plugins> section.

```
<plugins>
    <plugin> 
        <groupId>io.saagie</groupId> 
        <artifactId>saagie-maven-plugin</artifactId> 
        <version>x.x.x</version>
        <configuration> 
            <login>your-username</login> 
            <password>your-password</password> 
            <platformId>your-platform-id</platformId> 
            <realm>your-realm</realm>
            <jobName>TestMavenPlugin</jobName> 
            <jobCategory>extract</jobCategory> 
            <urlApi>https://[YOUR_REALM]-manager.prod.saagie.io/api/v1</urlApi> 
            <cpu>0.2</cpu> 
            <mem>256</mem> 
            <disk>512</disk> 
            <jarName>${project.build.finalName}.jar</jarName> 
        </configuration> 
    </plugin>
</plugins>
```

### build the test project jar
```
mvn clean package
```

### deploy it to Saagie

```
mvn saagie:create
``` 