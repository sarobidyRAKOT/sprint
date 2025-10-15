package mg.ITU.SPRINT.beans;

public class Preced_path {
    
    private String uri;
    private Verb verb;

    public Preced_path (String uri, Verb verb) {
        this.uri = uri;
        this.verb = verb;

        // System.out.println(this.verb+" "+this.uri);
    }

    public String getUri() { return uri; }
    public Verb getVerb() { return verb; }

}
