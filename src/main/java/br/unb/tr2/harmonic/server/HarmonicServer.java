package br.unb.tr2.harmonic.server;

import br.unb.tr2.zeroconf.DiscoveryListener;
import br.unb.tr2.zeroconf.DiscoveryService;
import br.unb.tr2.zeroconf.ServiceAnnouncement;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class HarmonicServer implements DiscoveryListener {

    private Logger logger = Logger.getLogger("main");

    public static void main(String[] args) throws IOException {

        DiscoveryService discoveryService = DiscoveryService.getInstance();
        discoveryService.addListener(new HarmonicServer());

        discoveryService.broadcastServiceAnnouncement(new ServiceAnnouncement("Harmonic Series Calculation Server._tcp.local", 44445l, null));

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void DSHasReceivedAnnouncement(ServiceAnnouncement serviceAnnouncement) {
        DiscoveryService discoveryService = DiscoveryService.getInstance();
        try {
            discoveryService.sendServiceAnnouncement(new ServiceAnnouncement("Harmonic Series Calculation Server._tcp.local", 44445l, InetAddress.getLocalHost()), serviceAnnouncement.getAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        logger.info(serviceAnnouncement.getService());
    }
}
