package br.unb.tr2.zeroconf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class DiscoveryThread implements Runnable {

    DatagramSocket socket;

    DiscoveryService discoveryService;

    Logger logger = Logger.getLogger("DiscoveryThread");

    public DiscoveryThread(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(44444, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                byte[] buffer = new byte[10000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                ObjectInputStream objectStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
                ServiceAnnouncement serviceAnnouncement =  (ServiceAnnouncement)objectStream.readObject();
                discoveryService.notifyReceivedServiceAnnouncement(serviceAnnouncement);
            }

        } catch (UnknownHostException e) {
            logger.log(Level.SEVERE, "Could not get localhost address! (InetAddress.getLocalHost())");
        } catch (SocketException e) {
            logger.log(Level.SEVERE, "Failed to open discovery socket: " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException while discovering: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }
}
