package br.unb.tr2.harmonic.server;

import br.unb.tr2.harmonic.entity.Server;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class ServerHandler implements Runnable{

    private Server runningServer;

    private Server remoteServer;

    public ServerHandler(Server runningServer, Server otherServer) {
        this.runningServer = runningServer;
        this.remoteServer = otherServer;
    }

    @Override
    public void run() {


    }
}
