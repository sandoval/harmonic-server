package br.unb.tr2.harmonic.httpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class HttpServer implements Runnable {

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                new Thread(new HttpRequestHandler(serverSocket.accept())).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
