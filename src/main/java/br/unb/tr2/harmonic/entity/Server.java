package br.unb.tr2.harmonic.entity;

import br.unb.tr2.harmonic.exceptions.ConnectionFailedException;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class Server implements Serializable {

    private static final long serialVersionUID = -6349713745196753422L;

    private InetAddress address;

    private Long port;

    private UUID uuid;

    transient private Object usingInstanceIdentification;

    transient private Socket socket = null;

    transient private ObjectInputStream ois = null;

    transient private ObjectOutputStream oos = null;

    public Server(InetAddress address, Long port) {
        this(address, port, UUID.randomUUID());
    }

    public Server(InetAddress address, Long port, UUID uuid) {
        this.address = address;
        this.port = port;
        this.uuid = uuid;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Long getPort() {
        return port;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void connect(Object identification) throws ConnectionFailedException {
        if (socket != null && !socket.isClosed())
            return;
        socket = new Socket();
        if (identification != null)
            usingInstanceIdentification = identification;
        if (usingInstanceIdentification == null)
            throw new ConnectionFailedException("Instance that uses the Server object to connect must provide instance identification.");
        try {
            System.out.println("Trying to connect to server " + address.getHostAddress() + ":" + port.intValue() + " as " + usingInstanceIdentification.getClass().getSimpleName());
            socket.connect(new InetSocketAddress(address, port.intValue()), 5000);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            writeObjectToStream(usingInstanceIdentification);
        } catch (Exception e) {
            socket = null;
            e.printStackTrace();
            throw new ConnectionFailedException(e.getMessage());
        }
    }


    public CalculationInterval getCalculationInterval() throws ConnectionFailedException {
        connect(null);
        CalculationInterval interval = null;
        try {
            oos.writeObject("CALCULATION INTERVAL REQUEST");
            oos.flush();
            interval = (CalculationInterval)ois.readObject();
        } catch (IOException e) {
            if (!socket.isConnected()) {
                this.connect(null); // May throw ConnectionFailedException while trying to connect
                return getCalculationInterval();
            } else {
                e.printStackTrace();
                throw new ConnectionFailedException(e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return interval;
    }

    public void sendCalculationInterval(CalculationInterval interval) throws ConnectionFailedException {
        connect(null);
        try {
            oos.writeObject(interval);
            oos.flush();
        } catch (IOException e) {
            if (!socket.isConnected()) {
                this.connect(null); // May throw ConnectionFailedException while trying to connect
                sendCalculationInterval(interval);
            } else {
                e.printStackTrace();
                throw new ConnectionFailedException(e.getMessage());
            }
        }
    }

    public boolean pingServer() throws ConnectionFailedException {
        connect(null);
        try {
            String response = (String)writeObjectAndRead("PING");
            if ("PONG".equals(response))
                return true;
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectionFailedException(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void writeObjectToStream(Object object) throws IOException {
        System.out.println("ESCREVENDO: " + object);
        oos.writeObject(object);
        oos.flush();
    }

    private synchronized Object readObjectFromStream() throws IOException,ClassNotFoundException {
        Object o = ois.readObject();
        System.out.println("LENDO: " + o);
        return o;
    }

    private synchronized Object writeObjectAndRead(Object sendingObject) throws IOException,ClassNotFoundException {
        writeObjectToStream(sendingObject);
        return readObjectFromStream();
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }

    public void setOis(ObjectInputStream ois) {
        this.ois = ois;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        if (uuid != null ? !uuid.equals(server.uuid) : server.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }

    public void setUsingInstanceIdentification(Object usingInstanceIdentification) {
        this.usingInstanceIdentification = usingInstanceIdentification;
    }

    public boolean isConnected() {
        if (socket == null)
            return false;
        return true;
    }

    public void receiveRequests() throws ConnectionFailedException {
        try {
            while (true) {
                String request = (String) readObjectFromStream();
                if ("PING".equals(request)) {
                    writeObjectToStream("PONG");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
