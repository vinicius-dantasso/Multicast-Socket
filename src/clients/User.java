package clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class User {
    private static final String MULTICAST_ADDRESS = "230.0.0.0";
    private static final int MULTICAST_PORT = 6789;
    private static final String INTERFACE_NAME = "wlp2s0";

    public static void main(String[] args) {
        Runnable userTask = () -> {
            MulticastSocket ms = null;
            try {
                ms = new MulticastSocket(MULTICAST_PORT);
                InetAddress multicastIp = InetAddress.getByName(MULTICAST_ADDRESS);
                InetSocketAddress group = new InetSocketAddress(multicastIp, MULTICAST_PORT);
                NetworkInterface networkInterface = NetworkInterface.getByName(INTERFACE_NAME);
                ms.joinGroup(group, networkInterface);

                System.out.println("Usuário conectado ao grupo multicast");

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                
                for(int i=0; i<60; i++) {
                    ms.receive(packet);
                    String datas = new String(
                        packet.getData(),
                        0,
                        packet.getLength()
                    );
                    System.out.println("Usuário recebeu: " + datas);
                }

                ms.leaveGroup(group, networkInterface);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            finally {
                if(ms != null) {
                    ms.close();
                }
            }
        };

        Thread userThread = new Thread(userTask);
        userThread.start();
    }
}
