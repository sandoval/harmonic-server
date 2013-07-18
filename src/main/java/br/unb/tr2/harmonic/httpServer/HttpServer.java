package br.unb.tr2.harmonic.httpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class HttpServer implements Runnable {

    private static HttpServer instance;

    private Map<String,User> users = Collections.synchronizedMap(new HashMap<String, User>());

    private HttpServer() {
        users.put("admin", new User("admin", "admin", Role.ADMIN));
        users.put("user", new User("user", "user", Role.USER));
    }

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
                new Thread(new HttpRequestHandler(serverSocket.accept(), this)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public Map<String, User> getUsers() {
        return users;
    }

    public static HttpServer getInstance() {
        if (instance == null)
            instance = new HttpServer();
        return instance;
    }
}
