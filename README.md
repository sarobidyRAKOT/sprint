# SPRINT

inclure tous les library :
   - servlet-api.jar
--> CLIQUER sprint.bat pour lancer

- sprint 0:
   test gitHub
   affiche url dans index (/)
- sprint 1:
   afficher les listes de controllers annoter an Annotation_controller
- sprint 2:
   Annotation des controllers -> classe: Annotation controller
- sprint 3:
   liste controller -> url + nom de la classe controller + methode appelée 
- sprint 4:
   . Objectif: Envoyer des données du controller vers view
   . Etapes:
      Côté Framework:
         créer une classe ModelView qui aura pour attributs:
            . String url[url de destination après l'exécution de méthode], 
            . HashMap<String : nom de la variable, Object: sa valeur> data [donnée à envoyer vers cette view],
         créer une fonction "AddObject" qui a comme type de retour void pour pouvoir mettre les données dans HashMap "data"
         Dans FrontController,dans ProcessRequest, récupérer les données issues de la méthode annotée Get, si les data sont de type string --> retourner la valeur directement, si les données sont de type ModelView --> récupérer le url et dispatcher les données vers cet url: boucle de data: y faire request.setAttribute, si non --> retourner "non reconnu"
      Côté Test: 
         Les méthodes des controlleurs qui seront annotées ont pour type de retour "String" ou "ModelView"
- sprint 5:
   Rh misy url mitov d manao Exception, rh ohtr k tss anilay package controller d misy Exception,na ko oe vide n ao anatinilay package de misy Exception, d n type de retour an fonction rh ohtr k ts mitov amin String na ModelView d misy Exception ko
- sprint 6:
   methode get avec params
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