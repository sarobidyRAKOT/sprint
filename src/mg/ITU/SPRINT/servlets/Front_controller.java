package mg.ITU.SPRINT.servlets;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.*;

import mg.ITU.DAO.reflexion.Reflexion;
// import jakarta.servlet.*;
// import jakarta.servlet.http.*;
// import mg.ITU.DAO.reflexion.Reflexion;
import mg.ITU.SPRINT.Err.Error404;
import mg.ITU.SPRINT.Err.Error500;
import mg.ITU.SPRINT.Err.Errors;
import mg.ITU.SPRINT.annotation.*;
import mg.ITU.SPRINT.beans.*;
    

@MultipartConfig (
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB : Taille maximale du fichier en mémoire avant qu'il ne soit écrit sur le disque
    maxFileSize = 1024 * 1024 * 20,      // 10MB : Taille maximale autorisée pour un fichier téléchargé
    maxRequestSize = 1024 * 1024 * 50    // 50MB : Taille totale autorisée pour tous les fichiers dans la requête
)
public class Front_controller extends HttpServlet {

    protected String package_name;
    protected HashMap <String, Mapping> url_Mapping;
    // protected Reflexion reflexion;
    private static Exception error500 = null;
    private Verb verb;


    @Override
    public void init(ServletConfig config) throws ServletException {
        
        
        super.init(config);
        this.package_name = config.getInitParameter("package_controllers");
        
        try {
            this.package_name.isEmpty(); 
            // reflexion = new Reflexion();
            url_Mapping = new HashMap<String, Mapping> ();
            
            this.check_controller(package_name);
            // System.out.println("INIT _________________________________");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Error500 e) {
            error500 = e;
            e.printStackTrace();
        }
    }



    /*** doGet */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        verb = Verb.GET;
        processRequest(request, response);
    }   

    /** * doPost */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        verb = Verb.POST;
        processRequest(request, response);
    }


    

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        if (error500 instanceof Error500) {
            response.setContentType("text/html;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Front_controller.HTML500(Front_controller.error500.getMessage()));
        } else {
            String contextPath = request.getContextPath();
            String requestURI = request.getRequestURI();
            /** lenght + 1 pour enlever `/`*/
            String url = requestURI.substring(contextPath.length());
            // System.out.println("URL "+ url+" "+this.url_Mapping.size());

            // for (String u : this.url_Mapping.keySet()) {
            //     System.out.println(u);
            // }

            Mapping mapping = this.url_Mapping.get(url);
            this.traite_mapping(request, response, url, mapping, this.verb);
        }
    }

    // EXECUTION MAPPING ...
    private void traite_mapping (HttpServletRequest request, HttpServletResponse response, String url, Mapping mapping, Verb verb) throws IOException {

        try {
            VerbAction verbAction = get_VerbAction(request, mapping, verb);
            String className_ctrl = mapping.getClasse();

            execute_mapping (className_ctrl, verbAction, url, response, request);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Errors e) {
            e.printStackTrace();
        } catch (Error404 e) {
            response.setContentType("text/html;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(Front_controller.HTML404 (e.getMessage(), request.getContextPath()));
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (Error500 e) {
            response.setContentType("text/html;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(Front_controller.HTML500(e.getMessage()));
            e.printStackTrace();
        }

    }

    private VerbAction get_VerbAction (HttpServletRequest request, Mapping mapping, Verb verb) throws Error404 {
        if (mapping != null) {
            return  mapping.getVerbAction_by(verb);
        } else {
            throw new Error404 ("l'URL "+request.getRequestURL()+" est introuvable\r\n");
        }
    }
    
    private void execute_mapping (String className_ctrl, VerbAction verbAction, String uri, HttpServletResponse response, HttpServletRequest request) throws IOException, Errors, Error404, ServletException, Error500 {

        /**
         * EXECUTION MAPPING
         */


        // ETAPE 1 : METHODE exposer en REST API
        if (verbAction == null) {
            throw new Error500("VERB NON TROUVER ...");
        } 
        
        Object object_returnFCT = traite_MethodController (className_ctrl, verbAction, request, response);
        if (verbAction.isRestAPI()) {
            Gson gson = new Gson();
            PrintWriter out = response.getWriter();
            if (object_returnFCT != null && object_returnFCT instanceof ModelView) {
                ModelView model_view = (ModelView) object_returnFCT;
                out.print(gson.toJson(model_view.getData()));
            } else {
                out.print(gson.toJson(object_returnFCT));
            }
            out.close();
        } else if (object_returnFCT == null) System.out.println("TYPE DE RETOUR VOID");
        // ETAPE 2 : OBJECT SIMPLE EN RETOUR
        else objet_returnSIMPLE(object_returnFCT, request, response);
        
    }



    private void check_controller (String package_name)  throws Error500 {

        // ____ charger package ____
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String package_path = package_name.replace('.', '/');
        URL resource = classLoader.getResource(package_path);
        try {
            if (resource == null) {
                // il n'y a rien dans le package na tsy miexiste ...
                throw new Error500("(web.xml - [param-value]) PACKAGE CONTROLLER N'EXISTE PAS OU LE PACKAGE EST VIDE");
            } else {
                URI uri = resource.toURI(); // maka uri-package
                Path package_ = Paths.get(uri);
                Errors error = new Errors();
                // ____ parcourir tous les fichiers dans le paquet ____
                Files.walk(package_).filter(fichier -> fichier.toString().endsWith(".class"))
                .forEach(fichier -> {
                    // System.out.println("FONTION "+fichier);
                    try { this.validControlleur(fichier); } 
                    catch (Error500 e) {
                        // manapaka an'le boucle raha misy exception fa tsy afaka throw exception
                        error.setMessage(e.getMessage());
                    }
                });
                if (!error.getMessage().isEmpty()) {
                    throw new Error500(error.getMessage());
                }
            }

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }   


    private void validControlleur (Path fichier) throws Error500 {
        try {
            String path_class = package_name+"."+fichier.getFileName().toString().replace(".class", "");
            Class <?> classe = Class.forName(path_class);
            
            
            
            /** TRAITEMENT D'UN CONTROLLEUR et SES FONCTION
             //  sady annoter controlleur
             */

            if (classe.isAnnotationPresent(Controlleur.class)) {
            //     System.out.println("annoter "+classe.getName());
                Method[] methods = classe.getDeclaredMethods();

                for (Method method : methods) {
                    VerbAction VA = this.conf_verbAction (method, classe);
                    this.check_verbAction (VA, classe.getName());
                }
            }
            // else skip _______
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    

    private VerbAction conf_verbAction (Method method, Class <?> classe ) {
        
        // GET PARAMETRES DU FONCTION ...
        Paranamer paranamer = new AdaptiveParanamer();
        Parameter[] parameters = method.getParameters();
        Parametre params[] = new Parametre[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            params[i] = new Parametre(paranamer.lookupParameterNames(method)[i], parameters[i]);
        }

        // voir si la methode est GET ou POST par defaut GET(tsy annoter le methode)
        VerbAction VA = null;
        boolean restAPI = false;
        if (method.isAnnotationPresent(RestAPI.class)) {
            restAPI = true;
        }


        if (method.isAnnotationPresent(Get.class)) VA = new VerbAction(method.getName(), Verb.GET, method.getAnnotation(Get.class).url(), restAPI);
        else if (method.isAnnotationPresent(Post.class)) VA = new VerbAction(method.getName(), Verb.POST, method.getAnnotation(Post.class).url(), restAPI);
        else VA = new VerbAction(method.getName(), Verb.GET, method.getName(), restAPI);
        
        VA.setParametres(params); // ajouter les parametres dans le verbAction VA
        return VA;
    }


    private void check_verbAction (VerbAction verbAction, String class_name) throws Error500 {
        Mapping mapp = url_Mapping.get(verbAction.getUri());

        if (mapp != null) {
            if (!mapp.getClasse().equals(class_name)) throw new Error500 ("DES METHODS ASSICIER A UN MEME URL["+verbAction.getUri()+"] DE VERB DIFFERENT DOIT ETRE CONTENU DANS UN SEUL ET MEME CLASS");
            if (!mapp.getVerbActions().add(verbAction)) throw new Error500 ("LA METHOD ["+verbAction.getMethode()+"] DANS LA CLASS ["+class_name+"] ASSOCIE PAR  L'URL ["+verbAction.getUri()+"] DOIT ETRE DE VERB DIFFERENT QUE L'AUTRE");
            // else SKIP __
        } else {
            mapp = new Mapping(class_name);
            mapp.getVerbActions().add(verbAction);
            url_Mapping.put(verbAction.getUri(), mapp);
        }
    }

    
    private static String HTML500 (String message) {
        String html500 = 
        "<!DOCTYPE html>\r\n" +
        "<html lang=\"en\">\r\n" +
        "<head>\r\n" +
        "    <meta charset=\"UTF-8\">\r\n" +
        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" +
        "    <title>500 Error - Internal Server Error</title>\r\n" +
        "    <style>\r\n" +
        Front_controller.style +
        "    </style>\r\n" +
        "</head>\r\n" +
        "<body>\r\n" +
        "\r\n" +
        "    <h1>500</h1>\r\n" +
        "    <h2>Oops! Something went wrong</h2>\r\n" +
        "    <p>The server encountered an internal error and was unable to complete your request. Please try again.</p>\r\n" +
        "    <p><b>"+message+".</b></p>\r\n" +
        "</body>\r\n" +
        "</html>"; 
        return html500;
    }

    private static String HTML404 (String message, String contextPath) {
        String html404 = "<!DOCTYPE html>\r\n" +
        "<html lang=\"en\">\r\n" +
        "<head>\r\n" +
        "    <meta charset=\"UTF-8\">\r\n" +
        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" +
        "    <title>404 Error - Page Not Found</title>\r\n" +
        "    <style>"+
        Front_controller.style +
        "    </style>\r\n" +
        "</head>\r\n" +
        "<body>\r\n" +
        "\r\n" +
        "    <h1>404</h1>\r\n" +
        "    <h2>Oops! Nothing was found</h2>\r\n" +
        "    <p>The page you are looking for might have been removed, had its name changed, or is temporarily unavailable.</p>\r\n" +
        "    <p><b>"+message+"</b></p>\r\n" +
        "    <p><a href=\""+contextPath+"\">Return to homepage</a></p>\r\n" +
        "</body>\r\n" +
        "</html>";
    
        return html404;
    }




    
    private Object traite_MethodController (String className_ctrl, VerbAction verbAction, HttpServletRequest request, HttpServletResponse response) {
        
        try {


            Class<?> class_ctrl = Class.forName(className_ctrl);
            Object ctrl = class_ctrl.getDeclaredConstructor().newInstance();
            
            Object[] params = new Object[verbAction.getParametres().length];
            Class <?> [] type_params = new Class [verbAction.getParametres().length];
            for (int i = 0; i < verbAction.getParametres().length; i ++) {
                Object[] type__param = verbAction.getParametres()[i].get_config_param(request);
                type_params[i] = (Class<?>) type__param[0]; // indice 0 type du parametre
                params[i] = type__param[1]; // indice 1 le parametre
            }

            // System.out.println("TAILLE "+params.length+" "+type_params.length);
            // Object obj_retour = reflexion.execute_METHODE(ctrl, verbAction.getMethode(), type_params, params);
            Object obj_retour = Reflexion.executeMethod_WR(ctrl, verbAction.getMethode(), params, type_params);

            return obj_retour;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private void objet_returnSIMPLE (Object obj_retour, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, Error500 {
        /**
         * traiter l'objet retouner par la
         * fonction du controlleur specifier dans 
         * l'objet Mapping 
         */


        RequestDispatcher dispatcher = null;

        if (obj_retour  instanceof ModelView) {
            // traitement model view
            ModelView model_view = (ModelView) obj_retour;

            dispatcher = request.getRequestDispatcher(model_view.getUrl());
            model_view.getData().forEach((cle, valeur) -> {
                request.setAttribute(cle, valeur);
            });

            dispatcher.forward(request, response);
        } else if (obj_retour instanceof String) {
            if (((String) obj_retour).startsWith("redirect")) {
                this.redirect (response, request, (String) obj_retour);
            } else {
                // PrintWriter out = response.getWriter();
                String text = "<!DOCTYPE html>\r\n"+
                "<html lang=\"en\">\r\n"+
                "    <head>\r\n"+
                "        <meta charset=\"UTF-8\">\r\n"+
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"+
                "        <title></title>\r\n"+ // titre modifiable
                "    </head>\r\n"+
                "    <body>\r\n"+
                "        <h2>"+this.get_classeName()+"</h2>\r\n"+
                "        <p>"+obj_retour.toString()+"</p>\r\n"+
                "    </body>\r\n"+
                "</html>";
                // out.close();

                response.getWriter().write(text);

            }
        } else {
            throw new Error500("OBJET DE RETOUR NON RECONNNU");
        }
    }

    //  FONCTION POUR TRAITER LES RETOUR `STRING` DU CONTROLLEUR
    private void redirect (HttpServletResponse response, HttpServletRequest request, String retour) throws IOException, Error500 {
        // redirect:/url
        
        Pattern pattern = Pattern.compile("^redirect:(/.*)");
        Matcher matcher = pattern.matcher(retour);
        
        if (matcher.matches()) {
            String url = matcher.group(1);  // Extrait "url : le mot entre ()"

            response.sendRedirect(request.getContextPath()+url);
            // this.traite_mapping(request, response, url, mapping, Verb.GET);
        } else {
            throw new Error500("FORMAT `redirect incorrect`; VALIDE FORMAT: redirect:/URL");
        }

    }



    private static String style = 
    "        body {\r\n" +
    "            font-family: consolas;\r\n" +
    "            background-color: #fff;\r\n" +
    "            color: #555;\r\n" +
    "            text-align: center;\r\n" +
    "            padding: 120px;\r\n" +
    "        }\r\n" +
    "        \r\n" +
    "        h1 {\r\n" +
    "            font-size: 120px;\r\n" +
    "            color: #f4645f;\r\n" +
    "            margin: 0;\r\n" +
    "        }\r\n" +
    "        \r\n" +
    "        h2 {\r\n" +
    "            font-size: 24px;\r\n" +
    "            margin: 20px 0;\r\n" +
    "        }\r\n" +
    "        \r\n" +
    "        p {\r\n" +
    "            font-size: 16px;\r\n" +
    "            color: #777;\r\n" +
    "        }\r\n" +
    "        \r\n" +
    "        a {\r\n" +
    "            color: #f4645f;\r\n" +
    "            text-decoration: none;\r\n" +
    "        }";


    private String get_classeName () {
        return this.getClass().getSimpleName();
    }

}
