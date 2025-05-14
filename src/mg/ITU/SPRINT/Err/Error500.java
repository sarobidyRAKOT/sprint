package mg.ITU.SPRINT.Err;

public class Error500 extends Exception {
    
    String mes = null;
    public Error500 (String message) {
        super(message);
    }
    public Error500 () {
        super();
    }

}
