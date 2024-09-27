package drone;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OpenDrone {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static DatagramSocket ds = null;

    public static void main(String[] args) {
        Runnable droneTask = new Runnable() {
            Random random = new Random();

            @Override
            public void run() {
                try {
                    ds = new DatagramSocket();

                    String datas = generateData(random);
                    byte[] buffer = datas.getBytes();

                    InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
                    DatagramPacket packet = new DatagramPacket(
                        buffer,
                        buffer.length,
                        serverAddress,
                        SERVER_PORT
                    );

                    ds.send(packet);
                    System.out.println("Drone enviou: " + datas);
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private String generateData(Random random) {
                int pressure = random.nextInt(40) + 960;
                int radiation = random.nextInt(800) + 200;
                int temperature = random.nextInt(35);
                int humitidy = random.nextInt(100);
        
                return "Pressao: " + pressure + " hPa, " +
                        "Radiacao: " + radiation + " W/m², " +
                        "Temperatura: " + temperature + " °C, " +
                        "Umidade: " + humitidy + " %";
            }
        };

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(droneTask, 0, 3, TimeUnit.SECONDS);

        Runnable stopTask = () -> {
            scheduler.shutdown();
            if(ds != null) {
                ds.close();
            }
        };

        scheduler.schedule(stopTask, 180, TimeUnit.SECONDS);

        Thread droneThread = new Thread(droneTask);
        droneThread.start();
    }
}
