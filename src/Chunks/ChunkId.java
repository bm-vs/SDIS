package Chunks;


public class ChunkId {

    private final String fileId;
    private final int chunkNo;

    public ChunkId(String fileId, int chunkNo){
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        ChunkId c = (ChunkId) obj;
        return this.fileId.equals(c.fileId) && this.chunkNo == c.chunkNo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + 32;
        result = prime * result + chunkNo;
        return result;
    }
}
