package chunks;


public class ChunkInfo {

    private int replDegree;
    private int confirmations;

    public ChunkInfo(int replDegree){
        this.replDegree = replDegree;
        confirmations = 0;
    }

    public ChunkInfo(int replDegree, int confirmations){
        this.replDegree = replDegree;
        this.confirmations = confirmations;
    }

    public synchronized void addConfirmation(){
        confirmations++;
    }

    public synchronized void removeConfirmation(){
        confirmations--;
    }

    public int getReplDegree(){
        return replDegree;
    }

    public int getConfirmations(){
        return confirmations;
    }

    @Override
    public String toString(){
        return "Replications caught: " + confirmations + "\n";
    }
}
