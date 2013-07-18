package br.unb.tr2.harmonic.httpServer;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class HttpRequestHandler implements Runnable {

    private BufferedWriter writer = null;

    private BufferedReader reader = null;

    Socket socket;

    Logger logger = Logger.getLogger("HttpRequestHandler");

    public HttpRequestHandler(Socket socket) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            String request = reader.readLine();
            if (request.startsWith("GET / ") || request.startsWith("GET /index")) {
                serveView("index");
            } else if (request.startsWith("GET /admin")) {
                serveView("admin");
            } else if (request.startsWith("GET /user")) {
                serveView("user");
            } else {
                serve404();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serve404() throws IOException {
        writer.write("HTTP/1.1 404 Not Found\n" +
                "Content-Type: text/html; charset=UTF-8\n\n");
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(InputStream.class.getResourceAsStream("/html/404.html")));
        String line = fileReader.readLine();
        do {
            writer.write(line);
            line = fileReader.readLine();
        } while (line != null);
        writer.flush();
    }

    private void serveView(String view) throws IOException {
        writer.write("HTTP/1.1 200 OK\n" +
                "status: 200 OK\n" +
                "version: HTTP/1.1\n" +
                "content-type: text/html; charset=UTF-8\n\n");
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(InputStream.class.getResourceAsStream("/html/" + view + ".html")));
        String line = fileReader.readLine();
        do {
            writer.write(line);
            line = fileReader.readLine();
        } while (line != null);
        writer.flush();
    }
}
