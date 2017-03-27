package Chunks;


public class ChunkId {

    private final int fileId;
    private final int chunkNo;

    public ChunkId(int fileId, int chunkNo){
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
        return this.fileId == c.fileId && this.chunkNo == c.chunkNo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + fileId;
        result = prime * result + chunkNo;
        return result;
    }
}
