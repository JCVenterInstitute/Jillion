package org.jcvi.common.annotation.hmm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.annotation.Gene;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.aa.Codon;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
/**
 * {@code HmmModels} contain HMM models
 * with differing numbers of states.  The higher the number
 * of states, the better (hopefully) gene prediction results
 * can be obtained.
 * @author dkatzel
 *
 */
public enum HmmModels {
	/**
	 * 4 state model for gene finding.
	 * <ol>
	 * <li> initial state</li>
	 * <li> intergenic state</li>
	 * <li> exon</li>
	 * <li> intron</li>
	 * </ol>
	 */
	H3{	
		@Override
		protected int[] visitOnlyExon(NucleotideSequence exonBases){
			return visitExon(exonBases);
		}
		@Override
		protected int[] visitInitialExon(NucleotideSequence exonBases) {
			return visitExon(exonBases);
		}
		@Override
		protected int[] visitMiddleExon(NucleotideSequence exonBases) {
			return visitExon(exonBases);
		}
		@Override
		protected int[] visitFinalExon(NucleotideSequence exonBases) {
			return visitExon(exonBases);
		}
		private int[] visitExon(NucleotideSequence exonBases) {
			int[] array = new int[(int)exonBases.getLength()];
			Arrays.fill(array, 2);
			return array;
		}
		@Override
		protected int[] visitIntron(NucleotideSequence intronBases) {
			int[] array = new int[(int)intronBases.getLength()];
			Arrays.fill(array, 3);
			return array;
		}
	},
	/**
	 * 6 state Model where each phase of the exon gets
	 * its own state.
	 */
	H5{
		/**
		 * Counts the number of bases seen
		 * in exons so far.  Used to compute the phase.
		 */
		private int numberOfBasesInExonSoFar=0;

		protected int[] visitOnlyExon(NucleotideSequence exonBases){
			return visitExon(exonBases);
		}
		protected int[] visitInitialExon(NucleotideSequence exonBases) {
			return visitExon(exonBases);
		}
		@Override
		protected int[] visitMiddleExon(NucleotideSequence exonBases) {
			return visitExon(exonBases);
		}
		@Override
		protected int[] visitFinalExon(NucleotideSequence exonBases) {
			return visitExon(exonBases);
		}
		
		private int[] visitExon(NucleotideSequence exonBases) {
			int length = (int)exonBases.getLength();
			int[] array = new int[length];
			for(int i=0; i<length; i++){
				array[i] = 2 + numberOfBasesInExonSoFar%3;
				numberOfBasesInExonSoFar++;
			}
			return array;
		}
		@Override
		protected int[] visitIntron(NucleotideSequence intronBases) {
			int[] array = new int[(int)intronBases.getLength()];
			Arrays.fill(array, 5);
			return array;
		}
		
		
		
	},
	/**
	 * A 28 state model that contains 3 separate intron submodels to 
	 * label introns in different frames differently.  Each
	 * intron model also has unique states to label
	 * donor and acceptor sites.
	 */
	H27{
		/**
		 * Counts the number of bases seen
		 * in exons so far.  Used to compute the phase.
		 */
		private int numberOfBasesInExonSoFar=0;
		
		@Override
		protected int[] visitOnlyExon(NucleotideSequence exonBases) {
			int exonLength = (int)exonBases.getLength();
			int[] array = new int[exonLength];
			//start codon
			for(int i=0; i<exonLength && i<3; i++){
				array[i] = i+2;
			}
			for(int i=3; i<exonLength-3; i++){
				array[i] = 5 + i%3;
			}
			labelStopCodon(exonBases, array);
			
			return array;
		}
		private void labelStopCodon(NucleotideSequence exonBases, int[] array) {
			int stopCodonOffset=(int)exonBases.getLength()-3;
			Codon stopCodon = Codon.getCodonByOffset(exonBases, stopCodonOffset);
			if(!stopCodon.isStopCodon()){
				throw new IllegalStateException("invalid stop codon :" + stopCodon);
			}
			List<Nucleotide> codonBases =stopCodon.getNucleotides();
			//stop always starts with T
			array[stopCodonOffset] = 8;
			if(codonBases.get(1) == Nucleotide.Adenine){
				array[stopCodonOffset+1] = 9;
			}else{
				array[stopCodonOffset+1] = 11;
			}
			if(codonBases.get(2) == Nucleotide.Guanine){
				array[stopCodonOffset+2] = 10;
			}else{
				array[stopCodonOffset+2] = 12;
			}
		}
		@Override
		protected  int[] visitMiddleExon(NucleotideSequence exonBases) {
			int exonLength = (int) exonBases.getLength();
			int[] array = new int[exonLength];
			for(int i=0; i<exonLength; i++){
				array[i] = 5 + numberOfBasesInExonSoFar%3;
				numberOfBasesInExonSoFar++;
			}			
			return array;
		}
		@Override
		protected int[] visitFinalExon(NucleotideSequence exonBases) {
			int exonLength = (int) exonBases.getLength();
			int[] array = new int[exonLength];
			for(int i=0; i<exonLength-3; i++){
				array[i] = 5 + numberOfBasesInExonSoFar%3;
				numberOfBasesInExonSoFar++;
			}
			labelStopCodon(exonBases, array);
			return array;
		}
		@Override
		protected int[] visitInitialExon(NucleotideSequence exonBases) {
			int exonLength = (int) exonBases.getLength();
			int[] array = new int[exonLength];
			//start codon
			for(int i=0; i<exonLength && i<3; i++){
				array[i] = i+2;
			}
			numberOfBasesInExonSoFar=0;
			for(int i=3; i<exonLength; i++){
				array[i] = 5 + numberOfBasesInExonSoFar%3;
				numberOfBasesInExonSoFar++;
			}
			return array;
		}
		@Override
		protected int[] visitIntron(NucleotideSequence intronBases) {
			int intronLength = (int) intronBases.getLength();
			int[] array = new int[intronLength];
			//each phase has it's own intron sub-model
			//of 5 states each
			//use exonPhase -1 so that when we get back
			//into an exon we are in the correct phase.
			final int phaseShift;
			if(numberOfBasesInExonSoFar ==0){
				//special case to avoid negative shifts
				phaseShift=10;
			}else{
				phaseShift= 5*((numberOfBasesInExonSoFar-1)%3);
			}
			Arrays.fill(array, 15+phaseShift);
			//first bases are GT
			if(intronBases.get(0)!= Nucleotide.Guanine 
					|| intronBases.get(1)!= Nucleotide.Thymine ){
				throw new IllegalStateException("intron must start with 'GT' : "+ 
					intronBases.get(0)+ intronBases.get(1));
			}
			array[0] = 13+phaseShift;
			array[1] = 14+phaseShift;
			//last bases are AG
			int size = intronLength;
			if(intronBases.get(size-2)!= Nucleotide.Adenine 
					|| intronBases.get(size-1)!= Nucleotide.Guanine ){
				throw new IllegalStateException("intron must start with 'AG' : "+ 
						intronBases.get(size-2)+
						intronBases.get(size-1));
			}
			array[array.length-2] = 16+phaseShift;
			array[array.length-1] = 17+phaseShift;
			
			
			return array;
		}
	}
	;
	/**
	 * This gene only contain 1 exons (and therefore, no introns).
	 * This exon should start with a start codon and end with a stop codon. 
	 * @param exonBases the bases of this exon
	 * @return an int array of the state labels traversed. The ith
	 * index in the array should correspond to the ith index
	 * in the List of exonBases.  Will never be null.
	 */
	protected abstract int[] visitOnlyExon(NucleotideSequence exonBases);
	/**
	 * Visit the first exon of a gene that will contain multiple exons.
	 * This exon should start with a start codon but will not end 
	 * with a stop codon. 
	 * @param exonBases the bases of this exon
	 * @return an int array of the state labels traversed. The ith
	 * index in the array should correspond to the ith index
	 * in the List of exonBases.  Will never be null.
	 */
	protected abstract int[] visitInitialExon(NucleotideSequence exonBases);	
	/**
	 * Visit the an internal exon of a gene that contains multiple exons.
	 * This exon should neither start with a start codon nor end 
	 * with a stop codon. 
	 * @param exonBases the bases of this exon
	 * @return an int array of the state labels traversed. The ith
	 * index in the array should correspond to the ith index
	 * in the List of exonBases.  Will never be null.
	 */
	protected abstract  int[] visitMiddleExon(NucleotideSequence exonBases);
	/**
	 * Visit the last exon of a gene that contains multiple exons.
	 * This exon will not start with a start codon but will end 
	 * with a stop codon. 
	 * @param exonBases the bases of this exon
	 * @return an int array of the state labels traversed. The ith
	 * index in the array should correspond to the ith index
	 * in the List of exonBases.  Will never be null.
	 */
	protected abstract int[] visitFinalExon(NucleotideSequence exonBases);
	/**
	 * Visit an intron.
	 * This intron should start with a start donor site (GT) and end 
	 * with an acceptor site (AG).
	 * @param intronBases the bases of this intron
	 * @return an int array of the state labels traversed. The ith
	 * index in the array should correspond to the ith index
	 * in the List of intronBases.  Will never be null.
	 */
	protected abstract  int[] visitIntron(NucleotideSequence intronBases);
	
	/**
	 * Compute the {@link LabeledSequence} including initial and final states q0
	 * for the given Sequence and corresponding genes.
	 * @param sequence the nucleotide sequence to use.
	 * @param genes the corresponding {@link Gene} which contains
	 * intron and exon coordinates mapped to the sequence.
	 * @return a new LabeledSequence instance containing
	 * the path starting at the
	 * initial state 0, traversing through the HmmModel 
	 * and ending in the terminal state 0.
	 */
	public final LabeledSequence computeLabeledSequence(
								NucleotideSequence sequence, List<Gene> genes){
	    int[] labels = labelSequence(sequence, genes);
	    List<Integer> path = new ArrayList<Integer>(labels.length+2);
	    path.add(Integer.valueOf(0)); //initial state
	    for(int i=0; i< labels.length; i++){
	        path.add(labels[i]);
	    }
	    path.add(Integer.valueOf(0)); //final state
	    return new LabeledSequence(sequence, path);
	}
	
	/**
	 * Same as {@link #computeLabeledSequence(Sequence, List)} but return only the path
	 * through the model as an int array and do not include the initial and terminal states.
	 * @param sequence
	 * @param genes
	 * @return
	 */
	final int[] labelSequence(NucleotideSequence sequence, List<Gene> genes){
		int[] labels = new int[(int)sequence.getLength()];
		//initialize to intergenic state
		Arrays.fill(labels, 1);
		
		for(Gene gene : genes){
			List<Range> exons = gene.getExons();
			Iterator<Range> introns = gene.getIntrons().iterator();
			if(exons.isEmpty()){
				//not sure how a gene can't have any exons
				//but skip whole gene if it happens...
				continue;
			}
			if(exons.size() ==1){
				//only 1 exon need to include both start and stop codons
				Range exon = exons.get(0);
				labels = handleSingleExon(sequence, labels, exon);
			}else{
				Range initialExon = exons.get(0);
				labels = handleInitialExon(sequence, labels, initialExon);
				labels =handleIntron(sequence, labels, introns.next());
				
				for(int i=1; i< exons.size()-1; i++){
					Range middleExon = exons.get(i);
					labels =handleMiddleExon(sequence, labels, middleExon);
					handleIntron(sequence, labels, introns.next());
				}
				Range finalExon = exons.get(exons.size()-1);
				labels =handleFinalExon(sequence, labels, finalExon);				
			}
					
		}
		return labels;
	}

	private int[] handleFinalExon(NucleotideSequence sequence, int[] labels, Range finalExon) {
		int[] finalExonStates = visitFinalExon(new NucleotideSequenceBuilder(sequence)
														.trim(finalExon)
														.build());
		System.arraycopy(finalExonStates, 0, labels, (int)finalExon.getBegin(), finalExonStates.length);
		return labels;
	}

	private int[] handleMiddleExon(NucleotideSequence sequence, int[] labels, Range middleExon) {
		int[] middleExonStates = visitMiddleExon(new NucleotideSequenceBuilder(sequence)
													.trim(middleExon)
													.build());
		System.arraycopy(middleExonStates, 0, labels, (int)middleExon.getBegin(), middleExonStates.length);
		return labels;
	}

	private int[] handleInitialExon(NucleotideSequence sequence, int[] labels, Range initialExon) {
		int[] exonStates = visitInitialExon(new NucleotideSequenceBuilder(sequence)
											.trim(initialExon)
											.build());
		System.arraycopy(exonStates, 0, labels, (int)initialExon.getBegin(), exonStates.length);
		return labels;
	}

	private int[] handleIntron(NucleotideSequence sequence, int[] labels,	Range intron) {
		int[] intronStates = visitIntron(new NucleotideSequenceBuilder(sequence)
										.trim(intron)
										.build());
		System.arraycopy(intronStates, 0, labels, (int)intron.getBegin(), intronStates.length);
		return labels;
	}

	private int[] handleSingleExon(NucleotideSequence sequence, int[] labels,
			Range exon) {
		int[] exonStates = visitOnlyExon(new NucleotideSequenceBuilder(sequence)
										.trim(exon)
										.build());
		System.arraycopy(exonStates, 0, labels, (int)exon.getBegin(), exonStates.length);
		return labels;
	}
}
