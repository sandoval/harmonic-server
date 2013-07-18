package br.unb.tr2.harmonic.server;

import br.unb.tr2.harmonic.entity.CalculationInterval;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class CalculationManager {

    private static final Long INTERVAL_SIZE = 100000000l;

    private static CalculationManager instance;

    private Set<CalculationInterval> calculatedIntervals = Collections.synchronizedSet(new HashSet<CalculationInterval>());

    private Set<CalculationInterval> calculatingIntervals = Collections.synchronizedSet(new HashSet<CalculationInterval>());

    private Set<CalculationInterval> pendingRecalculationIntervals = Collections.synchronizedSet(new HashSet<CalculationInterval>());

    private Long nextIntervalStart = 1l;

    private Double calculation = 0d;

    public CalculationManager() {
        new Thread(new Watchdog(this)).start();
    }

    public synchronized CalculationInterval getCalculationInterval() {
        CalculationInterval interval = new CalculationInterval(nextIntervalStart, nextIntervalStart + INTERVAL_SIZE - 1);
        nextIntervalStart += INTERVAL_SIZE;
        calculatingIntervals.add(interval);
        return interval;
    }

    public void addCalculated(CalculationInterval interval) {
        if(calculatedIntervals.add(interval))
            calculation += interval.getResult();
        calculatingIntervals.remove(interval);
    }

    public static CalculationManager getInstance() {
        if (instance == null)
            instance = new CalculationManager();
        return instance;
    }

    public Double getCalculation() {
        return calculation;
    }

    private Set<CalculationInterval> getCalculatingIntervals() {
        return calculatingIntervals;
    }

    private Set<CalculationInterval> getPendingRecalculationIntervals() {
        return pendingRecalculationIntervals;
    }

    public int calculatedIntervals() {
        return calculatedIntervals.size();
    }

    public Set<CalculationInterval> pendingCalculationIntervals() {
        return new HashSet<CalculationInterval>(calculatingIntervals);
    }

    private class Watchdog implements Runnable {

        private CalculationManager calculationManager;

        private Set<CalculationInterval> lastCalculatingIntervals = Collections.synchronizedSet(new HashSet<CalculationInterval>());

        public Watchdog(CalculationManager calculationManager) {
            this.calculationManager = calculationManager;

        }

        @Override
        public void run() {
            while (true) {
                System.out.println("Resultado do Cálculo: " + calculationManager.getCalculation());
                Set<CalculationInterval> recalculationIntervals = new HashSet<CalculationInterval>(lastCalculatingIntervals);
                recalculationIntervals.retainAll(calculationManager.getCalculatingIntervals());
                calculationManager.getCalculatingIntervals().removeAll(recalculationIntervals);
                calculationManager.getPendingRecalculationIntervals().addAll(recalculationIntervals);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
