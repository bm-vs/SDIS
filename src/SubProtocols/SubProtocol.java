package SubProtocols;

import Server.PeerId;

public class SubProtocol {

    protected PeerId peer;
    protected int fileId;

    public SubProtocol(PeerId peer, int fileId){
        this.peer = peer;
        this.fileId = fileId;
    }

    public String getCommonHeader(){
        return peer.version + " " + peer.id + " " + fileId;
    }
}
