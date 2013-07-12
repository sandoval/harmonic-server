package br.unb.tr2.harmonic.server;

import br.unb.tr2.harmonic.entity.CalculationInterval;

import java.io.*;
import java.net.Socket;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class ClientHandler implements Runnable {

    private Socket socket;

    private CalculationManager calculationManager;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.calculationManager = CalculationManager.getInstance();
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            while (true) {
                try {
                    String request = (String)ois.readObject();
                    if ("CALCULATION INTERVAL REQUEST".equals(request)) {
                        CalculationInterval interval = calculationManager.getCalculationInterval();
                        oos.writeObject(interval);
                        oos.flush();
                        CalculationInterval calculatedInterval = null;
                        calculatedInterval = (CalculationInterval)ois.readObject();
                        calculationManager.addCalculated(calculatedInterval);
                    }
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
