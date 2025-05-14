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
- **SPRINT 4 :**
   1. Objectif: Envoyer des données du controller vers view
   2. Etapes:
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
- **SPRINT 6 :** methode ``get`` avec params
   utilisation: /uri?param1=value&&param2=value
   * on separe les parametres par '&' et on met '?' avant d'ajouter les parametres
   * on utilise une library Paranamer
   * tsy afak manao type de parametre sarotr be fa int, double, String, Byte,... (type date tsy mety)
- Sprint 7:
   * Objet atao parametre:
      - Fields: Annoter attr na mitovy am an am formulaire fotsin
      - Method setter tsy maints String parametre, de ny nom method: set+attribut_name(attribut_name-> majuscul ny voloany)
      - Tsy maintsy manan constructeur par defaut

- Sprint 8:
   * Gestion et utilisation du session
   > Framework:
      1
      - Créer une classe MySession ayant comme seul attribut  HttpSession
      - ajouter une fonction get(String key), add(String key, Object obj), delete(String key)
      2
      - A l'appel des methodes des controllers de l'utilisateur, pendant la génération des arguments, verifier 
      si le paramètre est de type MySession et dans ce cas, créer un MySession avec req.getSession()
   > Test:
      - Creer un formulaire de login (identifiant, mot de passe)
      - Quand la personne se connecte, elle accède à une liste de donnée propres à son identifiant
      - Ajouter un bouton déconnexion qui supprime les données de la session
   > Vous pouvez utiliser n'importe quel type pour les listes de données mais sans utiliser de système  de base de donnée

- Sprint 9 :
   OBJECTIF : Exposer les actions du controlleur en REST API
   FRAMEWORK :
      - Creer une classe annotation (EX : RestAPI)
      - Dans la partie Front_controller, on doit verifier l'existance de cette annotation
         > Si l'annotation n'existe pas --> continuez comme avant
         > Si oui
            Recuperer la valeur de retour de la methode
               (transformation en JSON on peux utiliser le library Gson)
               si autre que ModelView, transformer en json directement
               si ModelView, transformer en json la valeur de l'attribut "data"
- SPRINT 10 : 
   OBJECTIF : implementation du methode POST et GET
- SPRINT 11 :
   OBJECTIF : GESTION D'EXCEPTION au ùoùent du check controller

   ON NE DEFINI PAS LE CONSTRUCTEUR DU CONTROLLER (Constructeur par defaut)

- SPRINT 12 : Upload file 
- SPRINT 13 : Form valide
- SPRINT 14 : Champ de formulaire avec session
- SPRINT 15 : Méthode protégés par authentification
- SPRINT 16 : Classe protégés par authentification
- SPRINT 17 : Redirection (SEULEMENT VERS UN METHODE de verbe GET)

- m