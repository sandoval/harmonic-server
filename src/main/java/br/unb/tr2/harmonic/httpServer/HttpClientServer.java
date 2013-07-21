package br.unb.tr2.harmonic.httpServer;

import br.unb.tr2.harmonic.client.HarmonicClient;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class HttpClientServer implements Runnable {

    private static HttpClientServer instance;

    private HttpClientServer() {
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
                Socket socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                reader.readLine();
                if (HarmonicClient.getInstance().getServer() != null) {
                    String url = "http://" + HarmonicClient.getInstance().getServer().getAddress().getHostAddress() + ":8080";
                    writer.write("HTTP/1.1 200 ok\n" +
                            "Refresh: 0; url=" + url + "\n" +
                            "Content-type: text/html\n\n");
                    writer.write("Please follow <a href=\"" + url + "\">this link</a>.");
                } else {
                    writer.write("HTTP/1.1 200 OK\n" +
                            "status: 200 OK\n" +
                            "version: HTTP/1.1\n" +
                            "content-type: text/html; charset=UTF-8\n\n");
                    writer.write("<html><body>Client not connected to any server.</body></html>");
                }
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static HttpClientServer getInstance() {
        if (instance == null)
            instance = new HttpClientServer();
        return instance;
    }
}