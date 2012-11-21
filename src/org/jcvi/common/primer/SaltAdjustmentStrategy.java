package org.jcvi.common.primer;

public enum SaltAdjustmentStrategy {
	/**
	 * The used salt correction
	 * used most often in programs
	 * like primer3.
	 * The actual correction equation
	 * was derived by
	 * 
	 * C C Schildkraut Biopolymers 3(2):195-208 (1965), PMID 5889540.
	 */
	SCHILDKRAUT_LIFSON(16.6D),
	SANTALUCIA(12.5D)
	;
	
	private final double adjustmentConstant;
	
	private SaltAdjustmentStrategy(double adjustmentConstant){
		this.adjustmentConstant = adjustmentConstant;
	}
	/**
	 * Adjust the given melting temperature
	 * for the given salt concentration in mM.
	 * @param initialTm the initial temperature
	 * in degrees celsius;
	 * @param mM salt concentration in mM;
	 * can not be negative.
	 * @return a new temperature in degrees
	 * celsius taking salt concentration into account.
	 */
	public double adjustTm(double initialTm, double mM){
		if(mM<0){
			throw new IllegalArgumentException("concentration can not be negative");
		}
		return adjustmentConstant * Math.log10(mM/1000D) + initialTm;
	}
	
}
