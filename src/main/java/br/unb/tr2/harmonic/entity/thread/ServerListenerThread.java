package br.unb.tr2.harmonic.entity.thread;

import br.unb.tr2.harmonic.entity.Server;
import br.unb.tr2.harmonic.exceptions.ConnectionFailedException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class ServerListenerThread implements Runnable {

    Server server;

    ObjectInputStream ois;

    Logger logger = Logger.getLogger("ServerListener");

    public ServerListenerThread(Server server, ObjectInputStream ois) {
        this.server = server;
        this.ois = ois;
    }

    @Override
    public void run() {
        try {
            while (true) {
                server.receivedServerMessage(ois.readObject());
            }
        } catch (IOException e) {
            logger.severe("IOException: " + e.getMessage());
            if (server.getSocket() != null) try {
                server.getSocket().close();
            } catch (IOException e1) {
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
