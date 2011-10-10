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
	H3(4){		
		
		
			
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
	H5(6){
		private int exonPhase=0;

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
				array[i] = 2 + exonPhase%3;
				exonPhase++;
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
	H27(28){
		private int exonPhase=0;
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
				array[i] = 5 + exonPhase%3;
				exonPhase++;
			}			
			return array;
		}
		@Override
		protected int[] visitFinalExon(List<Nucleotide> exonBases) {
			int[] array = new int[exonBases.size()];
			for(int i=0; i<exonBases.size()-3; i++){
				array[i] = 5 + exonPhase%3;
				exonPhase++;
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
			exonPhase=0;
			for(int i=3; i<exonBases.size(); i++){
				array[i] = 5 + exonPhase%3;
				exonPhase++;
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
			if(exonPhase ==0){
				//special case to avoid negative shifts
				phaseShift=10;
			}else{
				phaseShift= 5*((exonPhase-1)%3);
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
	private final int numberOfStates;
	HmmModels(int numberOfStates){
		this.numberOfStates = numberOfStates;
	}

	public int getNumberOfStates() {
		return numberOfStates;
	}
	public final LabeledSequence computeLabeledSequence(Sequence<Nucleotide> sequence, List<Gene> genes){
	    int[] labels = labelSequence(sequence, genes);
	    List<Integer> path = new ArrayList<Integer>(labels.length+2);
	    path.add(Integer.valueOf(0)); //initial state
	    for(int i=0; i< labels.length; i++){
	        path.add(labels[i]);
	    }
	    path.add(Integer.valueOf(0)); //final state
	    return new LabeledSequence(sequence, path);
	}
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
				int[] exonStates = visitOnlyExon(sequence.asList(exon));
				System.arraycopy(exonStates, 0, labels, (int)exon.getStart(), exonStates.length);
			}else{
				Range exon = exons.get(0);
				int[] exonStates = visitInitialExon(sequence.asList(exon));
				System.arraycopy(exonStates, 0, labels, (int)exon.getStart(), exonStates.length);
				if(introns.hasNext()){
					Range intron = introns.next();
					int[] intronStates = visitIntron(sequence.asList(intron));
					System.arraycopy(intronStates, 0, labels, (int)intron.getStart(), intronStates.length);
				}
				for(int i=1; i< exons.size()-1; i++){
					exon = exons.get(i);
					int[] middleExonStates = visitMiddleExon(sequence.asList(exon));
					System.arraycopy(middleExonStates, 0, labels, (int)exon.getStart(), middleExonStates.length);
					if(introns.hasNext()){
						Range intron = introns.next();
						int[] intronStates = visitIntron(sequence.asList(intron));
						System.arraycopy(intronStates, 0, labels, (int)intron.getStart(), intronStates.length);
					}
				}
				Range finalExon = exons.get(exons.size()-1);
				int[] finalExonStates = visitFinalExon(sequence.asList(finalExon));
				System.arraycopy(finalExonStates, 0, labels, (int)finalExon.getStart(), finalExonStates.length);
				
			}
					
		}
		return labels;
	}

	

	protected abstract int[] visitFinalExon(List<Nucleotide> exonBases);

	protected abstract  int[] visitMiddleExon(List<Nucleotide> exonBases);

	protected abstract  int[] visitIntron(List<Nucleotide> exonBases);

	protected abstract int[] visitInitialExon(List<Nucleotide> exonBases);
	protected abstract int[] visitOnlyExon(List<Nucleotide> exonBases);
	

}
