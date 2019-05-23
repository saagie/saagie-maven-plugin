# Saagie Maven Plugin

 
[![Travis CI](https://travis-ci.org/spotify/docker-maven-plugin.svg?branch=master)](https://travis-ci.org/spotify/docker-maven-plugin/) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.saagie/saagie-maven-plugin/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22saagie-maven-plugin%22)



A Maven plugin to push jar files in Saagie Manager and create a new java job.

More information about Saagie : https://www.saagie.com/

* [Usage](#usage)
  * [Creation](#job-creation)
  * [Update](#update)
* [Authentication](#authentication)
* [Configuration](#configuration)
  * [List of all parameters available](#list-of-all-available-parameters)
    * [Mandatory for all modes](#mandatory-for-all-modes)
    * [Mandatory for update mode only](#mandatory-for-update-mode-only)
    * [Mandatory if you use authenticating directly in pom](#mandatory-if-authenticating-directly-from-pom)
    * [Optional parameters](#optional-parameters)
    * [Example](#example)
    

## Usage

### Setup
In order to use this plugin, you need to set it up in your project's Maven configuration file.
First you need to add its configuration in the `<plugins/>` block of your `pom.xml`.

Here is the minimum required:
```
<build>
  <plugins>
    ...
    <plugin>
        <groupId>io.saagie</groupId>
        <artifactId>saagie-maven-plugin</artifactId>
        <version>1.0.4</version>
        <configuration>
            <login>my-username</login>
            <password>my-password</password>
            <realm>my-realm</realm>
            <platformId>1</platformId>
            <jobName>TestMaven</jobName>
            <jobCategory>extract</jobCategory>
        </configuration>
    </plugin>
    ...
  </plugins>
</build>
```
_Note that `login` and `password` can also be removed : see [Authentication](#authentication)._


See [Configuration](#configuration) to fine tune your configuration.


Optionally, you can force a job creation after generating your project jar file, by adding the following `execution` strategy to the plugin configuration:
```
<build>
  <plugins>
    ...
    <plugin>
        <groupId>io.saagie</groupId>
        <artifactId>saagie-maven-plugin</artifactId>
        <version>1.0.4</version>
        <configuration>
            <login>my-username</login>
            <password>my-password</password>
            <realm>my-realm</realm>
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
```

### Job creation
After setting up the plugin, you can create a job by running the following command:
```
mvn clean package saagie:create
```

### Update
You can update an existing job by running the following command:
```
mvn clean package saagie:update
```

After you added (at least) the jobId to the plugin configuration:
```
<build>
  <plugins>
    ...
    <plugin>
        <groupId>io.saagie</groupId>
        <artifactId>saagie-maven-plugin</artifactId>
        <version>1.0.4</version>
        <configuration>
            <login>my-username</login>
            <password>my-password</password>
            <realm>my-realm</realm>
            <platformId>1</platformId>
            <jobId>18</jobId>
            <jobName>TestMaven</jobName>
            <jobCategory>extract</jobCategory>
        </configuration>
    </plugin>
    ...
  </plugins>
</build>
```

_Note:_

- _The jobId is given at creation time_
- _`jobId` parameter is ignored present in "create" mode_

## Authentication

### Using settings.xml 

You can put your credentials in your Maven's global `settings.xml` file as part of the `<servers></servers>` block.
```
<servers>
  <server>
    <id>saagie-manager</id>
    <username>username</username>
    <password>your-password</password>
  </server>
</servers>
```

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

### List of all available parameters

#### Mandatory for all modes
These parameters are mandatory (in create and update mode) :

* **realm**
  - represents the realm (often the company name) of Saagie's platform owner. This realm can be retrieved in the platform URL: _https://**companyName**-manager.prod.saagie.io_ - Here "**companyName**" is the realm.
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

#### Mandatory if authenticating directly from pom  

We recommend to use the authenticating mode using the settings.xml. It's more secure and you'll be sure to never commit your login/password in your pom.xml.

* **login** 
  - represents the login you'll use to have access to your manager (UI and API use the same).

* **password** 
  - represents the password you'll use to have access to your manager (UI and API use the same).
  
  
#### Optional parameters
Setting those parameters will override default values.

* **urlAPI**
  - represents the URL of your manager.
  - default value : "https://realm-manager.prod.saagie.io/api/v1" where "realm" is automatically replaced by the value of the given "realm" parameter. (for Saagie Kumo)
  - You can override this parameter if you use a Saagie Su (Appliance). Don't forget to add "**/api/v1**" at the end of the URL. In this case, the "realm" parameter will have no impact.

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

* **arguments**
  - represents the arguments in the the job command-line.
  - default value : Empty-String

* **jobType**
  - represents the type of job you want to create
  - default value : "java-scala"
  - for the moment, only "java-scala" works, but maybe in the future, more jobType will be available

* **releaseNote**
  - represents the release note of the job version.
  - default value : Empty-String

* **description**
  - represents the job description.
  - default value : Empty-String

#### Example
A fully customized configuration may look like this:
```
<build>
  <plugins>
    ...
    <plugin>
        <groupId>io.saagie</groupId>
            <artifactId>saagie-maven-plugin</artifactId>
            <version>1.0.4</version>
            <configuration>
                <login>my-username</login>
                <password>my-password</password>
                <realm>my-realm</realm>
                <platformId>1</platformId>
                <jobName>My Java job</jobName>
                <jobCategory>processing</jobCategory>
                <urlApi>https://realm-manager.prod.saagie.io/api/v1</urlApi>
                <cpu>0.4</cpu>
                <mem>256</mem>
                <disk>1024</disk>
                <jarName>${project.build.finalName}.jar</jarName>
                <jobId>1234</jobId>
                <releaseNote>Adding new feature</releaseNote>
                <description>My Java job goal</description>
                <arguments>arg1</arguments>
                <languageVersion>8.131</languageVersion>
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
```