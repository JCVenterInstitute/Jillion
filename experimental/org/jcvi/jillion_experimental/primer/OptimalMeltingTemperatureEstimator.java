/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.primer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.PeekableIterator;
/**
 * Includes several algorithms
 * that compute the optimal
 * melting temperature (T<sub>m</sub>) for
 * a primer sequence.
 * Melting Temperature is when half
 * of the DNA dissociates and becomes
 * single stranded.
 * @author dkatzel
 *
 */
public enum OptimalMeltingTemperatureEstimator {
	/**
	 * Use the Marmur formaula
	 * which only takes into account the number of 
	 * GC and AT nucleotides.  The positions of the nucleotides
	 * is does not matter.  This is the simplest
	 * estimation and is not recommended for
	 * large primers.
	 * @see <a href="http://www.pubget.com/paper/14470099">
	 Marmur J and Doty P (1962) J Mol Biol 5:109-118; PMID 14470099</a> 
	 */
	MARMUR{
		/**
		 * @param nM ignored
		 * {@inheritDoc}
		 */
		@Override
		public double estimateTm(NucleotideSequence sequence,double nM) {
			int gc=0, at=0;
			for(Nucleotide base : sequence){
				switch(base){
				case Adenine:
				case Thymine: at++;
						break;
				case Cytosine:
				case Guanine: gc++;
						break;
				default : //ignore for now
					break;
				}
			}
			return 4*gc+ 2*at;
		}
		
	},
	/**
	 * Use the Wallace et al. formula
	 * which only takes into account the number of 
	 * A,C,G and T nucleotides.  The positions of the nucleotides
	 * is does not matter.  This
	 * estimation and is not recommended for
	 * large primers.
	 * @see <a href="http://www.ncbi.nlm.nih.gov/pmc/articles/PMC327955/"> 
	 * RB Wallace, et al. Nucleic Acids Res. 1979;6:3543.</a> 
	 */
	WALLACE{
		/**
		 * @param sequence the NucleotideSequence to estimate the temperature.
		 * @param nM ignored
		 *
		 */
		@Override
		public double estimateTm(NucleotideSequence sequence, double nM) {
			int a=0,c=0,g=0,t=0;
			
			for(Nucleotide base : sequence){
				switch(base){
				case Adenine:	a++;
								break;
				case Thymine: t++;
						break;
				case Cytosine: c++;
							break;
				case Guanine: g++;
						break;
				default : //ignore for now
					break;
				}
			}
			return 64.9F +41*(g+c-16.4D)/(a+t+g+c);
		}
		
	},
	/**
	 * Computes the optimal temperature
	 * using the nearest neighbor algorithm
	 * as described by Freier et al using the 
	 * Breslaur 1986 transition tables.
	 * This is the same algorithm
	 * (if you add salt correction)
	 * that primer3 uses.
	 * @see <a href ="http://www.ncbi.nlm.nih.gov/pubmed/3459152">
	Breslauer KJ et al. Predicting DNA duplex stability from the base sequence.
	Proc Natl Acad Sci U S A. 1986 Jun;83(11):3746-50; PMID 3459152</a>
	
	 * @see <a href= "http://www.ncbi.nlm.nih.gov/pubmed/2432595">
	 Freier SM et al
	 Improved free-energy parameters for predictions of RNA duplex stability.
	 Proc Natl Acad Sci U S A. 1986 Dec;83(24):9373-7.</a>
	 */
	BRESLAUR{
		private final LookupTable deltaS = new LookupTable.Builder()
											.aa(240).ac(173).ag(208).at(239)
											.ca(129).cc(266).cg(278).ct(208)
											.ga(135).gc(267).gg(266).gt(173)
											.ta(169).tc(135).tg(129).tt(240)
											.build();
		private final LookupTable deltaH = new LookupTable.Builder()
											.aa(91).ac(65).ag(78).at(86)
											.ca(58).cc(110).cg(119).ct(78)
											.ga(56).gc(111).gg(110).gt(65)
											.ta(60).tc(56).tg(58).tt(91)
											.build();
		
		@Override
		public double estimateTm(NucleotideSequence sequence,
				double molarConcentration) {
			return estimateTm(sequence, molarConcentration, deltaH, deltaS);
		}

		@Override
		protected InitialValues getSymmetryCorrection() {
			return new InitialValues(13.4, 0D);
		}

		@Override
		protected InitialValues getInitialValuesFor(NucleotideSequence sequence) {
			if(hasGorC(sequence)){
				return new InitialValues(167.7D, 0D);
			}
			return new InitialValues(201.3D, 0D);
		}
		
	},
	/**
	 * Computes the optimal melting temperature
	 * using an improved nearest-neighbor 
	 * version of Freier et al 
	 * using updated transition tables
	 * as described 
	 * Allawi, H. T. & SantaLucia, J., Jr. (1997), "Thermodynamics and NMR of Internal G-T Mismatches in DNA", Biochemistry 36, 10581-10594.
	 */
	ALLAWI_SANTALUCIA{

		private final LookupTable deltaH = new LookupTable.Builder()
												.aa(79).ac(84).ag(78).at(72)
												.ca(85).cc(80).cg(106).ct(78)
												.ga(82).gc(98).gg(80).gt(84)
												.ta(72).tc(82).tg(85).tt(79)
												.build();
		private final LookupTable deltaS = new LookupTable.Builder()
												.aa(222).ac(224).ag(210).at(204)
												.ca(227).cc(199).cg(272).ct(210)
												.ga(222).gc(244).gg(199).gt(224)
												.ta(213).tc(222).tg(227).tt(222)
												.build();
		@Override
		public double estimateTm(NucleotideSequence sequence, double molarConcentration) {
			return estimateTm(sequence, molarConcentration, deltaH, deltaS);
		}
		
		@Override
		protected InitialValues getSymmetryCorrection() {
			return new InitialValues(14D, 0D);
		}
		
		@Override
		protected InitialValues getInitialValuesFor(NucleotideSequence sequence) {
			Nucleotide firstBase = sequence.get(0);
			Nucleotide lastBase = sequence.get(sequence.getLength()-1);
			double entropy=0D;
			double enthalpy = 0D;
			if(firstBase==Nucleotide.Guanine || firstBase==Nucleotide.Cytosine){
				entropy +=28;
				enthalpy -=1;
			}else if(firstBase==Nucleotide.Adenine || firstBase==Nucleotide.Thymine){
				entropy -=41;
				enthalpy -=23D;
			}
			if(lastBase==Nucleotide.Guanine || lastBase==Nucleotide.Cytosine){
				entropy +=28;
				enthalpy -=1;
			}else if(lastBase==Nucleotide.Adenine || lastBase==Nucleotide.Thymine){
				entropy -=41;
				enthalpy -=23D;
			}
			return new InitialValues(entropy,enthalpy);
		}
		
		
		
	},
	/**
	 * Computes the optimal melting temperature
	 * using an the same transition tables
	 * as {@link #ALLAWI_SANTALUCIA} (now calling
	 * this "unified" transition tables)
	 * but uses a modified initiation
	 * parameters for terminal
	 * G*C and terminal A*T pairs.
	 * @see <a href="http://www.ncbi.nlm.nih.gov/pubmed/9465037">
	 J J SantaLucia.
	 A unified view of polymer, dumbbell, and oligonucleotide DNA
nearest-neighbor thermodynamics. 
	Proc Natl Acad Sci U S A 95(4):1460-5 (1998), PMID 9465037 
	 </a>
	 */
	SANTALUCIA_1998{
			
		private final LookupTable deltaH = new LookupTable.Builder()
											.aa(79).ac(84).ag(78).at(72)
											.ca(85).cc(80).cg(106).ct(78)
											.ga(82).gc(98).gg(80).gt(84)
											.ta(72).tc(82).tg(85).tt(79)
											.build();
		private final LookupTable deltaS = new LookupTable.Builder()
											.aa(222).ac(224).ag(210).at(204)
											.ca(227).cc(199).cg(272).ct(210)
											.ga(222).gc(244).gg(199).gt(224)
											.ta(213).tc(222).tg(227).tt(222)
											.build();
		
		@Override
		public double estimateTm(NucleotideSequence sequence, double molarConcentration) {
			return estimateTm(sequence, molarConcentration, deltaH, deltaS);
		}

		
		@Override
		protected InitialValues getInitialValuesFor(NucleotideSequence sequence) {
			double entropy=0D;
			double enthalpy=0D;
			if(hasGorC(sequence)){
				entropy +=59;
			}else{
				entropy +=90;
			}
			Nucleotide firstBase=sequence.get(0);
			if(firstBase==Nucleotide.Thymine || firstBase == Nucleotide.Adenine){
				entropy -=41;
				enthalpy -=23;
			}else{
				entropy +=28;
				enthalpy -=1;
			}

			return new InitialValues(entropy, enthalpy);
		}


		@Override
		protected InitialValues getSymmetryCorrection() {
			return new InitialValues(14D, 0D);
		}
		
		
	},
	/**
	 * Computes the optimal melting temperature
	 * using transition tables derived from
	 *  John SantaLucia, Jr., Hatim T. Allawi, and P. Ananda Seneviratne.
	Improved Nearest-Neighbor Parameters for Predicting DNA Duplex Stability. 
	Biochemistry 1996, 35, 3555-3562.
	 *  @see <a href="http://www.ncbi.nlm.nih.gov/pubmed/8639506">
	 SantaLucia J Jr, Allawi HT, Seneviratne PA.
	 Improved nearest-neighbor parameters for predicting DNA duplex stability.
	Biochemistry. 1996 Mar 19;35(11):3555-62; PMID 8639506 </a>
	 */
	SANTALUCIA_1996{
			
		private final LookupTable deltaH = new LookupTable.Builder()
												.aa(84).ac(86).ag(61).at(65)
												.ca(74).cc(67).cg(101).ct(61)
												.ga(77).gc(111).gg(67).gt(86)
												.ta(63).tc(77).tg(74).tt(84)
												.build();
		private final LookupTable deltaS = new LookupTable.Builder()
												.aa(236).ac(230).ag(161).at(188)
												.ca(193).cc(156).cg(255).ct(161)
												.ga(203).gc(284).gg(156).gt(230)
												.ta(185).tc(203).tg(193).tt(236)
												.build();
										
		@Override
		public double estimateTm(NucleotideSequence sequence, double molarConcentration) {
			return estimateTm(sequence, molarConcentration, deltaH, deltaS);
		}

		
		@Override
		protected InitialValues getInitialValuesFor(NucleotideSequence sequence) {
			//this data is set by table 3 in the paper
			//specifically table 3 footnotes b,c and e
			double entropy=0D;
			double enthalpy=0D;
			if(hasGorC(sequence)){
				//initiation paramter for duplexes
				//that contain at least one G*C pair
				entropy +=59;
			}else{
				//initiation paramter for duplexes
				//that contain only A*T pairs
				entropy +=90;
			}
			//compute penalty for EACH terminal 5'T*A 3'
			// but not 5' A*T 3'
			enthalpy -=40 *getNumberOfTerminal5primerTs(sequence);
			

			return new InitialValues(entropy, enthalpy);
		}


		private int getNumberOfTerminal5primerTs(NucleotideSequence sequence) {
			NucleotideSequence reversedSeq = new NucleotideSequenceBuilder(sequence)
												.reverse()
												.build();
			Iterator<Nucleotide> iter = reversedSeq.iterator();
			boolean done=false;
			int numberOfTerminalTs=0;
			while(!done && iter.hasNext()){
				if(iter.next() == Nucleotide.Thymine){
					numberOfTerminalTs++;
				}else{
					done=true;
				}
				
			}
			return numberOfTerminalTs;
		}


		@Override
		protected InitialValues getSymmetryCorrection() {
			//this data is set by table 3 in the paper
			//specifically table 3 footnote d
			return new InitialValues(14D, 0D);
		}
		
		
	},
	/**
	  Computes the optimal melting temperature
	 * using an improved nearest-neighbor 
	 * version of Freier et al 
	 * using updated transition tables
	 * as described by Sugimoto et all.
	 * @see <a href="http://www.ncbi.nlm.nih.gov/pmc/articles/PMC146261/">
	 Naoki Sugimoto, Shu-ich Nakano, Mari Yoneyama and Kei-ich Honda.
	 Improved thermodynamic parameters and helix
initiation factor to predict stability of DNA duplexes.
	Nucleic Acids Research. 1996 Vol. 24. No. 22 4501-4505; PMCID: PMC146261 
	 </a>
	 */
	SUGIMOTO{
		
		private final LookupTable deltaH = new LookupTable.Builder()
											.aa(80).ac(94).ag(66).at(56)
											.ca(82).cc(109).cg(118).ct(66)
											.ga(88).gc(105).gg(109).gt(94)
											.ta(66).tc(88).tg(82).tt(80)
											.build();
		private final LookupTable deltaS = new LookupTable.Builder()
											.aa(219).ac(255).ag(164).at(152)
											.ca(210).cc(284).cg(290).ct(164)
											.ga(235).gc(264).gg(284).gt(255)
											.ta(184).tc(235).tg(210).tt(219)
											.build();
		
		@Override
		public double estimateTm(NucleotideSequence sequence, double molarConcentration) {
			return estimateTm(sequence, molarConcentration, deltaH, deltaS);
		}
		
		
		@Override
		protected InitialValues getInitialValuesFor(NucleotideSequence sequence) {
			
			return new InitialValues(90D, -6D);
		}

		@Override
		protected InitialValues getSymmetryCorrection() {
			return new InitialValues(14D, 0D);
		}
		
	}
	
	;
	
	private static final double R = 1.987D;
	
	protected final double estimateTm(NucleotideSequence sequence, double nM, 
			LookupTable enthalpyLookup,  LookupTable entropyLookup){
		verifyOnlyHasACGT(sequence);
		if(nM<0){
			throw new IllegalArgumentException("concentration must be >0");
		}
		//initiation
		InitialValues initialValues = getInitialValuesFor(sequence);
		double totalEntropy = initialValues.getInitialEntropy();
		
		double totalEnthalpy = initialValues.getInitialEnthalpy();
		double adjustedMolarConcentration;
		if(isSymmetric(sequence)){
			InitialValues symmetryPenalty = getSymmetryCorrection();
			totalEntropy += symmetryPenalty.getInitialEntropy();
			totalEnthalpy += symmetryPenalty.getInitialEnthalpy();
			adjustedMolarConcentration = nM/1000000000D;
		}else{
			adjustedMolarConcentration = nM/4000000000D;
		}
		PeekableIterator<Nucleotide> iter = IteratorUtil.createPeekableIterator(sequence.iterator());
		
		Nucleotide previous = iter.next();		
		while(iter.hasNext()){
			Nucleotide next = iter.next();
			NucleotideSequence diNucleotide = new NucleotideSequenceBuilder(2)
												.append(previous)
												.append(next)
												.build();
			totalEnthalpy += enthalpyLookup.lookup(diNucleotide);
			totalEntropy += entropyLookup.lookup(diNucleotide);
			previous = next;
		}
		//enthalpy lookups are in 100cal/mol
		//need to convert them
		totalEnthalpy *=-100D;
		//entropy lookups are in .1` cal/K/mol
		//need to convert them
		totalEntropy *=-0.1D;
		
		
		double tempInKelvin= totalEnthalpy / (totalEntropy + ( R * Math.log(adjustedMolarConcentration)));
		return tempInKelvin - 273.15D;
	
	}
	protected InitialValues getSymmetryCorrection() {
		return new InitialValues();
	}
	protected InitialValues getInitialValuesFor(NucleotideSequence sequence) {
		return new InitialValues();
	}
	
	private void verifyOnlyHasACGT(NucleotideSequence sequence) {
		for(Nucleotide n : sequence){
			if(n != Nucleotide.Adenine && n != Nucleotide.Cytosine 
					&& n != Nucleotide.Guanine && n != Nucleotide.Thymine){
				throw new IllegalArgumentException(
						String.format("invalid base %s can only contain ACGTs", n));
			}
		}
		
	}
	/**
	 * Is this sequence equal
	 * to its reverse complement?
	 * @param sequence
	 * @return
	 */
	private static boolean isSymmetric(NucleotideSequence sequence) {
		NucleotideSequence complement = new NucleotideSequenceBuilder(sequence)
											.reverseComplement()
											.build();
		return sequence.equals(complement);
	}
	
	/**
	 * Estimate the optimal melting
	 * temperature in degrees Celsius for the given
	 * {@link NucleotideSequence} and concentration.
	 * @param sequence the NucleotideSequence to compute;
	 * can only have the nucleotides ACG and T.
	 * @param nM the DNA concentration in nM.
	 * @return the temperature as a double.
	 * @throws IllegalArgumentException if sequence contains
	 * any bases other than ACG and T.
	 * @throws IllegalArgumentException if nM is negative.
	 */
	public abstract double estimateTm(NucleotideSequence sequence, double nM);
	
	protected boolean hasGorC(NucleotideSequence sequence) {
		boolean hasGC=false;
		for(Nucleotide n : sequence){
			if(n == Nucleotide.Guanine || n == Nucleotide.Cytosine){
				hasGC=true;
				break;
			}
		}
		return hasGC;
	}

	private static final class InitialValues{
		private double initialEntropy;
		private double initialEnthalpy;
		public InitialValues(){
			this(0D,0D);
		}
		public InitialValues(double initialEntropy, double initialEnthalpy) {
			this.initialEntropy = initialEntropy;
			this.initialEnthalpy = initialEnthalpy;
		}
		public final double getInitialEntropy() {
			return initialEntropy;
		}
		public final double getInitialEnthalpy() {
			return initialEnthalpy;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(initialEnthalpy);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(initialEntropy);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof InitialValues)) {
				return false;
			}
			InitialValues other = (InitialValues) obj;
			if (Double.doubleToLongBits(initialEnthalpy) != Double
					.doubleToLongBits(other.initialEnthalpy)) {
				return false;
			}
			if (Double.doubleToLongBits(initialEntropy) != Double
					.doubleToLongBits(other.initialEntropy)) {
				return false;
			}
			return true;
		}
		
		
	}
	private static final class LookupTable implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Map<NucleotideSequence,Integer> map;
		
		LookupTable(Map<NucleotideSequence,Integer> map){
			this.map = map;
		}
		
		public int lookup(NucleotideSequence diNucleotide){
			Integer value = map.get(diNucleotide);
			if(value ==null){
				throw new NoSuchElementException("no value " + diNucleotide + " in lookup table");
			}
			return value;
		}
		private static final class Builder{

			private final Map<NucleotideSequence,Integer> map = new HashMap<NucleotideSequence, Integer>(10);
			
			public Builder aa(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("AA").build(), boxedValue);				
				return this;
			}
			public Builder ac(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("AC").build(), boxedValue);				
				return this;
			}
			public Builder ag(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("AG").build(), boxedValue);				
				return this;
			}
			public Builder at(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("AT").build(), boxedValue);				
				return this;
			}
			
			public Builder ca(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("CA").build(), boxedValue);				
				return this;
			}
			public Builder cc(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("CC").build(), boxedValue);				
				return this;
			}
			public Builder cg(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("CG").build(), boxedValue);				
				return this;
			}
			public Builder ct(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("CT").build(), boxedValue);				
				return this;
			}
			
			public Builder ga(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("GA").build(), boxedValue);				
				return this;
			}
			public Builder gc(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("GC").build(), boxedValue);				
				return this;
			}
			public Builder gg(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("GG").build(), boxedValue);				
				return this;
			}
			public Builder gt(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("GT").build(), boxedValue);				
				return this;
			}
			public Builder ta(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("TA").build(), boxedValue);				
				return this;
			}
			public Builder tc(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("TC").build(), boxedValue);				
				return this;
			}
			public Builder tg(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("TG").build(), boxedValue);				
				return this;
			}
			public Builder tt(int value){
				Integer boxedValue = Integer.valueOf(value);
				map.put(new NucleotideSequenceBuilder("TT").build(), boxedValue);				
				return this;
			}
			
			
			public LookupTable build() {
				return new LookupTable(map);
			}
		}
	}
}
