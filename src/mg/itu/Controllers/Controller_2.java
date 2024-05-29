package mg.itu.Controllers;

import mg.itu.annotation.Annotation_controller;
import mg.itu.annotation.Get;
import mg.itu.beans.Employer;

@Annotation_controller
public class Controller_2 {
    
    @Get("/liste_employer")
    public String liste_emp () {
        Employer employer = new Employer("RAKOTO");
        return employer.getNom();
    }

}
