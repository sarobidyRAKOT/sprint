# EXPLICATION ET DETAILS DU FRAMEWORK SPRINT

## 1) SCHEMA DU PROJET

```tree
    .
    ├── 404.html
    ├── 500.html
    ├── build/
    ├── build-jar
    │   ├── build-jar.exemple.xml
    │   └── build-jar.xml
    ├── build.xml
    ├── dist
    │   └── sprint-17.jar
    ├── docs/
    ├── lib
    │   ├── generic-DAO.jar
    │   ├── gson-2.4.jar
    │   ├── paranamer-2.8.jar
    │   └── servlet-api.jar
    ├── README.md
    ├── sprint.bat
    └── src
        └── mg
            └── ITU
                └── SPRINT
                    ├── annotation/
                    ├── beans/
                    ├── Err/
                    ├── servlets/
                    └── utils/

```

## 2) A FAIRE AVANT BUILD

1. Dossier `build-jar` contenant le fichier build xml.
    - Copier et renommer le fichier `build-jar.exemple.xml` en `build-jar.xml`
    - Des `properties` à modifier dans le fichier `build-jar.xml`.

        ```xml
            <!-- CHANGE THOSE TWO PROPERTIES -->
            <property name="jar-name" value="sprint-name.jar"/>
            <property name="dest-jarFile" value="${basedir}/destignation/lib"/>
        ```

2. Lib pre-requis:
    - [genereic-DAO.jar]('https://github.com/sarobidyRAKOT/DAO.git')
    - gson-2.4.jar
    - paranamer-2.8.jar
    - servlet-api.jar (pour Tomcat 10)

3. Environement de developpement requis
    - Java 17
    - Apache ant

## 3) BUILD JAR

Taper la commande `ant` dans la racine du projet.

## 4) UTILISATION DU FRAMEWORK
