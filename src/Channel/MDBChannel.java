package Channel;

import java.net.DatagramPacket;

public class MDBChannel extends Channel{

    public MDBChannel(int port, String address){
        super(port, address);
    }

    public void handle(DatagramPacket packet){

    }
}
