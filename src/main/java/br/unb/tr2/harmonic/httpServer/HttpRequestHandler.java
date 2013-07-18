package br.unb.tr2.harmonic.httpServer;

import br.unb.tr2.harmonic.server.CalculationManager;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.Logger;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class HttpRequestHandler implements Runnable {

    private HttpServer httpServer;

    private BufferedWriter writer = null;

    private BufferedReader reader = null;

    private String request;

    private Map<String,String> urlParameters = null;

    private Socket socket;

    private Logger logger = Logger.getLogger("HttpRequestHandler");

    public HttpRequestHandler(Socket socket, HttpServer httpServer) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.httpServer = httpServer;
    }

    @Override
    public void run() {
        try {
            request = reader.readLine();
            parseUrlParams();
            if (request.startsWith("GET / ") || request.startsWith("GET /index")) {
                serveView("index");
            } else if (request.startsWith("GET /login")) {
                User user = retrieveUser(urlParameters.get("user"), urlParameters.get("password"));
                if (user == null) {
                    logger.info("Failed login attempt: " + urlParameters.get("user") + ":" + urlParameters.get("password"));
                    serve401();
                } else {
                    if (user.getRole() == Role.ADMIN)
                        redirect("/admin");
                    else if (user.getRole() == Role.USER)
                        redirect("/user");
                }
            } else if (request.startsWith("GET /admin")) {
                if (retrieveUser(urlParameters.get("user"), urlParameters.get("password")) == null) {
                    logger.info("Failed login attempt: " + urlParameters.get("user") + ":" + urlParameters.get("password"));
                    serve401();
                } else {
                    serveView("admin");
                }
            } else if (request.startsWith("GET /user")) {
                if (retrieveUser(urlParameters.get("user"), urlParameters.get("password")) == null) {
                    logger.info("Failed login attempt: " + urlParameters.get("user") + ":" + urlParameters.get("password"));
                    serve401();
                } else {
                    serveUserView();
                }
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

    private void serve401() throws IOException {
        writer.write("HTTP/1.1 401 Unauthorized\n" +
                "Content-Type: text/html; charset=UTF-8\n\n");
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(InputStream.class.getResourceAsStream("/html/401.html")));
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

    private void serveUserView() throws IOException {
        writer.write("HTTP/1.1 200 OK\n" +
                "status: 200 OK\n" +
                "version: HTTP/1.1\n" +
                "content-type: text/html; charset=UTF-8\n\n");
        serveSnippet("user/1");
        writer.write(CalculationManager.getInstance().getCalculation().toString());
        serveSnippet("user/2");
        writer.flush();
    }

    private void serveSnippet(String view) throws IOException {
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(InputStream.class.getResourceAsStream("/html/" + view + ".html")));
        String line = fileReader.readLine();
        do {
            writer.write(line);
            line = fileReader.readLine();
        } while (line != null);
    }

    private void redirect(String uri) throws IOException {
        String url = uri + "?" +  urlParams();
        writer.write("HTTP/1.1 200 ok\n" +
                "Refresh: 0; url=" + url + "\n" +
                "Content-type: text/html\n\n");
        writer.write("Please follow <a href=\"" + url + "\">this link</a>.");
        writer.flush();
    }

    private User retrieveUser(String user, String password) {
        if (user == null || password == null)
            return null;
        User u = httpServer.getUsers().get(user);
        if (u != null && password.equals(u.getPassword()))
            return u;
        return null;
    }

    private String urlParams() {
        if (request.indexOf('?') == -1)
            return null;
        return request.substring(request.indexOf('?')+1, request.indexOf(' ', request.indexOf('?')));
    }

    private void parseUrlParams() {
        String parameters = urlParams();
        if (parameters != null) {
            urlParameters = new HashMap<String, String>();
            for(String param : parameters.split("&")) {
                String split[] = param.split("=");
                urlParameters.put(split[0], split[1]);
            }
        }
    }
}
