package br.unb.tr2.harmonic.server;

import br.unb.tr2.harmonic.entity.Server;
import br.unb.tr2.harmonic.exceptions.ConnectionFailedException;
import br.unb.tr2.harmonic.exceptions.ExistingServerException;

import java.util.logging.Logger;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class ServerHandler implements Runnable {

    private Server runningServer;

    private Server remoteServer;

    private ServerManager serverManager;

    private Logger logger = Logger.getLogger("ServerHandler");

    private Thread runningThread = null;

    public ServerHandler(Server runningServer, Server otherServer) {
        this.runningServer = runningServer;
        this.remoteServer = otherServer;
        this.remoteServer.setUsingInstanceIdentification(runningServer);
        this.serverManager = ServerManager.getInstance();
        try {
            this.serverManager.addHandler(this);
        } catch (ExistingServerException e) {
            logger.severe("Tried to connect to already connected server: " + remoteServer.getAddress().getHostAddress());
            remoteServer = null;
        }
    }

    public Server getRemoteServer() {
        return remoteServer;
    }

    @Override
    public void run() {
        runningThread = Thread.currentThread();
        if (remoteServer == null)
            return;

        try {
            remoteServer.connect(runningServer);
            remoteServer.receiveRequests();
        } catch (ConnectionFailedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerHandler that = (ServerHandler) o;

        if (remoteServer != null ? !remoteServer.equals(that.remoteServer) : that.remoteServer != null) return false;
        if (runningServer != null ? !runningServer.equals(that.runningServer) : that.runningServer != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = runningServer != null ? runningServer.hashCode() : 0;
        result = 31 * result + (remoteServer != null ? remoteServer.hashCode() : 0);
        return result;
    }

    public boolean pingServer() throws ConnectionFailedException {
        runningThread.interrupt();
        runningThread = null;
        boolean ping = false;
        try {
            ping = remoteServer.pingServer();
        } finally {
            new Thread(this).start();
        }
        return ping;
    }

    public boolean isConnected() {
        return remoteServer.isConnected();
    }
}
