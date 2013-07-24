package br.unb.tr2.harmonic.entity.messages;

import java.io.Serializable;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class SyncDatabaseRequest implements Serializable {

    private static final long serialVersionUID = -4657473369433776512L;

    private Long calculatedUntil;

    public SyncDatabaseRequest(Long calculatedUntil) {
        this.calculatedUntil = calculatedUntil;
    }

    public Long getCalculatedUntil() {
        return calculatedUntil;
    }
}
