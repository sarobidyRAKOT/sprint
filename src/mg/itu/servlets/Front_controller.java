package mg.itu.servlets;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.*;
import java.util.*;
import java.net.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import mg.itu.annotation.*;
import mg.itu.beans.Mapping;
import mg.itu.beans.ModelView;
import mg.itu.reflect.Reflexion;

public class Front_controller  extends HttpServlet {

    protected String package_name;
    protected HashMap <String, Mapping> lists;
    protected Reflexion reflexion;

    @Override
    public void init(ServletConfig config) throws ServletException {
        //  Auto-generated method stub
        /**
         * maka an le param-value (web.xml) -> package
         */
        super.init(config);
        this.package_name = config.getInitParameter("package_controllers");
        reflexion = new Reflexion();
        lists = new HashMap <String, Mapping> ();
        this.check_controller(package_name);
    }


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        PrintWriter out = response.getWriter();
        out.println(request.getRequestURL() +"<h1>Front Controller</h1>");
            
        String mapp = "/"+this.get_dernier_uri(request.getRequestURI());
        Mapping mapping = lists.get(mapp);
        if (mapping != null) {   
            traite_mapping(mapping, request, response);
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("autre_pages/ctrl_invalid.jsp");
            request.setAttribute("mapp", mapp);
            dispatcher.forward(request, response);
        }
        out.close();
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
            } else if (obj_retour instanceof String) {
                dispatcher = request.getRequestDispatcher("pages/aff_string.jsp");
                request.setAttribute("value", obj_retour.toString());
            } else {
                dispatcher = request.getRequestDispatcher("autre_pages/non_reconnu.jsp");
                request.setAttribute("value", "Non Reconnu");
            }
            dispatcher.forward(request, response);
        } catch (Exception e) {
            /**
             // EXCEPTION : 
             * InstantiationException, 
             * IllegalAccessException, 
             * IllegalArgumentException, 
             * InvocationTargetException, 
             * NoSuchMethodException, 
             * SecurityException
             */
            // e.printStackTrace();
            e.printStackTrace();
        }
    }


    private void check_controller (String pack) {
        /**
         * maka ny controller reetr
         * ka Annoter controller 
         */

        // ____ charger pack ____
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String package_path = pack.replace('.', '/');
        URL resource = classLoader.getResource(package_path);
        try {
            Path package_ = Paths.get(resource.toURI());
            Files.walk(package_).filter(fichier -> fichier.toString().endsWith(".class"))
            // ____ parcourir les fichiers ____
            .forEach(fichier -> {
                String path_class = pack+"."+fichier.getFileName().toString().replace(".class", "");
                try {
                    Class <?> classs = Class.forName(path_class);
                    this.valid_controller(classs);
                } catch (ClassNotFoundException e) {
                    // Auto-generated catch block
                    e.printStackTrace();
                }
            });

        } catch (URISyntaxException | IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void valid_controller (Class <?> classe) {

        if (classe.isAnnotationPresent(Annotation_controller.class)
        && !Modifier.isAbstract(classe.getModifiers())) {
            Method[] methods = classe.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Get.class)) {
                    Get get = method.getAnnotation(Get.class);
                    this.lists.put(get.value(), new Mapping(classe.getSimpleName(), method.getName()));
                } // else SKIP _______
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

}
