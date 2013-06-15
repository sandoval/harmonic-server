package br.unb.tr2.zeroconf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class AnnouncementThread implements Runnable {

    private ServiceAnnouncement serviceAnnouncement;

    private DiscoveryService discoveryService;

    private DatagramSocket socket;

    private InetAddress destination;

    private Logger logger = Logger.getLogger("AnnoucementThread");

    public AnnouncementThread(DiscoveryService discoveryService, ServiceAnnouncement serviceAnnouncement) {
        this.discoveryService = discoveryService;
        this.serviceAnnouncement = serviceAnnouncement;
        this.destination = null;
    }

    public AnnouncementThread(DiscoveryService discoveryService, ServiceAnnouncement serviceAnnouncement, InetAddress destination) {
        this.discoveryService = discoveryService;
        this.serviceAnnouncement = serviceAnnouncement;
        this.destination = destination;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);

            if(this.destination == null) {
                Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                    if (networkInterface.isLoopback() || !networkInterface.isUp())
                        continue;
                    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                        ServiceAnnouncement announcement = new ServiceAnnouncement(this.serviceAnnouncement);
                        announcement.setAddress(interfaceAddress.getAddress());
                        InetAddress broadcastAddress = interfaceAddress.getBroadcast();
                        if (broadcastAddress != null)
                            sendAnnouncement(announcement, broadcastAddress);
                    }
                }
            } else {
                ServiceAnnouncement announcement = new ServiceAnnouncement(this.serviceAnnouncement);
                sendAnnouncement(announcement, this.destination);
            }


        } catch (SocketException e) {
            logger.log(Level.SEVERE, "Failed to open broadcast socket: " + e.getMessage());
        }
    }

    private void sendAnnouncement(ServiceAnnouncement announcement, InetAddress destination) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(announcement);
            objectStream.flush();
            byte[] data = byteStream.toByteArray();

            discoveryService.notifySentServiceAnnouncement(announcement);
            DatagramPacket packet = new DatagramPacket(data, data.length, destination, 44444);
            socket.send(packet);
        } catch (Exception e) {
            logger.severe("Couldn't send broadcast packet: " + e.getMessage());
        }
    }
}
