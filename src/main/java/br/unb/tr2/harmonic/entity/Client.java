package br.unb.tr2.harmonic.entity;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.UUID;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class Client implements Serializable {

    private UUID uuid;

    transient private Socket socket;

    transient private ObjectInputStream ois;

    transient private ObjectOutputStream oos;

    transient private Thread listenerThread;

    public Client() {
        uuid = UUID.randomUUID();
    }

    public void listen() {
        listenerThread = new Thread(new ClientListenerThread(socket, ois, oos));
        listenerThread.start();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public void setOis(ObjectInputStream ois) {
        this.ois = ois;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }
}
