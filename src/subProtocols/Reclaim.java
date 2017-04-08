package subProtocols;

import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;

import channel.Channel;
import chunks.ChunkId;
import chunks.ChunkInfo;
import header.Type;
import server.Peer;

public class Reclaim extends SubProtocol implements Runnable {
	private String chunk_location = "storage";
	private int space;
	
	public Reclaim(int space){
		super("");
		this.space = space;
	}
	
	public void run(){
		int space_removed = 0;
		Iterator<Entry<ChunkId, ChunkInfo>> it = Peer.getReplies().entrySet().iterator();
		boolean remove_all = false;
		
		do {
			Entry<ChunkId, ChunkInfo> chunk = it.next();
			
			if (remove_all || chunk.getValue().replDegree > chunk.getValue().confirmations) {
				File chunk_file = new File(chunk_location + "/" + chunk.getKey().getFileId() + "/" + chunk.getKey().getChunkNo());

				// get file size & add to space_removed
				space_removed += chunk_file.length();
				
				// send removed
				String header = Channel.createHeader(Type.removed, fileId, chunk.getKey().getChunkNo(), -1);
				Peer.sendToChannel(header.getBytes(), Peer.mcChannel);
				
				// remove chunk file from file system
				try {
					chunk_file.delete();
				}
				catch (Exception e) {}
				
				// remove chunk info from hash map
				it.remove();
			}
				
			// Return iterator to beginning when it reaches the end
			if (!it.hasNext()) {
				// Removes everything regardless of rd if space not reached;
				it = Peer.getReplies().entrySet().iterator();
				remove_all = true;
			}
		} while (space_removed < space && Peer.getReplies().size() > 0);
		// Remove while space removed < space to reclaim or there's nothing else to remove
	}

}
