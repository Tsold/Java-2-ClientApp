package hr.algebra.threads;

import hr.algebra.model.Vehicle;
import hr.algebra.utils.ByteUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendThread extends Thread{

    private static final String PROPERTIES_FILE = "socket.properties";
    private static final String CLIENT_PORT = "CLIENT_PORT";
    private static final String GROUP = "GROUP";
    private static final Properties PROPERTIES = new Properties();

    static {
        try {

            PROPERTIES.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException ex) {
            Logger.getLogger(SendThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    private final LinkedBlockingDeque<Vehicle> vehicles = new LinkedBlockingDeque<>();

    public void trigger(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    @Override
    public void run() {
        try (DatagramSocket server = new DatagramSocket()) {
            while (true) {

                if (!vehicles.isEmpty()) {

                    byte[] suspectBytes;
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                         ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                        oos.writeObject(vehicles.getFirst());
                        vehicles.clear();
                        oos.flush();
                        suspectBytes = baos.toByteArray();
                    }

                    byte[] numberOfSuspectBytes = ByteUtils.intToByteArray(suspectBytes.length);
                    InetAddress groupAddress = InetAddress.getByName(PROPERTIES.getProperty(GROUP));
                    DatagramPacket packet = new DatagramPacket(
                            numberOfSuspectBytes,
                            numberOfSuspectBytes.length,
                            groupAddress, Integer.valueOf(PROPERTIES.getProperty(CLIENT_PORT))
                    );
                    server.send(packet);

                    packet = new DatagramPacket(
                            suspectBytes,
                            suspectBytes.length,
                            groupAddress, Integer.valueOf(PROPERTIES.getProperty(CLIENT_PORT)));
                    server.send(packet);
                }
            }
        } catch (SocketException e) {
            Logger.getLogger(SendThread.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException e) {
            Logger.getLogger(SendThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }


}
