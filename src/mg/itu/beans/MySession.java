package mg.ITU.beans;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class MySession {

    private HttpSession session;

    public MySession (HttpServletRequest request) {
        this.session = request.getSession();
    }
    public MySession () {}


    public Object get (String key) {return this.session.getAttribute(key);}
    public void add (String key, Object obj) { this.session.setAttribute(key, obj); }
    public void delete (String key) { this.session.removeAttribute(key);}
    // public void delete () { this.session.invalidate(); }
}
