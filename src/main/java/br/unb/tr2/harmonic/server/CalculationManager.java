package br.unb.tr2.harmonic.server;

import br.unb.tr2.harmonic.entity.CalculationInterval;

import java.util.*;

import static java.util.Collections.sort;

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
        CalculationInterval interval = null;
        if(!pendingRecalculationIntervals.isEmpty()) {
            interval = pendingRecalculationIntervals.iterator().next();
            pendingRecalculationIntervals.remove(interval);
        } else {
            interval = new CalculationInterval(nextIntervalStart, nextIntervalStart + INTERVAL_SIZE - 1);
            nextIntervalStart += INTERVAL_SIZE;
        }
        calculatingIntervals.add(interval);
        return interval;
    }

    public void addCalculated(CalculationInterval interval) {
        if(calculatedIntervals.add(interval))
            calculation += interval.getResult();
        calculatingIntervals.remove(interval);
    }

    public void recalculate(CalculationInterval interval) {
        CalculationInterval calculatedInterval = null;
        synchronized (calculatedIntervals) {
            Iterator<CalculationInterval> i = calculatedIntervals.iterator();
            while (i.hasNext()) {
                CalculationInterval in = i.next();
                if (interval.equals(in)) {
                    calculatedInterval = in;
                    break;
                }
            }
        }
        if (calculatedInterval != null) {
            calculation -= calculatedInterval.getResult();
            calculatedIntervals.remove(calculatedInterval);
            pendingRecalculationIntervals.add(interval);
        }
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

    public List<CalculationInterval> calculatedIntervalsCollection() {
        List<CalculationInterval> intervals = new ArrayList<CalculationInterval>(calculatedIntervals);
        sort(intervals, new Comparator<CalculationInterval>() {
            @Override
            public int compare(CalculationInterval o1, CalculationInterval o2) {
                return o2.getStart().compareTo(o1.getStart());
            }
        });
        return intervals;
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
