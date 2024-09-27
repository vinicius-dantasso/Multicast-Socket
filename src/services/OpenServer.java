package services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class OpenServer {
    private static final int SERVER_PORT = 12345;
    private static final String MULTICAST_ADDRESS = "230.0.0.0";
    private static final int MULTICAST_PORT = 6789;
    
    public static void main(String[] args) {
        Runnable serverTask = () -> {
            DatagramSocket ds = null;
            MulticastSocket ms = null;
            try {
                ds = new DatagramSocket(SERVER_PORT);
                InetAddress multicastGroup = InetAddress.getByName(MULTICAST_ADDRESS);

                byte[] buffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                
                System.out.println("Servidor esperando dados do drone...");

                boolean flag = true;
                while (flag) {
                    ds.receive(receivePacket);
                    String datas = new String(
                        receivePacket.getData(),
                        0,
                        receivePacket.getLength()
                    );
                    System.out.println("Servidor recebeu: " + datas);

                    // Enviando dados para o grupo multicast
                    ms = new MulticastSocket();
                    DatagramPacket multicastPacket = new DatagramPacket(
                        datas.getBytes(),
                        datas.length(),
                        multicastGroup,
                        MULTICAST_PORT
                    );

                    ms.send(multicastPacket);
                    System.out.println("Servidor enviou dados para o grupo multicast.");
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            finally {
                if(ds != null || ms != null) {
                    ds.close();
                    ms.close();
                }
            }
        };

        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }
}
