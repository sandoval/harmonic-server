package br.unb.tr2.harmonic.server;

import br.unb.tr2.harmonic.entity.Server;
import br.unb.tr2.zeroconf.DiscoveryListener;
import br.unb.tr2.zeroconf.DiscoveryService;
import br.unb.tr2.zeroconf.ServiceAnnouncement;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class HarmonicServer implements DiscoveryListener {

    private static HarmonicServer instance;

    private DiscoveryService discoveryService;

    private ServerSocket serverSocket;

    private Logger logger = Logger.getLogger("main");

    private InetAddress address;

    public static void main(String[] args) throws IOException {
        HarmonicServer.getInstance();
    }

    public static HarmonicServer getInstance() {
        if (instance == null)
            instance = new HarmonicServer();
        return instance;
    }

    private HarmonicServer() {
        chooseNetworkAddress();

        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't open a socket for server", e);
        }

        discoveryService = DiscoveryService.getInstance();
        discoveryService.addListener(this);

        while (true) {
            try {
                new Thread(new ClientHandler(serverSocket.accept())).start();
            } catch (IOException e) {
                e.printStackTrace();
                if (!serverSocket.isBound()) {
                    try {
                        serverSocket = new ServerSocket(0);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    private void chooseNetworkAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface n = interfaces.nextElement();
                if (!n.isVirtual() && !n.isLoopback() && n.isUp()) {
                    Enumeration<InetAddress> addresses = n.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if(Inet4Address.class.isInstance(address)) {
                            logger.info("Using interface and address: [" + n.getDisplayName() + "] " + address.getHostAddress());
                            this.address = address;
                            return;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Couldn't choose network interface and address.", e);
        }
    }

    @Override
    public void DSHasReceivedAnnouncement(ServiceAnnouncement serviceAnnouncement) {
        if ("Harmonic Series Calculation Client._tcp.local".equals(serviceAnnouncement.getService())) {
            try {
                ServiceAnnouncement response = new ServiceAnnouncement("Harmonic Series Calculation Server._tcp.local", (long)serverSocket.getLocalPort(), InetAddress.getLocalHost());
                response.getParameters().put("server", new Server(address, (long) serverSocket.getLocalPort()));
                response.getParameters().put("clientInstances", 0);
                Socket socket = new Socket(serviceAnnouncement.getAddress(), serviceAnnouncement.getPort().intValue());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(response);
                oos.flush();
                oos.close(); // Will cause socket to close.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
