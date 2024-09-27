package services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class EmitterServer {
    private String serverName;
    private static final String MULTICAST_ADDRESS = "230.0.0.0";
    private static final int MULTICAST_PORT = 6789;
    private static final String INTERFACE_NAME = "wlp2s0";

    public EmitterServer(String name) {
        this.serverName = name;
    }

    public void start() {
        MulticastSocket ms = null;

        try {
            ms = new MulticastSocket(MULTICAST_PORT);
            InetAddress multicastIp = InetAddress.getByName(MULTICAST_ADDRESS);
            InetSocketAddress group = new InetSocketAddress(multicastIp, MULTICAST_PORT);
            NetworkInterface networkInterface = NetworkInterface.getByName(INTERFACE_NAME);
            ms.joinGroup(group, networkInterface);
            
            System.out.println(serverName + " conectado ao grupo multicast");

            byte[] buffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

            boolean flag = true;
            while(flag) {
                ms.receive(receivePacket);
                String data = new String(
                    receivePacket.getData(),
                    0,
                    receivePacket.getLength()
                );
                System.out.println(serverName + " recebeu: " + data);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            ms.close();
        }
    }

    public static void main(String[] args) {
        // Server 3
        // Server 4
        EmitterServer server = new EmitterServer("Servidor 4");

        new Thread(server::start).start();
    }
}
