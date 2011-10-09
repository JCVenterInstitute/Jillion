package org.jcvi.common.annotation.hmm;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.annotation.Gene;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.Sequence;
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
		protected int getNextExonState() {
			return 2;			
		}
		@Override
		protected int getNextIntronState() {
			return 3;
		}
		@Override
		protected void newGene() {
			//no-op			
		}		
	},
	/**
	 * 6 state Model where each phase of the exon gets
	 * its own state.
	 */
	H5(6){
		private int currentExonPhase=0;
		@Override
		protected void newGene() {
			//no-op			
		}

		@Override
		protected int getNextExonState() {
			int next = 2+currentExonPhase%3;
			currentExonPhase++;
			return next;
		}

		@Override
		protected int getNextIntronState() {
			return 5;
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
	public final int[] labelSequence(Sequence<Nucleotide> sequence, List<Gene> genes){
		List<Nucleotide> sequenceAsList = sequence.asList();
		int[] labels = new int[sequenceAsList.size()];
		//initialize to intergenic state
		Arrays.fill(labels, 1);
		for(Gene gene : genes){
			for(Range exon : gene.getExons()){
				for(long coordinate : exon){
					labels[(int)coordinate] =getNextExonState();
				}
			}
			for(Range intron : gene.getIntrons()){
				for(long coordinate : intron){
					labels[(int)coordinate] =getNextIntronState();
				}
			}			
		}
		return labels;
	}
	protected abstract void newGene();
	protected abstract int getNextExonState();
	protected abstract int getNextIntronState();

}
