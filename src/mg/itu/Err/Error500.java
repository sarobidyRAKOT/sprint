package mg.itu.Err;

public class Error500 extends Exception {
    
    String mes = null;
    public Error500 (String message) {
        super(message);
    }
    public Error500 () {
        super();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public void setMes (String message) {
        this.mes = message;
    }
    public String getMes() {
        return mes;
    }
}
