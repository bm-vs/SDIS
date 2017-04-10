package server;


public class PeerId {
    public int id;
    private String version;

    PeerId(int id, String version){
        this.id = id;
        this.version = version;
    }

    public String getVersion(){
        return version;
    }

    public String toString(){
        return version + " " + id;
    }
}
