package mg.itu.servlets;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.*;
import java.util.*;
import java.net.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import mg.itu.Err.Errors;
import mg.itu.annotation.*;
import mg.itu.beans.Mapping;
import mg.itu.beans.ModelView;
import mg.itu.reflect.Reflexion;

public class Front_controller  extends HttpServlet {

    protected String package_name;
    protected HashMap <String, Mapping> lists;
    protected Reflexion reflexion;
    private boolean init_error = false;
    protected Error error;

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
            lists = new HashMap <String, Mapping> ();
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
        }

            
        String mapp = "/"+this.get_dernier_uri(request.getRequestURI());
        Mapping mapping = lists.get(mapp);
        if (mapping != null) {
            // misy le mapping "m'existe le uri"
            traite_mapping(mapping, request, response);
        } else {
            dispatcher = request.getRequestDispatcher("errors/error_framework.jsp");
            String err = "l'URL "+request.getRequestURL()+" est introuvable\r\n"+
            "uri: "+mapp+" introuvable - invalide";
            request.setAttribute("error", new Error(err));
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

    

    private void traite_mapping (Mapping mapping, HttpServletRequest request, HttpServletResponse response) {
        
        String ctrl_className = this.package_name+"."+mapping.getClass_name();
        RequestDispatcher dispatcher = null;
        try {
            Class <?> ctrl_class = Class.forName(ctrl_className);
            Object controller = ctrl_class.getDeclaredConstructor().newInstance();
            Object obj_retour = reflexion.execute_METHODE(controller, mapping.getMethode_name(), null);

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
                        errors.ajout_message(e.getMessage());
                    }
                });

                if (!errors.getMessage().isEmpty()) {
                    // on active l'erreur de init 
                    System.out.print(errors.getMessage());
                    this.error = new Error(errors.getMessage());
                    this.init_error = true;
                }
            }

        } catch (URISyntaxException | IOException e) {
            // IOException
            // Auto-generated catch block
            e.printStackTrace();
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
                if (method.isAnnotationPresent(Get.class)) {
                    Get get = method.getAnnotation(Get.class);
                    Mapping mapping = this.lists.get(get.value());
                    Mapping nv_mapping = new Mapping(classe.getSimpleName(), method.getName());
                    if (mapping != null) {
                        /**
                         * ef misy mitovy URI aminy ao */
                        error += "* "+ nv_mapping.getClass_name()+" misy mitovy url, methode: "+nv_mapping.getMethode_name()+" - url: "+get.value()+"\r\n";
                    } else {
                        this.lists.put(get.value(), nv_mapping);
                    }
                } // else SKIP _______
            }
            if (!error.isEmpty()) {
                throw new Errors(error);
            }
        }
        // else skip _______
    }

    /*** doGet */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** * doPost */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    private String get_classeName () {
        return this.getClass().getSimpleName();
    }
}
