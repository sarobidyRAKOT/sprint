package mg.itu.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.net.URISyntaxException;
import java.net.URL;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import mg.itu.annotation.*;

public class Front_controller  extends HttpServlet {

    protected String package_name;
    protected ArrayList <Class <?>> list_controllers;

    @Override
    public void init(ServletConfig config) throws ServletException {
        //  Auto-generated method stub
        /**
         * maka an le param-value (web.xml) -> package
         */
        super.init(config);
        this.package_name = config.getInitParameter("package_controllers");
    }


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");


        // CHECK CONTROLLER _______
        this.list_controllers = new ArrayList <Class <?>> ();
        this.check_controller();

        try (PrintWriter out = response.getWriter()) {
            out.println(request.getRequestURL()+"</br>");

            for (Class<?> classs : list_controllers) {
                out.println(classs.getName()+"</br>");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    private void check_controller () {
        /**
         * maka ny controller reetr
         * ka Annoter controller 
         */

        // ____ charger package ____
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String package_path = this.package_name.replace('.', '/');
        URL resource = classLoader.getResource(package_path);
        try {
            Path package_ = Paths.get(resource.toURI());
            Files.walk(package_).filter(fichier -> fichier.toString().endsWith(".class"))
            // ____ parcourir les fichiers ____
            .forEach(fichier -> {
                String path_class = this.package_name+"."+fichier.getFileName().toString().replace(".class", "");
                try {
                    Class <?> classs = Class.forName(path_class);
                    this.valid_controller(classs);
                } catch (ClassNotFoundException e) {
                    // Auto-generated catch block
                    e.printStackTrace();
                }
            });

            controller_checked = true;
        } catch (URISyntaxException | IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void valid_controller (Class <?> classe) {
        if (classe.isAnnotationPresent(Annotation_controller.class)
        && !Modifier.isAbstract(classe.getModifiers())) {
            this.list_controllers.add(classe);
        }
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
