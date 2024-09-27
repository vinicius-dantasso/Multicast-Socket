package drone;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Drones {
    private static final String SERVER_ADDRESS_NORTH = "localhost";
    private static final String SERVER_ADDRESS_SOUTH = "localhost";
    private static final int SERVER_PORT_NORTH = 12345;
    private static final int SERVER_PORT_SOUTH = 12346;
    private static final Set<String> AUTHORIZED_DRONES = Set.of("230.0.0.1", "230.0.0.2");

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        Runnable droneTask = new Runnable() {
            Random random = new Random();

            @Override
            public void run() {
                DatagramSocket dsNorth = null;
                DatagramSocket dsSouth = null;

                try {
                    dsNorth = new DatagramSocket();
                    dsSouth = new DatagramSocket();
                    
                    String dataNorth = generateData(random, "North");
                    String dataSouth = generateData(random, "South");

                    sendData(dsNorth, dataNorth, "230.0.0.1", SERVER_ADDRESS_NORTH, SERVER_PORT_NORTH);
                    sendData(dsSouth, dataSouth, "230.0.0.2", SERVER_ADDRESS_SOUTH, SERVER_PORT_SOUTH);
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
                finally {
                    dsNorth.close();
                    dsSouth.close();
                }
            }

            private void sendData(DatagramSocket ds, String data, String senderIp, String address, int port) throws IOException {
                if(!AUTHORIZED_DRONES.contains(senderIp)) {
                    System.out.println("Acesso negado ao drone: " + senderIp);
                    return;
                }

                byte[] buffer = data.getBytes();
                InetAddress serverAddress = InetAddress.getByName(address);
                DatagramPacket packet = new DatagramPacket(
                    buffer,
                    buffer.length,
                    serverAddress,
                    port
                );
                ds.send(packet);
                System.out.println("Drone enviou: " + data);
            }

            private String generateData(Random random, String region) {
                int pressure = random.nextInt(40) + 960;
                int radiation = random.nextInt(800) + 200;
                int temperature = random.nextInt(35);
                int humitidy = random.nextInt(100);
        
                return region + " - Pressao: " + pressure + " hPa, " +
                        "Radiacao: " + radiation + " W/m², " +
                        "Temperatura: " + temperature + " °C, " +
                        "Umidade: " + humitidy + " %";
            }
        };

        scheduler.scheduleAtFixedRate(droneTask, 0, 10, TimeUnit.SECONDS);
    }
}
