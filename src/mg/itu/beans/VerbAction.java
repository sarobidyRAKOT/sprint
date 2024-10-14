package mg.itu.beans;


/**
 * VerbAction
 */
public class VerbAction {

    // boolean restAPI = false;
    String uri;
    Parametre[] parametres;
    boolean restAPI = false;

    String methode;
    Verb verb; // post na get
    
    public VerbAction () {

    }
    
    public VerbAction (String method, Verb verb, String uri, boolean restAPI) {
        this.setVerb(verb);
        this.setUri(uri);
        this.setRestAPI(restAPI);
        this.methode = method;
    }
    // public boolean isVerb_UriExist (String uri, String verb) {
        
    //     // VERIFIER L'EXISTANCE DU VERBACTION 
    //     if (this.getUri().equals(uri) && this.getVerb().equals(verb)) {
    //         return true;
    //     } else {
    //         return false;
    //     }
    // }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerbAction verbAction = (VerbAction) o;
        return verb.equals(verbAction.verb);
    }

    @Override
    public int hashCode() {
        return verb.hashCode();
    }
    
    public boolean isVerb (Verb v) {
        if (this.getVerb().equals(v)) {
            return true;
        } else return false;
    }

    // public void setRestAPI(boolean restAPI) {
    //     this.restAPI = restAPI;
    // }
    // public boolean isRestAPI() { return restAPI; }
    public void setParametres(Parametre[] parametres) {
        this.parametres = parametres;
    }
    public boolean isRestAPI () {
        return this.restAPI;
    }
    void setRestAPI (boolean rest) {
        this.restAPI = rest;
    }
    public Parametre[] getParametres() { return parametres; }
    public void setUri (String uri) { this.uri = uri; }
    public String getUri() { return uri; }
    public void setVerb(Verb verb) { this.verb = verb; }
    public Verb getVerb() { return verb; }
    public String getMethode() { return methode; }


}