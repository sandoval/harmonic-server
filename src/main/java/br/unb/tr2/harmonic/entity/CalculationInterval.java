package br.unb.tr2.harmonic.entity;

import java.io.Serializable;

/**
 * Copyright (C) 2013 Loop EC - All Rights Reserved
 * Created by sandoval for harmonic-server
 */
public class CalculationInterval implements Serializable {

    private static final long serialVersionUID = 236271456999483720L;

    private Long start;

    private Long end;

    private Double result = null;

    private Long executionTime = null;

    public CalculationInterval(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    public void calculate() {
        Long start = System.currentTimeMillis();
        result = 0d;
        for (long i = this.start; i <= end; i++)
            result += (1d / (double)i);
        executionTime = System.currentTimeMillis() - start;
    }

    public Long getStart() {
        return start;
    }

    public Long getEnd() {
        return end;
    }

    public Double getResult() {
        return result;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalculationInterval interval = (CalculationInterval) o;

        if (end != null ? !end.equals(interval.end) : interval.end != null) return false;
        if (start != null ? !start.equals(interval.start) : interval.start != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
