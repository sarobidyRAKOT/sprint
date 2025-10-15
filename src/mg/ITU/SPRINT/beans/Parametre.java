package mg.ITU.SPRINT.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.*;

import jakarta.servlet.http.HttpServletRequest;
import mg.ITU.SPRINT.Err.Error500;
import mg.ITU.SPRINT.Err.Errors;
import mg.ITU.SPRINT.annotation.*;

public class Parametre {

    private String nom;
    private Parameter parameter;


    public Parametre (String nom, Parameter parameter) {
        this.nom = nom;
        this.parameter = parameter;
    }


    public Object[] get_config_param (HttpServletRequest request) throws Errors, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Error500 {
        
        /**
         * TRAITREMENT DU PARAMETRES ...
         */
        
        Class <?> type = this.getParameter().getType();
        Object param = null;


        if (this.getParameter().isAnnotationPresent(Param.class)) {
            String value = request.getParameter(this.getParameter().getAnnotation(Param.class).value());
            if (value != null) param = type.getConstructor(String.class).newInstance(value);
            else param = null;
        } else if (this.getParameter().isAnnotationPresent(Param_obj.class)) {
            Class <?> classe = this.getParameter().getType();
            param = process_traite_ParamObj(classe, request);
        } else if (type.isAssignableFrom(MySession.class)) {
            param = new MySession(request);
        } else { // sinon (pas annoter no sady tsy MySession)
            type = null;
            throw new Errors ("ETU 002491 PARAMETRE FONCTION CONTROLLER TSY ANNOTER [Param/Param_obj] NO SADY TSY [MySession]");
        }

        // Pour les fichier upload -> Type de donnée MultipartFile...
        if (type.isAssignableFrom(MultipartFile.class) && this.getParameter().isAnnotationPresent(Param.class)) {
            Param p = this.getParameter().getAnnotation(Param.class);
            // System.out.println("type nultipart "+p.value());

            param = new MultipartFile (request, p.value());
        }

        Object[] type__param = new Object[2];
        type__param[0] = type;
        type__param[1] = param;
        return type__param;
    }


    private Object process_traite_ParamObj (Class <?> classe, HttpServletRequest request) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, Error500 {
        
        if (classe.isAnnotationPresent(Form_object.class)) {
            Field[] attrs = classe.getDeclaredFields();
            Object obj = classe.getConstructor().newInstance();
            
            for (Field field : attrs) {
                String setter_name = "set"+field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                Object v = "";
                Method method = null;

                if (field.isAnnotationPresent(Attr.class)) {
                    v = request.getParameter(field.getAnnotation(Attr.class).value());
                } else if (field.isAnnotationPresent(Attr_tab.class)) {
                    v = request.getParameterValues (field.getAnnotation(Attr_tab.class).value());                    
                }
                // MBOLA MISY TRAITEMENT `Object v` ra oatr ka null   
                // System.out.println(v);
                if (v != null) {
                    method = classe.getDeclaredMethod(setter_name, v.getClass());                    
                    method.invoke(obj, v);
                }
                
            }
            return obj;
        } else {
            throw new Error500("Le param_obj n'est pas annoter Form_obj");
        }
        // for (Field field : attrs) {
        //     if (field.isAnnotationPresent(Attr.class)) {
        //         value = request.getParameter(field.getAnnotation(Attr.class).value());
        //     } else {
        //         value = request.getParameter(field.getName());
        //     }

            
        // }
    }

    public Parameter getParameter() { return parameter; }
    public String getNom() { return nom; }
}