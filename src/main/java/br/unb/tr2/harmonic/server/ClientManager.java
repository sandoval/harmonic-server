package br.unb.tr2.harmonic.server;

import br.unb.tr2.harmonic.entity.Client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class ClientManager {

    private static ClientManager instance;

    private Set<Client> clients = Collections.synchronizedSet(new HashSet<Client>());

    public int clientInstances() {
        HashSet<Client> removeClients = new HashSet<Client>();
        synchronized (clients) {
            for (Client client : clients)
                if (!client.isConnected())
                    removeClients.add(client);
        }
        clients.removeAll(removeClients);
        return clients.size();
    }

    public void addClient(Client client) {
        clients.add(client);
    }

    private ClientManager(){}

    public static ClientManager getInstance() {
        if (instance == null)
            instance = new ClientManager();
        return instance;
    }
}
