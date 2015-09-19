/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.experimental.primer;

public enum SaltCorrectionStrategy {
	/**
	 * The used salt correction
	 * used most often in programs
	 * like primer3.
	 * The correction equation is 
	 * <pre>16.6 * log10(concentration in nM) + initialTm</pre>
	 * The implementation will convert the input mM into nM.
	 * This equation
	 * was derived by
	 * 
	 * C C Schildkraut Biopolymers 3(2):195-208 (1965), PMID 5889540.
	 */
	SCHILDKRAUT_LIFSON(16.6D),
	/**
	 * The used salt correction
	 * listed in the santaLucia96 paper.
	 * The correction equation is 
	 * <pre>12.5 * log10(concentration in nM) + initialTm</pre>
	 * The implementation will convert the input mM into nM.
	 * This equation
	 * was derived by
	 * SantaLucia96
	 * @see <a href="http://www.ncbi.nlm.nih.gov/pubmed/8639506">
	 SantaLucia J Jr, Allawi HT, Seneviratne PA.
	 Improved nearest-neighbor parameters for predicting DNA duplex stability.
	Biochemistry. 1996 Mar 19;35(11):3555-62; PMID 8639506 </a>
	 */
	SANTALUCIA_1996(12.5D),
	/**
	 * No Salt Correction,
	 * {@link #adjustTemperature(double, double)}
	 * returns the initial uncorrected temperature.
	 */
	NONE(0D){
		/**
		 * @return initialTm always.
		 */
		@Override
		public double adjustTemperature(double initialTm, double mM) {
			return initialTm;
		}
		
	}
	;
	
	private final double adjustmentConstant;
	
	private SaltCorrectionStrategy(double adjustmentConstant){
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
	public double adjustTemperature(double initialTm, double mM){
		if(mM<0){
			throw new IllegalArgumentException("concentration can not be negative");
		}
		return adjustmentConstant * Math.log10(mM/1000D) + initialTm;
	}
	
}
