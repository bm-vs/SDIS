package Chunks;


public class ChunkInfo {

    public Thread thread;
    public int replDegree;
    public int confirmations;

    public ChunkInfo(Thread thread, int replDegree){
        this.thread = thread;
        this.replDegree = replDegree;
        confirmations = 0;
    }
}
