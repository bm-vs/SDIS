package Chunks;


public class ChunkInfo {

    public int replDegree;
    public int confirmations;

    public ChunkInfo(int replDegree){
        this.replDegree = replDegree;
        confirmations = 0;
    }

    public ChunkInfo(int replDegree, int confirmations){
        this.replDegree = replDegree;
        this.confirmations = confirmations;
    }
}
