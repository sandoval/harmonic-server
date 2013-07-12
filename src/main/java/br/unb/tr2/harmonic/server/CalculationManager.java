package br.unb.tr2.harmonic.server;

import br.unb.tr2.harmonic.entity.CalculationInterval;

import java.util.*;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class CalculationManager {

    private static final Long INTERVAL_SIZE = 100000000l;

    private static CalculationManager instance;

    private List<CalculationInterval> calculatedIntervals = Collections.synchronizedList(new ArrayList<CalculationInterval>());

    private Set<CalculationInterval> calculatingIntervals = Collections.synchronizedSet(new HashSet<CalculationInterval>());

    private Long nextIntervalStart = 1l;

    private Double calculation = 0d;

    public CalculationManager() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Resultado do Cálculo: " + CalculationManager.getInstance().getCalculation());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public synchronized CalculationInterval getCalculationInterval() {
        CalculationInterval interval = new CalculationInterval(nextIntervalStart, nextIntervalStart + INTERVAL_SIZE - 1);
        nextIntervalStart += INTERVAL_SIZE;
        calculatingIntervals.add(interval);
        return interval;
    }

    public void addCalculated(CalculationInterval interval) {
        calculatedIntervals.add(interval);
        calculatingIntervals.remove(interval);
        calculation += interval.getResult();
    }

    public static CalculationManager getInstance() {
        if (instance == null)
            instance = new CalculationManager();
        return instance;
    }

    public Double getCalculation() {
        return calculation;
    }
}
