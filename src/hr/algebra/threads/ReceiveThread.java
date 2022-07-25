package hr.algebra.threads;

import hr.algebra.controllers.MainDashboardController;
import hr.algebra.model.Vehicle;
import hr.algebra.utils.ByteUtils;
import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceiveThread extends Thread {


    private static final String PROPERTIES_FILE = "socket.properties";
    private static final String CLIENT_PORT = "CLIENT_PORT";
    private static final String GROUP = "GROUP";
    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException ex) {
            Logger.getLogger(ReceiveThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    private MainDashboardController dashController;



    public ReceiveThread(MainDashboardController controller) {
        this.dashController = controller;
    }


    @Override
    public void run() {

        try (MulticastSocket client = new MulticastSocket(Integer.valueOf(PROPERTIES.getProperty(CLIENT_PORT)))) {

            InetAddress groupAddress = InetAddress.getByName(PROPERTIES.getProperty(GROUP));

            client.joinGroup(groupAddress);

            while (true) {


                byte[] numberOfSuspectBytes = new byte[4];
                DatagramPacket packet = new DatagramPacket(numberOfSuspectBytes, numberOfSuspectBytes.length);
                client.receive(packet);
                int length = ByteUtils.byteArrayToInt(numberOfSuspectBytes);


                byte[] suspectBytes = new byte[length];
                packet = new DatagramPacket(suspectBytes, suspectBytes.length);
                client.receive(packet);
                try (ByteArrayInputStream bais = new ByteArrayInputStream(suspectBytes);
                     ObjectInputStream ois = new ObjectInputStream(bais)) {
                    Vehicle vehicle = (Vehicle) ois.readObject();

                    Platform.runLater(() -> {
                        dashController.addToList(vehicle);
                    });
                }
            }

        } catch (SocketException | UnknownHostException e) {
            Logger.getLogger(ReceiveThread.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException | ClassNotFoundException e) {
            Logger.getLogger(ReceiveThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }



}
