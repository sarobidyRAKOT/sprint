# SPRINT
### PRE-REQUIS (LIB JAVA .jar)
1. Generic-DAO `https://github.com/sarobidyRAKOT/DAO.git` il suffit de taper `ant` et récuperer le jar dans le `/dist/Generic-DAO.jar`
2. gson `gson-2.4.jar`
3. paranamer `paranamer-3.8.jar`
4. Tomcat 10 
5. servlet-api (jar servlet) `servlet-api.jar` récuperer dans le tomcat 10

### BUILD 
- Modilier le fichier `/build-jar/build-jar.xml`, changer le destination du fichier `.jar` et son nom si vous voulez
- Taper `ant` dans le repertoire racine

### DETAILS ...
- **SPRINT 0 :** test gitHub et afficher url dans index (/)
- **SPRINT 1 :** afficher les listes de controllers annoter an Annotation_controller
- **SPRINT 2 :** Annotation des controllers -> classe: Annotation controller
- **SPRINT 3 :** liste controller -> url + nom de la classe controller + methode appelée 
- **SPRINT 4 :** Objectif: Envoyer des données du controller vers view
   - __Côté Framework :__
      1. créer une classe ``ModelView`` qui aura pour attributs :
         - ``String url`` : url de destination des données (page `JSP`)
         - `HashMap<String, Object> data`: `String` le clé et `Object` la valeur, (donnée à envoyer vers view ou les page `JSP`)
      2. créer une fonction `public voic AddObject (String, Object)` dans la classe ``ModelView`` qui ajoute des données dans ``HashMap <String, Object> data``
      3. Dans la classe ``mg.ITU.SPRINT.servlets.FrontController`` -> methode ``ProcessRequest``, récupérer les données issues de la méthode annotée ``Get``
         - si les data sont de type ``String`` -> retourner la valeur directement, 
         - si les données sont de type ``ModelView`` -> récupérer le url et dispatcher les données vers cet url (page), boucler le `HashMap <String, Object> data` et fait ``request.setAttribute (String, Object)``
         - si non --> retourner "non reconnu"
   - __Côté Test :__ Les méthodes des controlleurs qui seront annotées ont pour type de retour "String" ou "ModelView"
- **SPRINT 5 :**
   1. Raha ohatra ka misy url mitovy dia manao Exception, 
   2. Raha ohatra ka tsy misy an'ilay package ``controller`` dia manao Exception,
   3. Na koa hoe vide ny ao anatin'ilay package de manao Exception, 
   4. Ny type de retour an'ilay fonction raha ohatra ka tsy mitovy amin'ny ``String`` na ``ModelView`` dia manao Exception
- **SPRINT 6 :** methode ``GET`` avec params `utilisation: /url?param1=value&&param2=value`
   1. on separe les parametres par '&' et on met '?' avant d'ajouter les parametres
   2. on utilise une library Paranamer
   3. tsy afaka manao type de parametre sarotra be fa int, double, String, Byte,... (type date tsy mety)
- **SPRINT 7 :** *Paramètre de type `OBJET`, fonction du controlleur*
   1. classe annoter `Form_object`
   2. Fields: Annoter ``Attr`` ou `Attr_tab` na mitovy am an am formulaire fotsin
   3. Méthode setter tsy maintsy ``String`` type parametre, de ny nom method: set+attribut_name(attribut_name-> majuscul ny voloany)
   4. Tsy maintsy manana constructeur par defaut 

- **SPRINT 8 :** *Gestion et utilisation du session `MySession`*
   1. Framework:
      1. Créer une classe ``MySession`` ayant comme seul attribut  HttpSession
      2. ajouter une fonction ``get(String key)``, `add(String key, Object obj)` ``et delete(String key)``
      3. A l'appel des methodes des controllers de l'utilisateur, pendant la génération des arguments, verifier 
      si le paramètre est de type ``MySession`` et dans ce cas, créer un ``MySession`` avec request.getSession()
   2. Test:
      1. Creer un formulaire de login (identifiant, mot de passe)
      2. Quand la personne se connecte, elle accède à une liste de donnée propres à son identifiant
      3. Ajouter un bouton déconnexion qui supprime les données de la session

- **SPRINT 9 :** *Exposer les actions du controlleur en ``REST API``*
   1. Creer une classe annotation `RestAPI`
   2. Dans la partie ``Front_controller``, on doit verifier l'existance de cette annotation
      1. Si l'annotation n'existe pas -> continuez comme avant (sans rien toucher)
      2. Si oui: Recuperer la valeur de retour de la methode
         1. (transformation en JSON on peux utiliser le library Gson)
         2. si autre que ``ModelView``, transformer en json directement
         3. si ``ModelView``, transformer en json la valeur de l'attribut ``data``
- **SPRINT 10 :** *implementation du methode ``POST`` et ``GET``*
- **SPRINT 11 :** *GESTION D'EXCEPTION au moment du check controller*
- **SPRINT 12 :** *Upload file *
- **SPRINT 13 :** *Form valide*
- **SPRINT 14 :** *Champ de formulaire avec session*
- **SPRINT 15 :** *Méthode protégés par authentification*
- **SPRINT 16 :** *Classe protégés par authentification*
- **SPRINT 17 :** *Redirection (SEULEMENT VERS UN METHODE de verbe ou methode ``GET``)*

## HOW TO BUILD JAR

1. Change jar name and destignation dir

   ```xml
      <!-- CHANGE THOSE TWO PROPERTIES -->
      <property name="jar-name" value="sprint-16.jar"/>
      <property name="dest-jarFile" value="${basedir}/../../POC/Ticketing/lib"/>

   ```

2. Build jar file - check in the dest dir

   ```bash
      ant
   ```
