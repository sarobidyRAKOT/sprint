package mg.ITU.beans;

import java.util.HashMap;

public class ModelView {
    
    private String url;
    private HashMap <String, Object> data;

    // constructeur ___
    public ModelView () {
        data = new HashMap <String, Object> ();
    }
    public ModelView (String url) {
        this();
        this.url = url;
    }

    public void setUrl(String url) { this.url = url; }
    
    public void add_object (String name, Object object) {
        this.data.put(name, object);
    }

    public String getUrl() {     return url; }
    public HashMap<String, Object> getData() {
        return data;
    }

}
