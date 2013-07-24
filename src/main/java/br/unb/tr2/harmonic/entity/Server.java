package br.unb.tr2.harmonic.entity;

import br.unb.tr2.harmonic.entity.messages.SyncDatabaseRequest;
import br.unb.tr2.harmonic.entity.messages.SyncDatabaseResponse;
import br.unb.tr2.harmonic.entity.thread.ServerListenerThread;
import br.unb.tr2.harmonic.exceptions.ConnectionFailedException;
import br.unb.tr2.harmonic.server.CalculationManager;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

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

    volatile private List<Object> serverMessages = Collections.synchronizedList(new ArrayList<Object>());

    transient private Thread listenerThread = null;

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
        if (socket != null && !socket.isClosed()) {
            if (listenerThread == null) {
                listenerThread = new Thread(new ServerListenerThread(this, ois));
                listenerThread.start();
            }
            return;
        }
        socket = new Socket();
        if (identification != null)
            usingInstanceIdentification = identification;
        if (usingInstanceIdentification == null)
            throw new ConnectionFailedException(socket, "Instance that uses the Server object to connect must provide instance identification.");
        try {
            System.out.println("Trying to connect to server " + address.getHostAddress() + ":" + port.intValue() + " as " + usingInstanceIdentification.getClass().getSimpleName());
            socket.connect(new InetSocketAddress(address, port.intValue()), 5000);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            writeObjectToStream(usingInstanceIdentification);
            listenerThread = new Thread(new ServerListenerThread(this, ois));
            listenerThread.start();
        } catch (Exception e) {
            socket = null;
            e.printStackTrace();
            throw new ConnectionFailedException(socket, e.getMessage());
        }
    }


    public CalculationInterval getCalculationInterval() throws ConnectionFailedException {
        try {
            writeObjectToStream("CALCULATION INTERVAL REQUEST");
            long maxTime = System.currentTimeMillis() + 500;
            while (true) {
                if (System.currentTimeMillis() > maxTime)
                    break;
                synchronized (serverMessages) {
                    Iterator i = serverMessages.iterator();
                    while (i.hasNext()) {
                        Object message = i.next();
                        if (message instanceof CalculationInterval) {
                            serverMessages.remove(message);
                            return (CalculationInterval)message;
                        }
                    }
                }
            }
        } catch (IOException e) {
            if (!socket.isConnected()) {
                this.connect(null); // May throw ConnectionFailedException while trying to connect
                return getCalculationInterval();
            } else {
                e.printStackTrace();
                throw new ConnectionFailedException(socket, e.getMessage());
            }
        }
        return null;
    }

    public void sendCalculationInterval(CalculationInterval interval) throws ConnectionFailedException {
        try {
            writeObjectToStream(interval);
        } catch (IOException e) {
            if (!socket.isConnected()) {
                this.connect(null); // May throw ConnectionFailedException while trying to connect
                sendCalculationInterval(interval);
            } else {
                e.printStackTrace();
                throw new ConnectionFailedException(socket, e.getMessage());
            }
        }
    }

    public boolean pingServer() throws ConnectionFailedException {
        try {
            writeObjectToStream("PING");
            long maxTime = System.currentTimeMillis() + 500;
            while (true) {
                if (System.currentTimeMillis() > maxTime)
                    break;
                synchronized (serverMessages) {
                    Iterator i = serverMessages.iterator();
                    while (i.hasNext()) {
                        Object message = i.next();
                        if ("PONG".equals(message)) {
                            serverMessages.remove(message);
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectionFailedException(socket, e.getMessage());
        }
    }

    public void syncDatabase() throws ConnectionFailedException {
        try {
            writeObjectToStream(new SyncDatabaseRequest(CalculationManager.getInstance().calculatedUntil()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConnectionFailedException(socket, e.getMessage());
        }
    }

    private synchronized void writeObjectToStream(Object object) throws IOException {
        oos.writeObject(object);
        oos.flush();
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

    public boolean isConnected() {
        if (socket == null)
            return false;
        return socket.isConnected() && !socket.isClosed();
    }

    public void receivedServerMessage(Object o) {
        try {
            if ("PING".equals(o))
                writeObjectToStream("PONG");
            else if (o instanceof SyncDatabaseRequest) {
                SyncDatabaseRequest request = (SyncDatabaseRequest)o;
                writeObjectToStream(new SyncDatabaseResponse(request));
            } else if (o instanceof SyncDatabaseResponse) {
                CalculationManager.getInstance().addAll(((SyncDatabaseResponse) o).getIntervals());
            } else
                serverMessages.add(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
