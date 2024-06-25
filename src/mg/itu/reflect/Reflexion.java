package mg.itu.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Reflexion {
    

    public String get_nomClasse (Object object) {
        /** maka anaran le objet en parametre... */
        return object.getClass().getSimpleName();
    }
    
    
    public Class <?>[] get_paramsTYPE (Object[] params) {
        /** maka type[] object  */
        Class <?>[] type_params = null;
        if (params != null && params.length > 0) {
            type_params = new Class <?>[params.length];
            int i = 0;
            for (Object clazz : params) {
                type_params[i] = clazz.getClass();
                ++ i; //  incrementer i
            }
        }
        
        return type_params;
    }

    
    public Field get_fieldClasse_byName (ArrayList <Field> fields, String attr_name) {
        
        Field attr = null;
        for (Field field : fields) {
            if (field.getName().equals(attr_name)) {
                attr = field;
                break;
            }
        }
        return attr;
    }


    public void execute_METHOD (Object object, String methode_name, Object[] params) 
        throws Exception {
        /** EXECUTE methode SANS RETURN */
        Class <?> clazz = object.getClass();
        Class <?>[] type_params = this.get_paramsTYPE(params);

        try {
            Method method = clazz.getDeclaredMethod(methode_name, type_params);
            method.invoke(object, params); // appeller la methode
        } catch (NoSuchMethodException | SecurityException e) {
            /// throws Exception
            throw e;
        }
    }


    public Object execute_METHODE (Object object, String methode_name, Object[] params) throws Exception {
        Class <?> clazz = object.getClass();
        Class <?>[] type_params = this.get_paramsTYPE(params);
        try {
            Method method = clazz.getDeclaredMethod(methode_name, type_params);
            return method.invoke(object, params); // appeller la methode
        } catch (NoSuchMethodException | SecurityException e) {
            /// throws Exception
            throw e; 
        }
    }

    public Object execute_METHODE (Object object, String methode_name, Class<?>[] type_params, Object[] params) throws Exception {
        Class <?> clazz = object.getClass();
        try {
            Method method = clazz.getDeclaredMethod(methode_name, type_params);
            return method.invoke(object, params); // appeller la methode
        } catch (NoSuchMethodException | SecurityException e) {
            /// throws Exception
            throw e; 
        }
    }



}
