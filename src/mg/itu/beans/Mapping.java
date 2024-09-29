package mg.itu.beans;

import java.util.ArrayList;

public class Mapping {
    
    protected String class_name;
    protected String methode_name;
    protected ArrayList <Parametre> params;
    boolean restAPI = false;

    public Mapping (String classs, String method, ArrayList <Parametre> parametres) {
        this.class_name = classs;
        this.methode_name = method;
        this.params = parametres;
    }

    @Override
    public String toString() {
        return "class:["+this.class_name+"] methode:["+this.methode_name+"]";
    }

    public String getClass_name() {
        return class_name;
    }
    public String getMethode_name() {
        return methode_name;
    }
    public boolean getRestAPI () { return this.restAPI; }
    public void setRestAPI(boolean restAPI) { this.restAPI = restAPI;}
    public ArrayList <Parametre> getParams() { return params; }




}

