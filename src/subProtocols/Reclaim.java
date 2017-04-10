package subProtocols;

import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;

import channel.Channel;
import chunks.ChunkId;
import chunks.ChunkInfo;
import client.Service;
import file.Disk;
import header.Type;
import server.Peer;
import utils.Utils;

public class Reclaim extends SubProtocol implements Runnable {
	private int spaceToRemove;
	
	public Reclaim(int space){
		super("");
		this.spaceToRemove = space;
	}
	
	public void run(){
		Iterator<Entry<ChunkId, ChunkInfo>> it = Peer.getReplies().entrySet().iterator();
		boolean remove_all = false;
		int space_removed = 0;

        // Remove while space removed < space to reclaim or there's nothing else to remove
        while (space_removed < spaceToRemove && Peer.getReplies().size() > 0) {
			Entry<ChunkId, ChunkInfo> chunk = it.next();
			
			if (remove_all || chunk.getValue().getReplDegree() > chunk.getValue().getConfirmations()) {
			    space_removed += remove(chunk);
				// remove chunk info from hash map
				it.remove();
			}
				
			// Return iterator to beginning when it reaches the end
			if (!it.hasNext()) {
				// Removes everything regardless of rd if space not reached;
				it = Peer.getReplies().entrySet().iterator();
				remove_all = true;
			}
		}
		Peer.deleteProtocol(Service.reclaim, String.valueOf(spaceToRemove));
	}

	private int remove(Entry<ChunkId, ChunkInfo> chunk ){
        File chunk_file = new File(Utils.storage + "/" + chunk.getKey().getFileId() + "/" + chunk.getKey().getChunkNo());
        int size = (int)chunk_file.length();
        // send removed
        String header = Channel.createHeader(Type.removed, chunk.getKey().getFileId(), chunk.getKey().getChunkNo(), -1);
        Peer.sendToChannel(header.getBytes(), Peer.mcChannel);

        System.out.println("Removed chunk: " + chunk.getKey().getFileId() + " numChunk: " + chunk.getKey().getChunkNo());
        try {
            chunk_file.delete();
            Disk.free(size);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

}
