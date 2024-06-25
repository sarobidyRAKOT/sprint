package mg.itu.servlets;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.*;
import java.util.*;

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
    protected HashMap <String, Mapping> do_gets;
    protected HashMap <String, Mapping> do_posts;
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
            do_gets = new HashMap <String, Mapping> ();
            do_posts = new HashMap <String, Mapping> ();

            this.check_controller(package_name);
        } catch (URISyntaxException | IOException e) {
            // TOKONY TSY TAFIDITRA ATO ___
            e.printStackTrace();
        } catch (NullPointerException e) {
            /**
             * PROBLEME TSY AZO LE PACKAGE AN AM INIT (web.xml)
             * ERREUR web.xml
             *  - param-name : DISO -> (package_controllers)
             * tsy azo ovaina fa tsy maintsy ito 'package_controllers'
             */
            this.init_error = true;
            String err = "ERREUR web.xml\r\n"+
            " - param-name : DISO -> <param-name>...</param-name>\r\n"+
            "tsy azo ovaina fa tsy maintsy ito 'package_controllers'";
            this.error = new Error(err);
        } catch (Errors e) {
            /**
             * ERREUR NOFORONINA
             * package vide na tsy mi-existe akory
             */
            this.init_error = true;
            String err = "PACKAGE vide na PACKAGE tsy mi'existe AKORY\r\n"+
            "Verifier fichier param-value dans 'web.xml'\r\n"+
            "changer 'Package controller' par le package qui contient les controllers\r\n"+
            "<servlet>\r\n"+
            "    <init-param>\r\n"+
            "        <param-name>package_controllers</param-name>\r\n"+
            "        <param-value>Package controller</param-value>\r\n"+
            "    </init-param>\r\n"+
            "</servlet>";
            this.error = new Error(err);
        }
    }

    private Mapping get_mapping (String mapp) {
        Mapping mapping = null;
        if (get) {
            mapping = do_gets.get(mapp);
        } 
        else if (post) {
            mapping = do_posts.get(mapp);
        }
        return mapping;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        /**
         * VITA MINTSY NY INIT VO MANDALO ATU AM TY FUNCTION TY ___
         */
        RequestDispatcher dispatcher;
        
        if (init_error) {
            /**
             * GESTION ERREUR: (vo mi-lance an le application fotsiny)
             * - init
             * - cheach_conetroller
             */
            dispatcher = request.getRequestDispatcher("errors/error_framework.jsp");
            request.setAttribute("error", this.error);
            dispatcher.forward(request, response);
        } else {
           
            // ef tsy misy exception ny init sy ny
            // controller noforonon le olona
            // traitement_uri(mapp);
            try {
                String key = "/"+this.get_dernier_uri(request.getRequestURI()); // uri
                ArrayList <Key_value> params_Key_values = traitement_params(request.getQueryString()); // params
                // for (Key_value key_value : params_Key_values) {
                //     System.out.println(key_value.toString());
                // }
                Mapping mapping = get_mapping(key);
                if (mapping != null) {
                    // misy le mapping
                    traite_mapping(mapping, params_Key_values, request, response);
                } else {
                    dispatcher = request.getRequestDispatcher("errors/error_framework.jsp");
                    String err = "l'URL "+request.getRequestURL()+" est introuvable\r\n"+
                    "uri: "+key+" introuvable - invalide";
                    request.setAttribute("error", new Error(err));
                    dispatcher.forward(request, response);
                }
            } catch (Errors e) {
                // e.printStackTrace();
                dispatcher = request.getRequestDispatcher("errors/error_framework.jsp");
                request.setAttribute("error", new Error(e.getMessage()));
                dispatcher.forward(request, response);
            }

        }

    }

    
    private ArrayList <Key_value> traitement_params (String string_params) throws Errors {
        /**
         * mamerina null null le string_params
         * sinon ArrayList Param (key, value)
         */

        ArrayList <Key_value> key_values = null;
        if (string_params != null && string_params.length() > 0) {
            String[] params = string_params.split("&");
            
            key_values = new ArrayList <Key_value> (params.length);
            for (String param : params) {
                String[] key_value = param.split("=");
                String key = param.split("=")[0];
                String value = param.split("=")[1];
                if (key_value.length == 2) { // key value parfait
                    Key_value kv = new Key_value(key, value);
                    key_values.add(kv);
                } else {                
                    throw new Errors("params doit etre de la forme key=value"); 
                }
            }
        }
        return key_values;
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

    

    private void traite_mapping (Mapping mapping, ArrayList <Key_value> params_Key_values, HttpServletRequest request, HttpServletResponse response) {
        
        String ctrl_className = this.package_name+"."+mapping.getClass_name();
        RequestDispatcher dispatcher = null;
        try {
            Class <?> ctrl_class = Class.forName(ctrl_className);
            
            Object controller = ctrl_class.getDeclaredConstructor().newInstance();
            Object[] params_fonct = new Object[mapping.getParams().size()];
            Class <?>[] type_params = new Class[params_fonct.length]; 

            int i = 0;
            for (Parametre parametre : mapping.getParams()) {
                Object param = null;
                Class <?> type = parametre.getParameter().getType();
                if (parametre.getParameter().isAnnotationPresent(Param.class)) {
                    System.out.println("param");
                    Param key = parametre.getParameter().getAnnotation(Param.class);
                    String value = request.getParameter(key.value());
                    if (value != null) {
                        param = type.getConstructor(String.class).newInstance(value);
                    } else param = null;
                } else if (parametre.getParameter().isAnnotationPresent(Param_obj.class)) {
                    Class <?> classe = parametre.getParameter().getType();
                    param = process_traite_ParamObj(classe, request);
                } else { // sinon (pas annoter)
                    String key = parametre.getNom();
                    Key_value keyvalue = Key_value.get_param(key, params_Key_values); 
                    if (keyvalue != null) { // ao le izy
                        String value = request.getParameter(key);
                        param = type.getConstructor(String.class).newInstance(value);
                    } else param = null;
                }
                params_fonct[i] = param;
                type_params[i] = type; // classe
                ++ i;
            }
            
            Object obj_retour = reflexion.execute_METHODE(controller, mapping.getMethode_name(), type_params, params_fonct);
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
            e.printStackTrace();
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
            
            // method = classe.getDeclaredMethod("get"+premier_lettreMaj(field.getName()));
            // Object o = method.invoke(obj);
            // System.out.println(method.getName()+" "+o);
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

    private void check_controller (String package_name) 
        throws URISyntaxException, Errors, IOException {
        /**
         * maka ny controller reetr
         * ka Annoter controller 
         */

        // ____ charger pack ____
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String package_path = package_name.replace('.', '/');
        URL resource = classLoader.getResource(package_path);
        try {
            if (resource == null) {
                // package vide na tsy miexiste
                throw new Errors("verifier le package controlleur dans (web.xml et srcs)");
            } else {
                URI uri = resource.toURI(); // maka uri package
                Path package_ = Paths.get(uri);
                Errors errors = new Errors(); // pour stocker l'erreurs
                Files.walk(package_).filter(fichier -> fichier.toString().endsWith(".class"))
                // ____ parcourir les fichiers ____
                .forEach(fichier -> {
                    try {
                        String path_class = package_name+"."+fichier.getFileName().toString().replace(".class", "");
                        Class <?> classs = Class.forName(path_class);
                        this.valid_controller (classs);
                    } catch (ClassNotFoundException e) {
                        /**
                         * CLASSE misy fona
                         * pas besoin de throw */
                        e.printStackTrace();
                    } catch (Errors e) {
                        /**
                         * rah misy mitovy uri
                         */
                        // errors ao am classe ray ...
                        errors.ajout_message(e.getMessage());
                    }
                });

                if (!errors.getMessage().isEmpty()) {
                    // on active l'erreur de init 
                    // System.out.print(errors.getMessage());
                    this.error = new Error(errors.getMessage());
                    this.init_error = true;
                }
            }

        } catch (URISyntaxException | IOException e) {
            // IOException
            // Auto-generated catch block
            // e.printStackTrace();
            throw e;
        }
    }

    private void valid_controller (Class <?> classe) throws Errors {

        /**
         * mila traitement 
         * mitovy uri
         * on stock les erreurs
         */
        if (classe.isAnnotationPresent(Annotation_controller.class) && !Modifier.isAbstract(classe.getModifiers())) {
            Method[] methods = classe.getDeclaredMethods();
            String error = "";// pour stocker les erreurs

            for (Method method : methods) {
                String err = config_mapping(method, classe);
                error += err;
            }
            if (!error.isEmpty()) {
                throw new Errors(error);
            }
        }
        // else skip _______
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
            String param_name = params_name[i];
            Parameter parameter = parameters[i];
            params.add(new Parametre(param_name, parameter));
        }

        try {
            if (method.isAnnotationPresent(Get.class)) this.get_methodGET(classe_name, method, params);
            else if (method.isAnnotationPresent(Post.class)) this.get_methodPOST(classe_name, method, params);
            // else SKIP _______ 
        } catch (Errors e) {
            // ERRORS mitovy uri le methode
            error += e.getMessage();
        }
        return error; 
    }

    private void get_methodGET (String classe_name, Method method, ArrayList <Parametre> params) throws Errors {
        Get get = method.getAnnotation(Get.class);
        
        Mapping mapping = this.do_gets.get(get.value()); // get mapping dans la liste do get
        if (mapping != null) {
            /** * ef misy mitovy URI aminy ao */
            throw new Errors ("* "+ mapping.getClass_name()+" misy mitovy url, methode: GET "+mapping.getMethode_name()+" - url: "+get.value()+"\r\n");
        } else {
            mapping = new Mapping(classe_name, method.getName(), params);
            this.do_gets.put(get.value(), mapping);
        }
    }

    private Mapping get_methodPOST (String classe_name, Method method, ArrayList <Parametre> params) throws Errors {
        Post post = method.getAnnotation(Post.class);

        Mapping mapping = this.do_posts.get(post.value());
        if (mapping != null) {
            /** * ef misy mitovy URI aminy ao */
            throw new Errors("* "+ mapping.getClass_name()+" misy mitovy url, methode: POST "+mapping.getMethode_name()+" - url: "+post.value()+"\r\n");
        } else {
            mapping = new Mapping(classe_name, method.getName(), params);
            this.do_posts.put(post.value(), mapping);
        }
        return null;
    }


    /*** doGet */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        this.setGet(true);
        this.setPost(false);
        System.out.println("post "+post+" get "+get);
        processRequest(request, response);
    }

    /** * doPost */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        this.setGet(false);
        this.setPost(true);
        System.out.println("post "+post+" get "+get);
        processRequest(request, response);
    }


    private String get_classeName () {
        return this.getClass().getSimpleName();
    }

    private void setGet(boolean get) { this.get = get; }
    private void setPost(boolean post) { this.post = post; }
}
