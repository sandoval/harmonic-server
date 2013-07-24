package br.unb.tr2.harmonic.entity.messages;

import br.unb.tr2.harmonic.entity.CalculationInterval;
import br.unb.tr2.harmonic.server.CalculationManager;

import java.io.Serializable;
import java.util.Set;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class SyncDatabaseResponse implements Serializable {

    private static final long serialVersionUID = 7937414738528149703L;

    private Long calculatedUntil;

    private Set<CalculationInterval> intervals;

    public SyncDatabaseResponse(SyncDatabaseRequest request) {
        CalculationManager calculationManager = CalculationManager.getInstance();
        calculatedUntil = calculationManager.calculatedUntil();
        if (calculationManager.calculatedUntil() > request.getCalculatedUntil()) {
            intervals = calculationManager.getIntervalsSince(request.getCalculatedUntil()+1);
        }
    }

    public Long getCalculatedUntil() {
        return calculatedUntil;
    }

    public Set<CalculationInterval> getIntervals() {
        return intervals;
    }
}
