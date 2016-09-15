# Saagie Maven Plugin

 
[![Travis CI](https://travis-ci.org/spotify/docker-maven-plugin.svg?branch=master)](https://travis-ci.org/spotify/docker-maven-plugin/) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.saagie/saagie-maven-plugin/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22saagie-maven-plugin%22)



A Maven plugin to push jar in Saagie Manager and create a new java job (not available yet on maven central - soon)

More informations about Saagie : https://www.saagie.com/

* [Usage](#usage)
  * [Creation](#creation)
  * [Update](#update)
* [Authenticating](#authenticating)
* [Configuration](#configuration)
  * [List of all parameters available](#list-of-all-parameters-available)
    * [Mandatory for all modes](#mandatory-for-all-modes)
    * [Mandatory for update mode only](#mandatory-for-update-mode-only)
    * [Mandatory if you use authenticating directly in pom](#mandatory-if-you-use-authenticating-directly-in-pom)
  * [Others parameters can be overriden](#others-parameters-can-be-overriden)
    

## Usage

### Creation
You can create a job with the above configurations by running this command.

    mvn clean package saagie:create

In order for this to succeed, at least some parameters can be set.

    <build>
      <plugins>
        ...
        <plugin>
            <groupId>io.saagie</groupId>
            <artifactId>saagie-maven-plugin</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <configuration>
                <login>username</login>
                <password>your-password</password>
                <platformId>1</platformId>
                <jobName>TestMaven</jobName>
                <jobCategory>extract</jobCategory>
            </configuration>
        </plugin>
        ...
      </plugins>
    </build>

Optionally, you can force the job create after the generation of the jar :

    <build>
      <plugins>
        ...
        <plugin>
            <groupId>io.saagie</groupId>
            <artifactId>saagie-maven-plugin</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <configuration>
                <login>username</login>
                <password>your-password</password>
                <platformId>1</platformId>
                <jobName>TestMaven</jobName>
                <jobCategory>extract</jobCategory>
            </configuration>
            <executions>
                <execution>
                    <id>create</id>
                    <phase>package</phase>
                    <goals>
                        <goal>create</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        ...
      </plugins>
    </build>

### Update
You can create a job with the above configurations by running this command.

    mvn clean package saagie:update

In order for this to succeed, at least some parameters can be set.

    <build>
      <plugins>
        ...
        <plugin>
            <groupId>io.saagie</groupId>
            <artifactId>saagie-maven-plugin</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <configuration>
                <login>username</login>
                <password>your-password</password>
                <platformId>1</platformId>
                <jobId>18</jobId>
                <jobName>TestMaven</jobName>
                <jobCategory>extract</jobCategory>
            </configuration>
        </plugin>
        ...
      </plugins>
    </build>


The difference between create and update is the add of the "jobId" parameter in the configuration to know which job should be updated (if present in the "create" mode, it'll be just ignored)

## Authenticating 

### Using settings.xml 

You can put your credentials in your Maven's global `settings.xml` file as part of the `<servers></servers>` block.

    <servers>
      <server>
        <id>saagie-manager</id>
        <username>username</username>
        <password>your-password</password>
      </server>
    </servers>


### Using settings.xml and encrypted passwords

Credentials can be encrypted using [Maven's built in encryption function.](https://maven.apache.org/guides/mini/guide-encryption.html)
Only passwords enclosed in curly braces will be considered as encrypted.

    <servers>
      <server>
        <id>saagie-manager</id>
        <username>username</username>
        <password>{4sNnX2vJXuoH6StIbCWwORqaF4nhRMOPffdmB9YHhuw=}</password>
      </server>
    </servers>

## Configuration

### List of all parameters available

#### Mandatory for all modes
These parameters are mandatory (in create and update mode) :

* **platformId**
  - represents the id of the platform you want to add the job. This id is accessible via the URL when you are authenticated and in your manager : _https://.../#/manager/**1**_ - Here "**1**" is the plaformId.
* **jobName**
  - represents the name of the job you want to create or to update (should be exactly the same for update - a verification is made).
* **jobCategory**
  - represents the category of the job you want to create or to update (should be exactly the same for update - a verification is made).
  - can be : "**extract**" or "**processing**" (another values can produce errors).

#### Mandatory for update mode only 

* **jobId**
  - represents the id the job you want to update. This id is accessible via the URL when you are on the details page of the job : _https://.../#/manager/1/job/**49**_ Here "**49**" is the jobId. 

#### Mandatory if you use authenticating directly in pom  

We recommand to use the authenticating mode using the settings.xml. It's more secure and you'll be sure to never commit your login/password in your pom.xml.

* **login** 
  - represents the login you'll use to have access to your manager (UI and API use the same).

* **password** 
  - represents the password you'll use to have access to your manager (UI and API use the same).
  
  
### Others parameters can be overriden

* **urlAPI**
  - represents the URL of your manager.
  - default value : "https://manager.prod.saagie.io/api/v1" (for Saagie Kumo)
  - You can override this parameter if you use a Saagie Su (Appliance). Don't forget to add "**/api/v1**" at the end of the URL.

* **jarName**
  - represents the name of the jar you want to upload
  - default value : "${project.build.finalName}-jar-with-dependencies.jar"
  - By default, we use the _maven-assembly-plugin_ to generate jar with dependencies (some examples are available in our [Github](https://github.com/saagie)) and the jar generated use this template. If you use the _maven-shade-plugin_ or other maven plugin, you can override this parameter to target the correct jar (```<jarName>${project.build.finalName}.jar</jarName>``` for the _maven-shade-plugin_ ).

* **cpu / mem / disk**
  - represents the amount of CPU / memory / disk space you want to reserve for your job (like you can set in the manager interface).
  - default values : **cpu**:0.5 / **mem**:512 / **disk**:1024
  - For **cpu**, the value represents the number of core (0.5 represent an half of core).
  - For **mem** and **disk**, the value represents the number of mega-octet allocated.
  
* **languageVersion**
  - represents the version of language you want to run your job.
  - default value : 8
  - Only 8 or 7 are available (See [anapsix/alpine-java](https://hub.docker.com/r/anapsix/alpine-java/) to see the precise version of java we use). 

* **jobType**
  - represents the type of job you want to create
  - default value : "java-scala"
  - for the moment, only "java-scala" works, but maybe in the future, more jobType will be availabe
 