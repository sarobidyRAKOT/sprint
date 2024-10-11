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



    public void addVerbAction (VerbAction VA) {
        if (VA == null) {

        } else {

        }

    }

    public boolean check_verbActionBY (String uri, String verb) {

        boolean verbAction_efaAo = false; // tsy mbola ao ...
        for (VerbAction verbAction : verbActions) {
            verbAction_efaAo = verbAction.isVerb_UriExist(uri, verb);
            if (verbAction_efaAo) {
                break;
            }
        }
        return verbAction_efaAo;
    }
    


}

