package mg.itu.beans;

import java.lang.reflect.Parameter;

public class Parametre {
    private String nom;
    private Parameter parameter;

    public Parametre (String nom, Parameter parameter) {
        this.nom = nom;
        this.parameter = parameter;
    }

    public Parameter getParameter() { return parameter; }
    public String getNom() { return nom; }
}