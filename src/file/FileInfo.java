package file;

public class FileInfo {

    public String fileId;
    public int totalChunks;

    public FileInfo(String fileId, int totalChunks){
        this.fileId = fileId;
        this.totalChunks = totalChunks;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        FileInfo f = (FileInfo) obj;
        return this.fileId.equals(f.fileId) && this.totalChunks == f.totalChunks;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + 32;
        result = prime * result + totalChunks;
        return result;
    }
}
