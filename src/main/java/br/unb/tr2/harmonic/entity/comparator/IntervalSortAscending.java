package br.unb.tr2.harmonic.entity.comparator;

import br.unb.tr2.harmonic.entity.CalculationInterval;

import java.util.Comparator;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class IntervalSortAscending implements Comparator<CalculationInterval> {
    @Override
    public int compare(CalculationInterval o1, CalculationInterval o2) {
        return o2.getStart().compareTo(o1.getStart());
    }
}
