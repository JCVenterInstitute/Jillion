package org.jcvi.common.core.seq.trim;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.align.pairwise.DefaultNucleotideScoringMatrix;
import org.jcvi.common.core.align.pairwise.NucleotidePairwiseSequenceAlignment;
import org.jcvi.common.core.align.pairwise.NucleotideSmithWatermanAligner;
import org.jcvi.common.core.align.pairwise.ScoringMatrix;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code PrimerDetector} scans a {@link NucleotideSequence}
 * against a {@link NucleotideDataStore} of primer/vector sequences
 * to find all primer hits.
 * @author dkatzel
 *
 */
public class PrimerDetector {
    private static final NucleotidePairwiseSequenceAlignment NULL_ALIGNMENT_OBJECT = new NucleotidePairwiseSequenceAlignment(){

		@Override
		public float getScore() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double getPercentIdentity() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getAlignmentLength() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getNumberOfMismatches() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getNumberOfGapOpenings() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public NucleotideSequence getGappedQueryAlignment() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public NucleotideSequence getGappedSubjectAlignment() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DirectedRange getQueryRange() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DirectedRange getSubjectRange() {
			// TODO Auto-generated method stub
			return null;
		}
    	
    };
    private static final ScoringMatrix<Nucleotide> MATRIX = new DefaultNucleotideScoringMatrix.Builder(-4)
    										.setMatch(4)
    										.ambiguityScore(2)
    										.build();

    
    private final int minLength;
    private final double minMatch;
    private final boolean alsoCheckReverseCompliment;
    /**
     * @param minLength
     * @param minMatch
     */
    public PrimerDetector(int minLength, double minMatch) {
        this(minLength, minMatch, true);
    }
    public PrimerDetector(int minLength, double minMatch, boolean alsoCheckReverseCompliment) {
        this.minLength = minLength;
        this.minMatch = minMatch;
        this.alsoCheckReverseCompliment = alsoCheckReverseCompliment;
    }

    public List<DirectedRange> detect(NucleotideSequence sequence,
            NucleotideDataStore primersDataStore) {
        List<DirectedRange> ranges = new ArrayList<DirectedRange>();
        CloseableIterator<NucleotideSequence> iter =null; 
        try{
        	iter =primersDataStore.iterator();
        while(iter.hasNext()){
        	NucleotideSequence primer = iter.next();
            if(primer.getLength()>=minLength){
            	NucleotidePairwiseSequenceAlignment forwardAlignment = NucleotideSmithWatermanAligner.align(primer, sequence, 
            					MATRIX, -2, -1);
               
                final NucleotidePairwiseSequenceAlignment reverseAlignment;
                if(alsoCheckReverseCompliment){
                	NucleotideSequence reversePrimer = new NucleotideSequenceBuilder(primer)
												.reverseComplement()
												.build();
					reverseAlignment = NucleotideSmithWatermanAligner.align(
							reversePrimer,sequence,
                			 MATRIX, -2, -1);
                }else{
                    reverseAlignment = NULL_ALIGNMENT_OBJECT;
                }
                
                if(forwardAlignment.getPercentIdentity() > minMatch || reverseAlignment.getPercentIdentity() > minMatch){
                    final Direction direction;
                    final NucleotidePairwiseSequenceAlignment bestAlignment;
                    if(reverseAlignment.getScore() > forwardAlignment.getScore()){
                    	bestAlignment = reverseAlignment;
                    	direction = Direction.REVERSE;
                    }else{
                    	bestAlignment = forwardAlignment;
                    	direction = Direction.FORWARD;
                    }
                	DirectedRange range = DirectedRange.create(
                    		bestAlignment.getSubjectRange().asRange(), direction);
                    ranges.add(range);
                }
            }
        }
        return ranges;
        } catch (DataStoreException e) {
			throw new IllegalStateException("error iterating over nucleotide sequences",e);
		}finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
    }
}
