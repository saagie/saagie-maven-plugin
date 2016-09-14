# saagie-maven-plugin

A Maven plugin to push jar in Saagie Manager (not available yet on maven central - soon)

* [Usage](#usage)
* [Authenticating using settings.xml](#authenticating-using-settings.xml)
* [Using encrypted passwords for authentication](#using-encrypted-passwords-for-authentication)



## Usage

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

#### Authenticating using settings.xml

You can put your credentials in your Maven's global `settings.xml` file as part of the `<servers></servers>` block.

    <servers>
      <server>
        <id>saagie-manager</id>
        <username>username</username>
        <password>your-password</password>
      </server>
    </servers>


#### Using encrypted passwords for authentication

Credentials can be encrypted using [Maven's built in encryption function.](https://maven.apache.org/guides/mini/guide-encryption.html)
Only passwords enclosed in curly braces will be considered as encrypted.

    <servers>
      <server>
        <id>saagie-manager</id>
        <username>username</username>
        <password>{4sNnX2vJXuoH6StIbCWwORqaF4nhRMOPffdmB9YHhuw=}</password>
      </server>
    </servers>
