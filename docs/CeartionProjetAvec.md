# CREATION PROJET AVEC LE FRAMEWORK AVEC MVN

## 1) STRUCTURE BASIQUE DU PROJET

```tree
    .
    ├── lib/
    ├── pom.xml
    ├── README.md
    ├── src
    │   └── main
    │       ├── java
    │       │   └── controllers/
    │       └── webapp
    │           ├── login.jsp
    │           └── WEB-INF
    │               └── web.xml
    └── target
```

## 2) PRE REQUIS

1. Java 17 ou plus
2. MVN
3. Tomcat 10
4. libraries:
    - [Generique DAO]("https://github.com")
    - Paranamer-2.8.jar
    - gson-2.4.jar

## 3) A FAIRE

1. Deux maniéres pour utiliser les librairies
    - Installer
    - utilisation de basedir dans le pom.xml

2. Creation du fichier `webapp/WEB-INF/web.xml`.

    ```xml
        <web-app xmlns="https://jakarta.ee/xml/ns/jakartaee" version="6.0">

            <servlet>
                <servlet-name>FrontController</servlet-name>
                <servlet-class>mg.ITU.SPRINT.servlets.Front_controller</servlet-class>

                <init-param>
                    <param-name>package_controllers</param-name>
                    <!-- Modifiable par rapport au position du dossier controllers -->
                    <param-value>controllers</param-value>
                </init-param>
                <load-on-startup>1</load-on-startup>

            </servlet>

            <servlet-mapping>
                <servlet-name>FrontController</servlet-name>
                <url-pattern>/</url-pattern>
            </servlet-mapping>

        </web-app>

    ```

3. Ficher `pom.xml` à la racine.

    ```xml
        <project xmlns="http://maven.apache.org/POM/4.0.0"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                http://maven.apache.org/xsd/maven-4.0.0.xsd">

            <modelVersion>4.0.0</modelVersion>
            <!-- SECTION MODIFIABLE -->
            <groupId>com.user.app</groupId>
            <artifactId>mon-projet-web</artifactId>
            <version>1.0.0</version>
            <packaging>war</packaging>
            <name>Mon Projet Web</name>
            <!-- SECTION MODIFIABLE -->


            <properties>
                <maven.compiler.source>17</maven.compiler.source>
                <maven.compiler.target>17</maven.compiler.target>
                <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            </properties>

            <dependencies>

                <!-- Servlet API (fourni par Tomcat) -->
                <dependency>
                    <groupId>jakarta.servlet</groupId>
                    <artifactId>jakarta.servlet-api</artifactId>
                    <version>6.0.0</version>
                    <scope>provided</scope>
                </dependency>

                <!-- Ton framework personnalisé -->
                <dependency>
                    <groupId>itu.framework</groupId>
                    <artifactId>sprint-lib</artifactId>
                    <version>17.0.0</version>
                </dependency>

                <!-- DAO perso -->
                <dependency>
                    <groupId>itu.generic.DAO</groupId>
                    <artifactId>generic-DAO-lib</artifactId>
                    <version>1.0.0</version>
                </dependency>


                <dependency>
                    <groupId>com.thoughtworks.paranamer</groupId>
                    <artifactId>paranamer</artifactId>
                    <version>2.8</version>
                </dependency>

                <!-- Gson 2.4 -->
                <dependency>
                    <groupId>com.google.code.gson</groupId>
                    <artifactId>gson</artifactId>
                    <version>2.4</version>
                </dependency>

            </dependencies>

            <build>
                <finalName>mon-projet-web</finalName>

                <plugins>

                    <!-- Compiler Plugin -->
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-compiler-plugin</artifactId>
                        <version>3.11.0</version>
                        <configuration>
                            <source>17</source>
                            <target>17</target>
                        </configuration>
                    </plugin>

                    <!-- Plugin pour générer le WAR -->
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-war-plugin</artifactId>
                        <version>3.4.0</version>
                        <configuration>
                            <failOnMissingWebXml>false</failOnMissingWebXml>
                        </configuration>
                    </plugin>

                </plugins>
            </build>

        </project>
    ```


## CREATION LIB/FRAMEWORK

1. Renommer le fichier `./build-jar/build-jar.exemple.xml` en `./build-jar/build-jar.xml`
2. Modifier les lignes suivants

    ```xml
        <property name="jar-name" value="sprint-name.jar"/>
        <property name="dest-jarFile" value="${basedir}/destignation/lib"/>
    ```

3. Excecute

    ```bash
        ant
    ```
