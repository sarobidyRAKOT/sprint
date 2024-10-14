package mg.itu.beans;

import java.lang.reflect.Parameter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import mg.itu.Err.Errors;
import mg.itu.annotation.Attr;
import mg.itu.annotation.Param;
import mg.itu.annotation.Param_obj;

public class Parametre {

    private String nom;
    private Parameter parameter;



    public Parametre (String nom, Parameter parameter) {
        this.nom = nom;
        this.parameter = parameter;
    }

    public Object[] get_config_param (HttpServletRequest request) throws Errors, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Class <?> type = this.getParameter().getType();
        Object param = null;
        if (this.getParameter().isAnnotationPresent(Param.class)) {
            String value = request.getParameter(this.getParameter().getAnnotation(Param.class).value());
            if (value != null) {
                param = type.getConstructor(String.class).newInstance(value);
            } else param = null;
        } else if (this.getParameter().isAnnotationPresent(Param_obj.class)) {
            Class <?> classe = this.getParameter().getType();
            param = process_traite_ParamObj(classe, request);
        } else if (type.isAssignableFrom(MySession.class)) {
        
            param = new MySession(request);
        } else { // sinon (pas annoter no sady tsy MySession)
            type = null;
            throw new Errors ("ETU 002491 PARAMETRE FONCTION CONTROLLER TSY ANNOTER [Param/Param_obj] NO SADY TSY [MySession]");
        }

        Object[] type__param = new Object[2];
        type__param[0] = type;
        type__param[1] = param;
        return type__param;
    }

    private Object process_traite_ParamObj (Class <?> classe, HttpServletRequest request) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        Field[] attrs = classe.getDeclaredFields();
        String value = null;
        Object obj = classe.getConstructor().newInstance();

        for (Field field : attrs) {
            String setter_name = "set"+field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            if (field.isAnnotationPresent(Attr.class)) {
                value = request.getParameter(field.getAnnotation(Attr.class).value());
            } else {
                value = request.getParameter(field.getName());
            }

            Method method = classe.getDeclaredMethod(setter_name, String.class);
            method.invoke(obj, value);
            
        }
        return obj;
    }

    public Parameter getParameter() { return parameter; }
    public String getNom() { return nom; }
}