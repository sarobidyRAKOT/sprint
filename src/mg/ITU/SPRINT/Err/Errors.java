package mg.ITU.SPRINT.Err;

public class Errors extends Exception {

    String message = "";

    //  constructeur ___
    public Errors (String mess) { this.message = mess; }
    public Errors () {}
    // fin constructeur ___

    public void ajout_message (String message) {
        /**
         * ajouter dd'autre message d'erreur
         */
        this.message += message;
    }
    
    
    public void setMessage(String message) { this.message = message; }
    @Override
    public String getMessage() {
        // get mess 
        return this.message;
    }
}
