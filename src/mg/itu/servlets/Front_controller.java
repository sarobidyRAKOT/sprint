package mg.itu.servlets;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.*;
import java.util.*;
import java.net.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import mg.itu.Err.Errors;
import mg.itu.annotation.*;
import mg.itu.beans.Mapping;
import mg.itu.beans.ModelView;
import mg.itu.beans.Param;
import mg.itu.reflect.Reflexion;

public class Front_controller  extends HttpServlet {

    protected String package_name;
    protected HashMap <String, Mapping> do_gets;
    // protected HashMap <String, Mapping> do_posts;
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
            // do_posts = new HashMap <String, Mapping> ();

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
        mapping = do_gets.get(mapp);

        // if (get) {
        //     // sila METHODE est get
        //     mapping = do_gets.get(mapp);
        // } else if (post) {
        //     // sila METHODE est post
        //     mapping = do_posts.get(mapp);
        // }

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
                String key = "/"+this.get_dernier_uri(request.getRequestURI());
                ArrayList <Param> params = traitement_params(request.getQueryString());
                
                Mapping mapping = get_mapping(key);
                if (mapping != null) {
                    // misy le mapping
                    traite_mapping(mapping, params, request, response);
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

    
    private ArrayList <Param> traitement_params (String string_params) throws Errors {

        /**
         * mamerina null null le string_params
         * sinon ArrayList Param (key, value)
         */
        ArrayList <Param> parametres = null;

        if (string_params != null && string_params.length() > 0) {
            String[] params = string_params.split("&&");
            parametres = new ArrayList <Param> (params.length);

            for (String param : params) {
                String[] key_value = param.split("=");
                if (key_value.length == 2) {
                    // key value parfait
                    /** REHEFA TSY key, value de tsy mety */                
                    parametres.add(new Param(key_value[0], key_value[1]));
                } else {
                    throw new Errors("params doit etre de la forme key=value"); 
                }
            }
        }
        return parametres;
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

    

    private void traite_mapping (Mapping mapping, ArrayList <Param> params, HttpServletRequest request, HttpServletResponse response) {
        
        String ctrl_className = this.package_name+"."+mapping.getClass_name();
        RequestDispatcher dispatcher = null;
        try {
            Class <?> ctrl_class = Class.forName(ctrl_className);
            
            Object controller = ctrl_class.getDeclaredConstructor().newInstance();
            Object[] params_fonct = new Object[mapping.getParams().size()];
            Class <?>[] type_params = new Class[mapping.getParams().size()]; 

            int i = 0;
            for (Parameter parameter : mapping.getParams()) {
                Request_param request_param = parameter.getAnnotation(Request_param.class);
                // Param param = Param.get_param(request_param.value(), params);
                String value = request.getParameter(request_param.value());
                Object param = null;
                if (value != null) {
                    param = parameter.getType().getConstructor(String.class).newInstance(value);
                } else {
                    param = null;
                }
                
                params_fonct[i] = param;
                type_params[i] = parameter.getType();
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
            // LISTE EXCEPTION POSSIBLES;
            /**
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
                throw new Errors();
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
        Mapping nv_mapping = null;
        Mapping mapping = null;
        String classe_name = classe.getSimpleName(),
        methode_name = method.getName();

        Parameter[] parameters = method.getParameters();
        ArrayList <Parameter> params = new ArrayList <Parameter> ();

        for (Parameter param : parameters) {
            // recuperation params ___
            if (param.isAnnotationPresent(Request_param.class)) {
                params.add(param);
            }
        }

        if (method.isAnnotationPresent(Get.class)) {
            // METHODE GET ___
            Get get = method.getAnnotation(Get.class);
            mapping = this.do_gets.get(get.value());
            nv_mapping = new Mapping(classe_name, methode_name, params);
            if (mapping != null) {
                /**
                 * ef misy mitovy URI aminy ao */
                error += "* "+ nv_mapping.getClass_name()+" misy mitovy url, methode: GET "+nv_mapping.getMethode_name()+" - url: "+get.value()+"\r\n";
            } else {
                this.do_gets.put(get.value(), nv_mapping);
            }
        }// else SKIP _______
        return error; 
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


    private String get_classeName () {
        return this.getClass().getSimpleName();
    }

    private void setGet(boolean get) { this.get = get; }
    private void setPost(boolean post) { this.post = post; }
}
