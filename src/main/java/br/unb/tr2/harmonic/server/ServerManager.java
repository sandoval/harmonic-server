package br.unb.tr2.harmonic.server;

import br.unb.tr2.harmonic.exceptions.ConnectionFailedException;
import br.unb.tr2.harmonic.exceptions.ExistingServerException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class ServerManager implements Runnable {

    private static ServerManager instance;

    private Set<ServerHandler> handlers = Collections.synchronizedSet(new HashSet<ServerHandler>());

    private ServerManager() {
        new Thread(this).start();
    }

    public static ServerManager getInstance() {
        if (instance == null)
            instance = new ServerManager();
        return instance;
    }

    public synchronized void addHandler(ServerHandler serverHandler) throws ExistingServerException {
        Iterator<ServerHandler> i = handlers.iterator();
        while (i.hasNext()) {
            ServerHandler handler = i.next();
            if (!handler.isConnected())
                handlers.remove(handler);
        }
        if (!handlers.add(serverHandler))
            throw new ExistingServerException();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000 + (int)(Math.random()*2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (handlers) {
            Iterator<ServerHandler> i = handlers.iterator();
            while (i.hasNext()) {
                ServerHandler handler = i.next();
                try {
                    if (handler.pingServer())
                        System.out.println("Successfully pinged server " + handler.getRemoteServer().getAddress().getHostAddress());
                } catch (ConnectionFailedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
