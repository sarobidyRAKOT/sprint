package mg.itu.beans;

import java.util.ArrayList;

public class Param {
    
    String nom;
    String value;

    public Param (String name, String valeur) {
        this.value = valeur;
        this.nom = name;
    }

    public String getNom() {
        return nom;
    }
    public String getValue() {
        return value;
    }

    public static Param get_param (String nom, ArrayList <Param> list_params) {
        Param p = null;
        for (Param param : list_params) {
            if (param.nom == nom) {
                p = param;
                break;
            }
        }
        return p;
    }
}
