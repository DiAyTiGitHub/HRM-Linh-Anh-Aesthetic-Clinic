package com.globits.hr.dto;

import java.time.LocalTime;

public class RelaxPeriod {
    private LocalTime startRelax;
    private LocalTime endRelax;
    
    public RelaxPeriod() {
    }

    public RelaxPeriod(LocalTime startRelax, LocalTime endRelax) {
        this.startRelax = startRelax;
        this.endRelax = endRelax;
    }

    public LocalTime getStartRelax() { 
    	return startRelax; 
    }
    
    public LocalTime getEndRelax() { 
    	return endRelax; 
    }
}