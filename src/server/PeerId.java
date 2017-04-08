package server;


public class PeerId {
    public int id;
    public String version;
    public PeerId(int id, String version){
        this.id = id;
        this.version = version;
    }

    public String toString(){
        return version + " " + id;
    }
}
