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
   