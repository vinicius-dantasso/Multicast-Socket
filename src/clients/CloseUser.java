package clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Set;

public class CloseUser {
    private static final String MULTICAST_ADDRESS = "230.0.0.0";
    private static final int MULTICAST_PORT = 6789;
    private static final String INTERFACE_NAME = "wlp2s0";
    private static final Set<String> AUTHORIZED_USERS = Set.of("230.0.1.1", "230.0.1.2");
    private String serverName;
    private String userIp;

    public CloseUser(String name, String ip) {
        this.serverName = name;
        this.userIp = ip;
    }

    public void start() {
        Runnable userTask = () -> {
            MulticastSocket ms = null;
            try {
                if(!AUTHORIZED_USERS.contains(userIp)) {
                    System.out.println("Acesso negado para o usuário: " + userIp);
                    return;
                }

                ms = new MulticastSocket(MULTICAST_PORT);
                InetAddress multicastIp = InetAddress.getByName(MULTICAST_ADDRESS);
                InetSocketAddress group = new InetSocketAddress(multicastIp, MULTICAST_PORT);
                NetworkInterface networkInterface = NetworkInterface.getByName(INTERFACE_NAME);
                ms.joinGroup(group, networkInterface);

                System.out.println("Usuário conectado ao " + serverName + " e ao grupo multicast");

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                boolean flag = true;
                while (flag) {
                    ms.receive(packet);
                    String data = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Usuário (" + serverName + ") recebeu: " + data);
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            finally {
                ms.close();
            }
        };

        Thread userThread = new Thread(userTask);
        userThread.start();
    }

    public static void main(String[] args) {
        // Server 3, 230.0.1.1
        // Server 4, 230.0.1.2
        CloseUser user = new CloseUser("Servidor 4", "230.0.1.2");
        user.start();
    }
}
