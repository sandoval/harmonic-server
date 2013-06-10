package br.unb.tr2.zeroconf;

import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class DiscoveryService {

    Logger logger = Logger.getLogger("DiscoveryService");

    HashSet<ServiceAnnouncement> sentAnnouncements = new HashSet<ServiceAnnouncement>();

    private DiscoveryService() {
        new Thread(new DiscoveryThread(this)).start();
    }

    public void sendServiceAnnouncement(ServiceAnnouncement serviceAnnouncement) {
        new Thread(new AnnouncementThread(this, serviceAnnouncement)).start();
    }

    public void notifyReceivedServiceAnnouncement(ServiceAnnouncement serviceAnnouncement) {
        if (!sentAnnouncements.contains(serviceAnnouncement))
            logger.info("New service: " + serviceAnnouncement.getService() + " on address " + serviceAnnouncement.getAddress().getHostAddress() + ":" + serviceAnnouncement.getPort());
    }

    public void notifySentServiceAnnouncement(ServiceAnnouncement serviceAnnouncement) {
        sentAnnouncements.add(serviceAnnouncement);
    }

    private static DiscoveryService instance = null;

    public static DiscoveryService getInstance() {
        if (instance == null)
            instance = new DiscoveryService();
        return instance;
    }

}
