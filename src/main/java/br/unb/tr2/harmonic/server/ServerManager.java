package br.unb.tr2.harmonic.server;

import br.unb.tr2.harmonic.entity.Server;
import br.unb.tr2.harmonic.exceptions.ConnectionFailedException;
import br.unb.tr2.harmonic.exceptions.ExistingServerException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class ServerManager implements Runnable {

    private static ServerManager instance;

    volatile private Set<Server> servers = Collections.synchronizedSet(new HashSet<Server>());

    private Object instanceIdentification = null;

    private Logger logger = Logger.getLogger("ServerManager");

    private ServerManager() {
    }

    public static ServerManager getInstance() {
        if (instance == null)
            instance = new ServerManager();
        return instance;
    }

    public void initialize(Object instanceIdentification) {
        this.instanceIdentification = instanceIdentification;
        new Thread(this).start();
    }

    public Set<Server> connectedServers() {
        synchronized (servers) {
            return new HashSet<Server>(servers);
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (servers) {
                Iterator<Server> i = servers.iterator();
                while (i.hasNext()) {
                    Server server = i.next();
                    if (!server.isConnected()) {
                        servers.remove(server);
                        continue;
                    }

                    try {
                        server.syncDatabase();
                    } catch (ConnectionFailedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(5000 + (int)(Math.random()*2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void addServer(Server server) {
        if (!servers.add(server)) {
            logger.severe("Tried to add already connected server " + server.getUuid());
            return;
        }

        try {
            server.connect(instanceIdentification);
        } catch (ConnectionFailedException e) {
            logger.severe("Failed to connect do server " + server.getUuid() + ": " + e.getMessage());
            servers.remove(server);
        }

    }
}
