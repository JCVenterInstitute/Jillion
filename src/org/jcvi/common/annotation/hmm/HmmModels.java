package org.jcvi.common.annotation.hmm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.annotation.Gene;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.aa.Codon;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
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
		protected int[] visitOnlyExon(List<Nucleotide> exonBases){
			return visitExon(exonBases);
		}
		@Override
		protected int[] visitInitialExon(List<Nucleotide> exonBases) {
			return visitExon(exonBases);
		}
		@Override
		protected int[] visitMiddleExon(List<Nucleotide> exonBases) {
			return visitExon(exonBases);
		}
		@Override
		protected int[] visitFinalExon(List<Nucleotide> exonBases) {
			return visitExon(exonBases);
		}
		private int[] visitExon(List<Nucleotide> exonBases) {
			int[] array = new int[exonBases.size()];
			Arrays.fill(array, 2);
			return array;
		}
		@Override
		protected int[] visitIntron(List<Nucleotide> intronBases) {
			int[] array = new int[intronBases.size()];
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

		protected int[] visitOnlyExon(List<Nucleotide> exonBases){
			return visitExon(exonBases);
		}
		protected int[] visitInitialExon(List<Nucleotide> exonBases) {
			return visitExon(exonBases);
		}
		@Override
		protected int[] visitMiddleExon(List<Nucleotide> exonBases) {
			return visitExon(exonBases);
		}
		@Override
		protected int[] visitFinalExon(List<Nucleotide> exonBases) {
			return visitExon(exonBases);
		}
		
		private int[] visitExon(List<Nucleotide> exonBases) {
			int[] array = new int[exonBases.size()];
			for(int i=0; i<exonBases.size(); i++){
				array[i] = 2 + numberOfBasesInExonSoFar%3;
				numberOfBasesInExonSoFar++;
			}
			return array;
		}
		@Override
		protected int[] visitIntron(List<Nucleotide> intronBases) {
			int[] array = new int[intronBases.size()];
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
		protected int[] visitOnlyExon(List<Nucleotide> exonBases) {
			int[] array = new int[exonBases.size()];
			//start codon
			for(int i=0; i<exonBases.size() && i<3; i++){
				array[i] = i+2;
			}
			for(int i=3; i<exonBases.size()-3; i++){
				array[i] = 5 + i%3;
			}
			labelStopCodon(exonBases, array);
			
			return array;
		}
		private void labelStopCodon(List<Nucleotide> exonBases, int[] array) {
			int stopCodonOffset=exonBases.size()-3;
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
		protected  int[] visitMiddleExon(List<Nucleotide> exonBases) {
			int[] array = new int[exonBases.size()];
			for(int i=0; i<exonBases.size(); i++){
				array[i] = 5 + numberOfBasesInExonSoFar%3;
				numberOfBasesInExonSoFar++;
			}			
			return array;
		}
		@Override
		protected int[] visitFinalExon(List<Nucleotide> exonBases) {
			int[] array = new int[exonBases.size()];
			for(int i=0; i<exonBases.size()-3; i++){
				array[i] = 5 + numberOfBasesInExonSoFar%3;
				numberOfBasesInExonSoFar++;
			}
			labelStopCodon(exonBases, array);
			return array;
		}
		@Override
		protected int[] visitInitialExon(List<Nucleotide> exonBases) {
			int[] array = new int[exonBases.size()];
			//start codon
			for(int i=0; i<exonBases.size() && i<3; i++){
				array[i] = i+2;
			}
			numberOfBasesInExonSoFar=0;
			for(int i=3; i<exonBases.size(); i++){
				array[i] = 5 + numberOfBasesInExonSoFar%3;
				numberOfBasesInExonSoFar++;
			}
			return array;
		}
		@Override
		protected int[] visitIntron(List<Nucleotide> intronBases) {
			int[] array = new int[intronBases.size()];
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
				throw new IllegalStateException("intron must start with 'GT' : "+ intronBases.subList(0, 2));
			}
			array[0] = 13+phaseShift;
			array[1] = 14+phaseShift;
			//last bases are AG
			int size = intronBases.size();
			if(intronBases.get(size-2)!= Nucleotide.Adenine 
					|| intronBases.get(size-1)!= Nucleotide.Guanine ){
				throw new IllegalStateException("intron must start with 'AG' : "+ intronBases.subList(0, 2));
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
	protected abstract int[] visitOnlyExon(List<Nucleotide> exonBases);
	/**
	 * Visit the first exon of a gene that will contain multiple exons.
	 * This exon should start with a start codon but will not end 
	 * with a stop codon. 
	 * @param exonBases the bases of this exon
	 * @return an int array of the state labels traversed. The ith
	 * index in the array should correspond to the ith index
	 * in the List of exonBases.  Will never be null.
	 */
	protected abstract int[] visitInitialExon(List<Nucleotide> exonBases);	
	/**
	 * Visit the an internal exon of a gene that contains multiple exons.
	 * This exon should neither start with a start codon nor end 
	 * with a stop codon. 
	 * @param exonBases the bases of this exon
	 * @return an int array of the state labels traversed. The ith
	 * index in the array should correspond to the ith index
	 * in the List of exonBases.  Will never be null.
	 */
	protected abstract  int[] visitMiddleExon(List<Nucleotide> exonBases);
	/**
	 * Visit the last exon of a gene that contains multiple exons.
	 * This exon will not start with a start codon but will end 
	 * with a stop codon. 
	 * @param exonBases the bases of this exon
	 * @return an int array of the state labels traversed. The ith
	 * index in the array should correspond to the ith index
	 * in the List of exonBases.  Will never be null.
	 */
	protected abstract int[] visitFinalExon(List<Nucleotide> exonBases);
	/**
	 * Visit an intron.
	 * This intron should start with a start donor site (GT) and end 
	 * with an acceptor site (AG).
	 * @param intronBases the bases of this intron
	 * @return an int array of the state labels traversed. The ith
	 * index in the array should correspond to the ith index
	 * in the List of intronBases.  Will never be null.
	 */
	protected abstract  int[] visitIntron(List<Nucleotide> intronBases);
	
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
								Sequence<Nucleotide> sequence, List<Gene> genes){
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
	final int[] labelSequence(Sequence<Nucleotide> sequence, List<Gene> genes){
		List<Nucleotide> sequenceAsList = sequence.asList();
		int[] labels = new int[sequenceAsList.size()];
		//initialize to intergenic state
		Arrays.fill(labels, 1);
		
		for(Gene gene : genes){
			List<Range> exons = gene.getExons();
			Iterator<Range> introns = gene.getIntrons().iterator();
			if(exons.size() ==0){
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

	private int[] handleFinalExon(Sequence<Nucleotide> sequence, int[] labels, Range finalExon) {
		int[] finalExonStates = visitFinalExon(sequence.asList(finalExon));
		System.arraycopy(finalExonStates, 0, labels, (int)finalExon.getBegin(), finalExonStates.length);
		return labels;
	}

	private int[] handleMiddleExon(Sequence<Nucleotide> sequence, int[] labels, Range middleExon) {
		int[] middleExonStates = visitMiddleExon(sequence.asList(middleExon));
		System.arraycopy(middleExonStates, 0, labels, (int)middleExon.getBegin(), middleExonStates.length);
		return labels;
	}

	private int[] handleInitialExon(Sequence<Nucleotide> sequence, int[] labels, Range initialExon) {
		int[] exonStates = visitInitialExon(sequence.asList(initialExon));
		System.arraycopy(exonStates, 0, labels, (int)initialExon.getBegin(), exonStates.length);
		return labels;
	}

	private int[] handleIntron(Sequence<Nucleotide> sequence, int[] labels,	Range intron) {
		int[] intronStates = visitIntron(sequence.asList(intron));
		System.arraycopy(intronStates, 0, labels, (int)intron.getBegin(), intronStates.length);
		return labels;
	}

	private int[] handleSingleExon(Sequence<Nucleotide> sequence, int[] labels,
			Range exon) {
		int[] exonStates = visitOnlyExon(sequence.asList(exon));
		System.arraycopy(exonStates, 0, labels, (int)exon.getBegin(), exonStates.length);
		return labels;
	}
}
