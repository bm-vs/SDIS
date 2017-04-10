package chunks;

import java.util.HashSet;

public class ChunkInfo {

    private int replDegree;
    private int confirmations;
    private HashSet<String> senders;

    public ChunkInfo(int replDegree){
        this.replDegree = replDegree;
        confirmations = 0;
        senders = new HashSet<>();
    }

    public ChunkInfo(int replDegree, int confirmations){
        this.replDegree = replDegree;
        this.confirmations = confirmations;
        senders = new HashSet<>();
    }

    public synchronized void addConfirmation(){
        confirmations++;
    }

    public synchronized void removeConfirmation(){
        confirmations--;
    }

    public synchronized void addSender(String senderId){
        senders.add(senderId);
    }

    public synchronized boolean alreadySent(String senderId){
        return senders.contains(senderId);
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
