package mg.ITU.SPRINT.beans;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import mg.ITU.SPRINT.Err.Error500;

public class MultipartFile {

    private String nom_fichier;
    private String contentType;
    private long size;
    private byte[] bytes;
    
    private String UPLOAD_DIR = "/images/uploads";
    private HttpServletRequest request;
    private Part file_Part;

    public MultipartFile (HttpServletRequest request, String file_name) {

        this.request = request;
        try {
            // if (!file_name.isEmpty()) {
            this.file_Part = request.getPart(file_name);
            // }
                
            if (this.file_Part != null) {
                
                // System.out.println("TONGA "+file_Part);
                this.nom_fichier = this.file_Part.getSubmittedFileName();
                this.contentType = this.file_Part.getContentType();
                this.size = this.file_Part.getSize();
                
                try (InputStream inputStream = this.file_Part.getInputStream()) {
                    this.bytes = inputStream.readAllBytes();
                }

            }

        } catch (IOException | ServletException e) {
            e.printStackTrace();

        }
    }

    public boolean upload_toServeur () throws Error500 {

        File uploadDir = new File(request.getServletContext().getRealPath("") + UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File file = new File(uploadDir, nom_fichier);
        if (this.file_Part != null) {
            try {
                this.file_Part.write(file.getAbsolutePath());
                // System.out.println("Fichier sauvegarder dans : " + file.getAbsolutePath());
                return true;
            } catch (IOException e) {
                throw new Error500(e.getMessage()+"sauvegarde NON VALIDE");
                // System.out.println("sauvegarde NON VALIDE");
            }
        } else {
            throw new Error500("Le fichier est VIDE");
        }
    }

    public void setNom_fichier(String nom_fichier) {
        if (nom_fichier != null) {
            this.nom_fichier = nom_fichier;
        }
    }

    public void setUPLOAD_DIR(String uPLOAD_DIR) {
        if (uPLOAD_DIR != null) {
            this.UPLOAD_DIR = uPLOAD_DIR;
        }
    }


    public String getNomFichier() { return nom_fichier; }
    public String getContentType() { return contentType; }
    public long getSize() { return size; }
    public byte[] getBytes() { return bytes; }

    
    @Override
    public String toString() {
        String b = "0";
        if (this.bytes != null && bytes.length > 0) {
          b = Arrays.toString(Arrays.copyOf(bytes, 10));  
        } 
        return "MultipartFile {" +
        "nom_fichier='" + nom_fichier + '\'' +
        ", contentType='" + contentType + '\'' +
        ", size=" + size +
        ", bytes=" + b + "..." +
        '}';
    }
}