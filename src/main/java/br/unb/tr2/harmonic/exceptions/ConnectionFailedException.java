package br.unb.tr2.harmonic.exceptions;

import java.io.IOException;
import java.net.Socket;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class ConnectionFailedException extends Exception {

    public ConnectionFailedException(Socket socket, String message) {
        super(message);
        try {
            if(socket != null) socket.close();
        } catch (IOException e) {
        }
    }
}
