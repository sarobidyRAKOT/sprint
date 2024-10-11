package mg.itu.servlets;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.*;
import java.util.*;

import com.google.gson.Gson;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

import java.net.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import mg.itu.Err.Errors;
import mg.itu.annotation.*;
import mg.itu.beans.*;
import mg.itu.reflect.Reflexion;
    
public class Front_controller extends HttpServlet {

    protected String package_name;
    protected HashMap <String, Mapping> url_Mapping;
    protected Reflexion reflexion;
    private boolean init_error = false;
    protected Error error;

    private boolean post = false;
    private boolean get = false;

    @Override
    public void init(ServletConfig config) throws ServletException {
        //  Auto-generated method stub
        /**
         * maka an le param-value (web.xml) -> package
         */
        super.init(config);

        this.package_name = config.getInitParameter("package_controllers");
        
        try {
            this.package_name.isEmpty();
            reflexion = new Reflexion();

            url_Mapping = new HashMap<String, Mapping> ();

            this.check_controller(package_name);
        } catch (URISyntaxException | IOException e) {
            // TOKONY TSY TAFIDITRA ATO ___
            System.err.println("\n");
            e.printStackTrace();
        } catch (NullPointerException e) {
            /**
             * PROBLEME TSY AZO LE PACKAGE AN AM INIT (web.xml)
             *  - param-name : package_controllers (tsy maintsy io)
             */
            this.init_error = true;
            String err = "param-name diso ao amin'ny web.xml, [correct param-name]: <param-name>package_controllers</param-name>";
            this.error = new Error(err);
            System.err.println("\n[ERROR-USER]: "+err);
        } catch (Errors e) {
            /** paquet controlleur vide (pas de classe annoter [Controller]) */
            this.init_error = true;
            System.err.println("\n[ERROR-USER]: "+e);
        }
    }



    /*** doGet */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        this.setGet(true);
        this.setPost(false);
        processRequest(request, response);
    }

    /** * doPost */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        this.setGet(false);
        this.setPost(true);
        processRequest(request, response);
    }


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        /**
         * VITA MINTSY NY INIT VO MANDALO ATU AM TY FUNCTION TY ___
         * raha misy erreur dia dispache-na makany amin'ny erreur framework
         */
        RequestDispatcher dispatcher =request.getRequestDispatcher("errors/error_framework.jsp");
        
        if (init_error) {
            request.setAttribute("error", this.error);
            dispatcher.forward(request, response);
        } else {
            // RAHA TSY MISY EXCEPTION NY CHECK CONTROLLER 
            try {
                String key = "/"+this.get_dernier_uri(request.getRequestURI()); // uri

                Mapping mapping = this.hashMap_Mapping.get(key);
                this.trait_mapping (mapping, key, dispatcher, request, response);
            } catch (Exception e) {
                request.setAttribute("error", new Error(e.getMessage()));
                dispatcher.forward(request, response);
            }

        }

    }


    private void objet_returnSIMPLE (Object obj_retour, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
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
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>\r\n"+
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
            "</html>");
            out.close();
        } else {
            dispatcher = request.getRequestDispatcher("errors/error_framework.jsp");
            request.setAttribute("error", new Error("Non reconnu"));
            dispatcher.forward(request, response);
        }
    }


    private String get_dernier_uri (String request_uri) {
        int indexlast_slash = request_uri.lastIndexOf('/');
        if (indexlast_slash == -1) {
            // SI AUCUN N'EST TROUVER ______
            return request_uri;
        } else {
            return request_uri.substring(indexlast_slash+1);
        } 
    }

    private void trait_mapping (Mapping mapping, String uri, RequestDispatcher dispatcher, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (mapping != null) {
            // misy le mapping
            /** TRAITE_MAPPING miretourne an le objet retourner par la fonction (FCT) */
            Object object_returnFCT = traite_controller (mapping, request, response);
            // ETAPE 1 : METHODE exposer en REST API
            if (mapping.getRestAPI()) {
                Gson gson = new Gson();
                PrintWriter out = response.getWriter();
                if (object_returnFCT instanceof ModelView) {
                    ModelView model_view = (ModelView) object_returnFCT;
                    out.print(gson.toJson(model_view.getData()));
                } else {
                    out.print(gson.toJson(object_returnFCT));
                }
                out.close();
            }
            // ETAPE 2 :
            else objet_returnSIMPLE(object_returnFCT, request, response);
            
        } else if (uri.equals("/") && mapping == null) {
            // PAGE INDEX PAR DEFAUT 
            response.sendRedirect("index.jsp");
        } else {
            dispatcher = request.getRequestDispatcher("errors/error_framework.jsp");
            String err = "l'URL "+request.getRequestURL()+" est introuvable\r\n"+
            "uri: "+uri+" introuvable - invalide";
            request.setAttribute("error", new Error(err));
            dispatcher.forward(request, response);
        }
    }

    

    private Object traite_controller (Mapping mapping, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String ctrl_className = this.package_name+"."+mapping.getClass_name();
        try {
            Class <?> ctrl_class = Class.forName(ctrl_className);
            
            Object controller = ctrl_class.getDeclaredConstructor().newInstance();
            Object[] params_fonct = new Object[mapping.getParams().size()];
            Class <?>[] type_params = new Class[params_fonct.length]; 
            boolean pas_annoter = false;
            int i = 0;
            for (Parametre parametre : mapping.getParams()) {
                /** TRAITEMENT PARAMETRES de la fonction */
                Object param = null;
                Class <?> type = parametre.getParameter().getType();
                if (parametre.getParameter().isAnnotationPresent(Param.class)) {
                    Param key = parametre.getParameter().getAnnotation(Param.class);
                    String value = request.getParameter(key.value());
                    if (value != null) {
                        param = type.getConstructor(String.class).newInstance(value);
                    } else param = null;
                } else if (parametre.getParameter().isAnnotationPresent(Param_obj.class)) {
                    Class <?> classe = parametre.getParameter().getType();
                    param = process_traite_ParamObj(classe, request);
                } else if (type.isAssignableFrom(MySession.class)) {
                    
                    param = new MySession(request);
                } else { // sinon (pas annoter no sady tsy MySession)
                    pas_annoter = true;
                    break;
                }
                params_fonct[i] = param;
                type_params[i] = type; // classe
                ++ i;
            }

            if (pas_annoter) {
                /** si un parametre n'est pas annoter */
                throw new Exception("ETU 002491 param tsy annoter !!");
            } 
            else {
                Object obj_retour = reflexion.execute_METHODE(controller, mapping.getMethode_name(), type_params, params_fonct);
                return obj_retour;
            }
            
        } catch (Exception e) {
            /**
             // LISTE EXCEPTION POSSIBLES;
             // EXCEPTION : 
             * InstantiationException, 
             * IllegalAccessException, 
             * IllegalArgumentException, 
             * InvocationTargetException, 
             * NoSuchMethodException, 
             * SecurityException
             */
            throw e;
        }
    }



    private Object process_traite_ParamObj (Class <?> classe, HttpServletRequest request) throws Exception {
        Field[] attrs = classe.getDeclaredFields();
        String value = null;
        Object obj = classe.getConstructor().newInstance();

        for (Field field : attrs) {
            String setter_name = "set"+premier_lettreMaj(field.getName());
            if (field.isAnnotationPresent(Attr.class)) {
                String key = field.getAnnotation(Attr.class).value();
                value = request.getParameter(key);
            } else {
                String key = field.getName();
                value = request.getParameter(key);
            }

            Method method = classe.getDeclaredMethod(setter_name, String.class);
            method.invoke(obj, value);
            
        }
        return obj;
    }

    private String premier_lettreMaj (String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return string.substring(0, 1)
        .toUpperCase() + string.substring(1);
    }





    private void check_controller (String package_name)  throws URISyntaxException, Errors, IOException {

        // ____ charger package ____
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String package_path = package_name.replace('.', '/');
        URL resource = classLoader.getResource(package_path);
        try {
            if (resource == null) {
                // package vide na tsy miexiste ...
                String err = "PACKAGE CONTROLLER EMPTY nom du paquet: "+this.package_name;
                throw new Errors(err);
            } else {
                URI uri = resource.toURI(); // maka uri-package
                Path package_ = Paths.get(uri);
                Errors errors = new Errors(); // pour stocker l'erreurs
                Files.walk(package_).filter(fichier -> fichier.toString().endsWith(".class"))
                // ____ parcourir tous les fichiers dans le paquet ____
                .forEach(fichier -> {
                    try { this.validControlleur(fichier);} 
                    catch (Errors e) { errors.ajout_message(e.getMessage()); }
                });

                if (!errors.getMessage().isEmpty()) {
                    this.error = new Error(errors.getMessage());
                    this.init_error = true; // ACTIVER L'ERREUR INIT
                }
            }

        } catch (URISyntaxException | IOException e) {
            // EXCEPTION TSY TOKONY ISEHO ...
            throw e;
        }
    }

    private void validControlleur (Path fichier) throws Errors {
        try {
            String path_class = package_name+"."+fichier.getFileName().toString().replace(".class", "");
            Class <?> classe = Class.forName(path_class);
                    
            /**
             * mila traitement 
             * mitovy uri
             * on stock les erreurs
             */
            /** TRAITEMENT D'UN CONTROLLEUR et SES FONCTION
             * 
             */
            //  sady annoter controlleur
            if (classe.isAnnotationPresent(Controller.class)
            && !Modifier.isAbstract(classe.getModifiers())) {
                
                Mapping mapping = new Mapping(classe.getSimpleName());

                Method[] methods = classe.getDeclaredMethods();
                String error = "";// pour stocker les erreur

                for (Method method : methods) {
                    VerbAction VA = this.traitement_methode(method, classe);

                    // String err = config_mapping(method, classe);
                    // error += err;

                    mapping.addVerbAction(VA); // ajouter les verbAction dans le mapping
                }

                if (!error.isEmpty()) {
                    throw new Errors(error);
                } else {
                    this.hashMap_Mapping.put("", mapping); 
                }
            }
            // else skip _______
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Errors e) {
            /**
             * rah misy mitovy uri
             * errors ao am classe ray ...
             * errors.ajout_message(e.getMessage());
             */
            throw e;
        }
    }

    

    private VerbAction traitement_methode (Method method, Class <?> classe ) {
        /**
         * get parametre ...
         * nom methode
         */
        ArrayList <Parametre> params = new ArrayList <Parametre> ();
        Paranamer paranamer = new AdaptiveParanamer();
        Parameter[] parameters = method.getParameters();
        String[] params_name = paranamer.lookupParameterNames(method);

        for (int i = 0; i < params_name.length; i++) {
            params.add(new Parametre(params_name[i], parameters[i]));
        }

        // voir si la methode est GET ou POST par defaut GET[raha tsy annoter le methode]
        if (method.isAnnotationPresent(Get.class)) {
            Get get = method.getAnnotation(Get.class);
        }
        return null;
    }




    private String config_mapping (Method method, Class <?> classe) {
        /**
         // return un stock d'erreurs ___
         * meme uri
         */
        String error = "";
        String classe_name = classe.getSimpleName();

        ArrayList <Parametre> params = new ArrayList <Parametre> ();
        Paranamer paranamer = new AdaptiveParanamer();
        Parameter[] parameters = method.getParameters();
        String[] params_name = paranamer.lookupParameterNames(method);

        for (int i = 0; i < params_name.length; i++) {
            params.add(new Parametre(params_name[i], parameters[i]));
        }

        try {
            if (method.isAnnotationPresent(Get.class)) this.get_methodGET(classe_name, method, params);
            else if (method.isAnnotationPresent(Post.class)) this.get_methodPOST(classe_name, method, params); 
            else {
                
            }
            // this.get_methodGET(classe_name, method, params);
        } catch (Errors e) {
            // ERRORS mitovy uri le methode
            error += e.getMessage();
        }
        return error; 
    }

    // creer une fonction pour zfficher hello world dans le console


    private void _methodGET (String classe_name, Method method, ArrayList <Parametre> params) throws Errors {
        String verb = "get";
        Get get = method.getAnnotation(Get.class);

        this.hashMap_Mapping.forEach(key, map)

        Mapping mapping = this.hashMap_Mapping.get(get.url()); // get mapping dans la liste do get
        if (mapping != null) {
            String msg = "URL MITOVY AO AMIN'NY CLASS: ["+mapping.getClass_name()+"] SY ["+classe_name+"] AVEC L'URI: "+get.url()+"";
            throw new Errors (msg);
        } else {
            mapping = new Mapping(classe_name, method.getName(), params);
            if (method.isAnnotationPresent(RestAPI.class)) mapping.setRestAPI(true);
            this.hashMap_Mapping.put(get.url(), mapping); // ALEFA AO ANATY LIST METHODE DOGET
        }
    }

    private void _methodPOST (String classe_name, Method method, ArrayList <Parametre> params) throws Errors {
        Post post = method.getAnnotation(Post.class);
        System.out.println("POST");

        Mapping mapping = this.hashMap_Mapping.get(post.url());
        if (mapping != null) {
            String msg = "URL MITOVY AO AMIN'NY CLASS: ["+mapping.getClass_name()+"] SY ["+classe_name+"] AVEC L'URI: "+post.url()+"";
            throw new Errors (msg);
        } else {
            mapping = new Mapping(classe_name, method.getName(), params);
            if (method.isAnnotationPresent(RestAPI.class)) mapping.setRestAPI(true);
            this.hashMap_Mapping.put(post.url(), mapping);
        }
    }



    private String get_classeName () {
        return this.getClass().getSimpleName();
    }

    private void setGet(boolean get) { this.get = get; }
    private void setPost(boolean post) { this.post = post; }
}
