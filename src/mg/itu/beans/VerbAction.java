package mg.itu.beans;

import java.util.ArrayList;

import mg.itu.Err.Errors;

/**
 * VerbAction
 */
public class VerbAction {

    String uri; // 
    ArrayList <Parametre> parametres;
    boolean restAPI = false;


    String methode;
    String verb; // post na get
    

    public boolean isVerb_UriExist (String uri, String verb) {
        
        // VERIFIER L'EXISTANCE DU VERBACTION 
        if (this.getUri().equals(uri) && this.getVerb().equals(verb)) {
            return true;
        } else {
            return false;
        }
    }
    

    public void setRestAPI(boolean restAPI) {
        this.restAPI = restAPI;
    }
    public boolean isRestAPI() { return restAPI; }
    public String getUri() {
        return uri;
    }
    public void setVerb(String verb) throws Errors {
        if (verb.equals("get") || verb.equals("post")) {
            this.verb = verb;
        } else throw new Errors ("VERB MAL DEFINI [correct]: 'get' ou 'post'");
    }
    public String getVerb() {
        return verb;
    }


}