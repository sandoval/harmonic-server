package br.unb.tr2.harmonic.entity.thread;

import br.unb.tr2.harmonic.entity.CalculationInterval;
import br.unb.tr2.harmonic.server.CalculationManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class ClientListenerThread implements Runnable {
    private ObjectInputStream ois;

    private ObjectOutputStream oos;

    private Socket socket;

    private CalculationManager calculationManager;

    public ClientListenerThread(Socket socket, ObjectInputStream ois, ObjectOutputStream oos) {
        this.socket = socket;
        this.calculationManager = CalculationManager.getInstance();
        this.oos = oos;
        this.ois = ois;
    }

    @Override
    public void run() {
        try {
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
