package org.jcvi.common.primer;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.IteratorUtil;
import org.jcvi.common.core.util.iter.PeekableIterator;

public enum PrimerTempEstimator {
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
	 * Use the Wallace formaula
	 * which only takes into account the number of 
	 * A,C,G and T nucleotides.  The positions of the nucleotides
	 * is does not matter.  This
	 * estimation and is not recommended for
	 * large primers.
	 * @see <a href="http://www.pubget.com/paper/158748">
	 Wallace RB et al. (1979) Nucleic Acids Res 6:3543-3557; PMID 158748</a> 
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
	Proc Natl Acad Sci U S A. 1986 Jun;83(11):3746-50; PMID 3459152<a>
	
	 * @see <a href= "http://www.ncbi.nlm.nih.gov/pubmed/2432595">
	 Freier SM et al
	 Improved free-energy parameters for predictions of RNA duplex stability.
	 Proc Natl Acad Sci U S A. 1986 Dec;83(24):9373-7.
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
		
	},
	/**
	 * Computes the optimal melting temperature
	 * using an improved nearest-neighbor 
	 * version of Freier et al 
	 * using updated transition tables
	 * as described by SantaLucia96.
	 * @see <a href="http://www.ncbi.nlm.nih.gov/pubmed/8639506">
	 SantaLucia J Jr, Allawi HT, Seneviratne PA.
	 Improved nearest-neighbor parameters for predicting DNA duplex stability.
	Biochemistry. 1996 Mar 19;35(11):3555-62; PMID 8639506 
	 </a>
	 */
	SANTALUCIA_ALLAWI{
			
		private final LookupTable deltaH = new LookupTable.Builder()
											.aa(84).ac(74).ag(61).at(65)
											.ca(74).cc(67).cg(101).ct(61)
											.ga(77).gc(111).gg(67).gt(86)
											.ta(63).tc(77).tg(74).tt(84)
											.build();
		private final LookupTable deltaS = new LookupTable.Builder()
											.aa(236).ac(230).ag(161).at(188)
											.ca(193).cc(156).cg(255).ct(161)
											.ga(203).gc(255).gg(156).gt(230)
											.ta(185).tc(203).tg(193).tt(236)
											.build();
		
		@Override
		public double estimateTm(NucleotideSequence sequence, double molarConcentration) {
			return estimateTm(sequence, molarConcentration, deltaH, deltaS);
		}
		/**
		 * The Santa Lucia paper has extra columns
		 * in table 3 to descript the deltaS and deltaG
		 * values for duplexes that contain at
		 * least one GC base pair and those that
		 * only contain AT base pairs.
		 * {@inheritDoc}
		 */
		@Override
		protected double getInitialEntropyFor(
				NucleotideSequence sequence) {
			boolean hasGC=false;
			for(Nucleotide n : sequence){
				if(n == Nucleotide.Guanine || n == Nucleotide.Cytosine){
					hasGC=true;
					break;
				}
			}
			if(hasGC){
				return 59;
			}else{
				return 90;
			}
		}
		/**
		 * The Santa Lucia paper found
		 * sequences with 5' terminal T-A 3' bp
		 * should get a entropy penalty
		 * while others (even including
		 * 5' A-T 3' bp) should not.
		 * {@inheritDoc}
		 */
		@Override
		protected double getTerminalBasePenalty(Nucleotide terminalBase) {
			//only 5' T should be penalized
			return terminalBase ==Nucleotide.Thymine? 4: 0;
		}
		
		
	},
	/**
	 * Computes the optimal melting temperature
	 * using an the same equation
	 * as {@link #SANTALUCIA_ALLAWI} except 
	 * using updated "unified" transition tables
	 * that were derived differently as
	 * described in SantaLucia 98.
	 * @see <a href="">
	 J J SantaLucia.
	 A unified view of polymer, dumbbell, and oligonucleotide DNA
nearest-neighbor thermodynamics. 
	Proc Natl Acad Sci U S A 95(4):1460-5 (1998), PMID 9465037 
	 </a>
	 */
	SANTALUCIA{
			
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
		/**
		 * The Santa Lucia paper has extra columns
		 * in table 3 to descript the deltaS and deltaG
		 * values for duplexes that contain at
		 * least one GC base pair and those that
		 * only contain AT base pairs.
		 * {@inheritDoc}
		 */
		@Override
		protected double getInitialEntropyFor(NucleotideSequence sequence) {
			Nucleotide firstBase = sequence.get(0);
			Nucleotide secondBase = sequence.get(1);
			if((firstBase==Nucleotide.Guanine && secondBase==Nucleotide.Cytosine)
					|| (firstBase==Nucleotide.Cytosine && secondBase==Nucleotide.Guanine) ){
				return -28;
			}else if((firstBase==Nucleotide.Adenine && secondBase==Nucleotide.Thymine)
				|| (firstBase==Nucleotide.Thymine && secondBase==Nucleotide.Adenine) ){
				return -28;
			}
				return 0D;
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
		
		double totalEntropy = getInitialEntropyFor(sequence);
		double totalEnthalpy;
		double adjustedMolarConcentration;
		if(isSymmetric(sequence)){
			//symmetry correction?
			totalEntropy += 14D;
			totalEnthalpy = 40;
			adjustedMolarConcentration = nM/1000000000D;
		}else{
			totalEnthalpy = 0D;
			totalEntropy += 108D;
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
		totalEnthalpy -= getTerminalBasePenalty(previous);
		//enthalpy lookups are in 100cal/mol
		//need to convert them
		totalEnthalpy *=-100D;
		//entropy lookups are in .1` cal/K/mol
		//need to convert them
		totalEntropy *=-0.1D;
		
		
		double tempInKelvin= totalEnthalpy / (totalEntropy + ( R * Math.log(adjustedMolarConcentration)));
		return tempInKelvin - 273.15D;
	
	}
	protected double getTerminalBasePenalty(Nucleotide terminalBase) {
		return 0D;
	}
	protected double getInitialEntropyFor(NucleotideSequence sequence){
		return 0D;
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
	
	
	private static final class LookupTable{
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
