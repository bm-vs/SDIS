package chunks;


public class ChunkInfo {

    public int replDegree;
    public int confirmations;
    public int size;

    public ChunkInfo(int replDegree){
        this.replDegree = replDegree;
        confirmations = 0;
    }

    public ChunkInfo(int replDegree, int confirmations, int size){
        this.replDegree = replDegree;
        this.confirmations = confirmations;
        this.size = size;
    }

    @Override
    public String toString(){
        String state = "Size: " + size + "\n";
        state += "Replications caught: " + confirmations + "\n";
        return state;
    }
}
