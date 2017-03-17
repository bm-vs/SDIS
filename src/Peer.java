import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by william on 15-03-2017.
 */
public class Peer {

    public MulticastSocket socket;
    DatagramPacket packet;

    public InetAddress address;

    public int port;

    byte[] buf = new byte[256];

    public void main(String[] args) {

        if(args.length != 6){
            printUsage(args);
            return;
        } else{
            init(args);
        }

        do {


            try {


                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                //analyze

            } catch (IOException err) {
                err.printStackTrace();
            }
        } while(true);

        try{
            socket.leaveGroup(address);

        }catch (IOException err) {
            err.printStackTrace();
        }


    }

    public void init(String[] args){
        try{
            address = InetAddress.getByName(args[1]);
            port = Integer.parseInt(args[2]);
            socket = new MulticastSocket(port);
            socket.joinGroup(address);

        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void printUsage(String[] args) {
        System.out.println("Wrong number of arguments");
        System.out.println(args[0] + " mcAddress mcPort mdbAddress mdbPort mdrAddress mdrPort");
    }
}
