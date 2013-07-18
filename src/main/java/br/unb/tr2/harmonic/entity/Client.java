package br.unb.tr2.harmonic.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class Client implements Serializable {

    private UUID uuid;

    public Client() {
        uuid = UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }
}
