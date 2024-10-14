package mg.itu.beans;

import java.util.HashSet;
import java.util.Set;

public class Mapping {
    
    protected String classe;
    protected Set <VerbAction> verbActions;

    public Mapping (String classe) {
        this.classe = classe;
        /**
         * TAILLE verbAction ...
         * 2 (post)
         */
        this.verbActions = new HashSet <VerbAction>(2);
    }    

    public VerbAction getVerbAction_by (Verb verb) {
        VerbAction verbAction = null;
        for (VerbAction va : verbActions) {
            if (va.isVerb(verb)) {
                verbAction = va;
                break;
            }
        }

        return verbAction;
    }

    public Set<VerbAction> getVerbActions() {
        return verbActions;
    }
    public String getClasse() {
        return classe;
    }

}

