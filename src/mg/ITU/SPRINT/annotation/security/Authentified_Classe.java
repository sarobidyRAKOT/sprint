package mg.ITU.SPRINT.annotation.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention (RetentionPolicy.RUNTIME)
@Target ({ElementType.TYPE})
public @interface Authentified_Classe {
    
    String[] roles ();
}
