package services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ReceiverServer {
    private String serverName;
    private int serverPort;
    private static final String MULTICAST_ADDRESS = "230.0.0.0";
    private static final int MULTICAST_PORT = 6789;
    
    public ReceiverServer(String name, int port) {
        this.serverName = name;
        this.serverPort = port;
    }

    public void start() {
        DatagramSocket ds = null;
        MulticastSocket ms = null;

        try {
            ds = new DatagramSocket(serverPort);
            ms = new MulticastSocket();

            byte[] buffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            System.out.println(serverName + " esperando dados do drone...");

            boolean flag = true;
            while (flag) {
                ds.receive(receivePacket);
                String data = new String(
                    receivePacket.getData(),
                    0,
                    receivePacket.getLength()
                );
                System.out.println(serverName + " recebeu: " + data);

                DatagramPacket multicastPacket = new DatagramPacket(
                    data.getBytes(),
                    data.length(),
                    InetAddress.getByName(MULTICAST_ADDRESS),
                    MULTICAST_PORT
                );

                ms.send(multicastPacket);
                System.out.println(serverName + " enviou dados para o grupo multicast");
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            ds.close();
            ms.close();
        }
    }

    public static void main(String[] args) {
        // Server1, 12345
        // Server2, 12346
        ReceiverServer server = new ReceiverServer("Servidor 2", 12346);

        new Thread(server::start).start();
    }
}
