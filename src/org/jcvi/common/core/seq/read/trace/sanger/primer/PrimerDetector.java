package org.jcvi.common.core.seq.read.trace.sanger.primer;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.align.pairwise.DefaultNucleotideScoringMatrix;
import org.jcvi.common.core.align.pairwise.NucleotidePairwiseSequenceAlignment;
import org.jcvi.common.core.align.pairwise.NucleotideSmithWatermanAligner;
import org.jcvi.common.core.align.pairwise.ScoringMatrix;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code PrimerDetector} scans a {@link NucleotideSequence}
 * against a {@link NucleotideSequenceDataStore} of primer/vector sequences
 * to find all primer hits.
 * @author dkatzel
 *
 */
public class PrimerDetector {
	
	
   
    
    private final int minLength;
    private final double minPercentIdentity;
    private final boolean alsoCheckReverseCompliment;
    private final Integer maxNumMismatches;
    private int gapOpenPenalty=-200;
    
    private static final ScoringMatrix<Nucleotide> MATRIX = new DefaultNucleotideScoringMatrix.Builder(0)
	.setMatch(4)
	.ambiguityScore(2)
	.build();

    /**
     * @param minLength
     * @param minPercentIdentity
     */
    public PrimerDetector(int minLength, double minPercentIdentity) {
        this(minLength, minPercentIdentity, true);
    }
    
    private PrimerDetector(int minLength, int maxAllowedMismatches,
    		boolean alsoCheckReverseCompliment,
    		int gapOpenPenalty) {
    	this.minLength = minLength;
        this.minPercentIdentity = 0D;
        this.alsoCheckReverseCompliment = alsoCheckReverseCompliment;
        this.maxNumMismatches = maxAllowedMismatches;
        this.gapOpenPenalty = gapOpenPenalty;
    }
    public PrimerDetector(int minLength, double minPercentIdentity, boolean alsoCheckReverseCompliment) {
        this.minLength = minLength;
        this.minPercentIdentity = minPercentIdentity;
        this.alsoCheckReverseCompliment = alsoCheckReverseCompliment;
        this.maxNumMismatches = null;
    }

    public List<DirectedRange> detect(NucleotideSequence sequence,
            NucleotideSequenceDataStore primersDataStore) {
        List<DirectedRange> ranges = new ArrayList<DirectedRange>();
        StreamingIterator<NucleotideSequence> iter =null; 
        try{
        	iter =primersDataStore.iterator();
        while(iter.hasNext()){
        	NucleotideSequence primer = iter.next();
            if(primer.getLength()>=minLength){
            	NucleotidePairwiseSequenceAlignment forwardAlignment = NucleotideSmithWatermanAligner.align(primer, sequence, 
            					MATRIX, gapOpenPenalty, -1);
               
                final NucleotidePairwiseSequenceAlignment reverseAlignment;
                if(alsoCheckReverseCompliment){
                	NucleotideSequence reversePrimer = new NucleotideSequenceBuilder(primer)
												.reverseComplement()
												.build();
					reverseAlignment = NucleotideSmithWatermanAligner.align(
							reversePrimer,sequence,
                			 MATRIX, gapOpenPenalty, -1);
                }else{
                    reverseAlignment = NullAlignment.INSTANCE;
                }
                if(maxNumMismatches ==null){
	                if(forwardAlignment.getPercentIdentity() > minPercentIdentity || reverseAlignment.getPercentIdentity() > minPercentIdentity){
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
                }else{
                	int maxAllowedMismatches = maxNumMismatches;
                	boolean forwardIsCandidate = forwardAlignment.getAlignmentLength() >= minLength && forwardAlignment.getNumberOfMismatches() <= maxAllowedMismatches;
                	boolean reverseIsCandidate = reverseAlignment.getAlignmentLength() >= minLength && reverseAlignment.getNumberOfMismatches() <= maxAllowedMismatches;
                	
                	if(forwardIsCandidate && reverseIsCandidate){
                		if(reverseAlignment.getScore() > forwardAlignment.getScore()){
                			DirectedRange range = DirectedRange.create(
	                    			reverseAlignment.getSubjectRange().asRange(), Direction.REVERSE);
		                    ranges.add(range);
                		}else{
                			DirectedRange range = DirectedRange.create(
	                    			forwardAlignment.getSubjectRange().asRange(), Direction.FORWARD);
		                    ranges.add(range);
                		}
                	}else if(forwardIsCandidate){
                		DirectedRange range = DirectedRange.create(
                    			forwardAlignment.getSubjectRange().asRange(), Direction.FORWARD);
	                    ranges.add(range);
                	}else if(reverseIsCandidate){
                		DirectedRange range = DirectedRange.create(
                    			reverseAlignment.getSubjectRange().asRange(), Direction.REVERSE);
	                    ranges.add(range);
                	}
	                   

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
    
    public List<PrimerHit> detect(NucleotideSequence sequence,
            NucleotideSequenceFastaDataStore primersDataStore) {
        List<PrimerHit> hits = new ArrayList<PrimerHit>();
        StreamingIterator<NucleotideSequenceFastaRecord> iter =null; 
        try{
        	iter =primersDataStore.iterator();
        while(iter.hasNext()){
        	NucleotideSequenceFastaRecord fasta = iter.next();
        	NucleotideSequence primer = fasta.getSequence();
            if(primer.getLength()>=minLength){
            	NucleotidePairwiseSequenceAlignment forwardAlignment = NucleotideSmithWatermanAligner.align(primer, sequence, 
            					MATRIX, gapOpenPenalty, -1);
               
                final NucleotidePairwiseSequenceAlignment reverseAlignment;
                if(alsoCheckReverseCompliment){
                	NucleotideSequence reversePrimer = new NucleotideSequenceBuilder(primer)
												.reverseComplement()
												.build();
					reverseAlignment = NucleotideSmithWatermanAligner.align(
							reversePrimer,sequence,
                			 MATRIX, gapOpenPenalty, -1);
                }else{
                    reverseAlignment = NullAlignment.INSTANCE;
                }
                if(maxNumMismatches ==null){
                	
	                if(forwardAlignment.getPercentIdentity() > minPercentIdentity || reverseAlignment.getPercentIdentity() > minPercentIdentity){
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
	                    hits.add(new PrimerHit(fasta.getId(), range));
	                }
                }else{
                	int maxAllowedMismatches = maxNumMismatches;
                	int numberOfMissingForwardBases = Math.max(0, minLength - forwardAlignment.getAlignmentLength());
                	int numberOfMissingReverseBases = Math.max(0, minLength - reverseAlignment.getAlignmentLength());
                	
                	int numberOfForwardMismatchesAndMissingBases = forwardAlignment.getNumberOfMismatches() + numberOfMissingForwardBases;
                	int numberOfReverseMismatchesAndMissingBases = reverseAlignment.getNumberOfMismatches() + numberOfMissingReverseBases;
                	
                	boolean forwardIsCandidate =  numberOfForwardMismatchesAndMissingBases <= maxAllowedMismatches;
                	boolean reverseIsCandidate = numberOfReverseMismatchesAndMissingBases <= maxAllowedMismatches;
                	
                	if(forwardIsCandidate && reverseIsCandidate){
                		if(reverseAlignment.getScore() > forwardAlignment.getScore()){
                			DirectedRange range = DirectedRange.create(
	                    			new Range.Builder(reverseAlignment.getSubjectRange().asRange())
	                    			.expandBegin(numberOfMissingReverseBases)
	                    			.build(), 
	                    			
	                    			Direction.REVERSE);
                			hits.add(new PrimerHit(fasta.getId(), range));
                		}else{
                			DirectedRange range = DirectedRange.create(
                					new Range.Builder(forwardAlignment.getSubjectRange().asRange())
                					.expandEnd(numberOfMissingForwardBases)
                					.build(), 
                					Direction.FORWARD);
                			hits.add(new PrimerHit(fasta.getId(), range));
                		}
                	}else if(forwardIsCandidate){
                		DirectedRange range = DirectedRange.create(
                				new Range.Builder(forwardAlignment.getSubjectRange().asRange())
                				.expandEnd(numberOfMissingForwardBases)
                				.build(),
                				Direction.FORWARD);
                		hits.add(new PrimerHit(fasta.getId(), range));
                	}else if(reverseIsCandidate){
                		DirectedRange range = DirectedRange.create(
                				new Range.Builder(reverseAlignment.getSubjectRange().asRange())
                				.expandBegin(numberOfMissingReverseBases)
                				.build(), Direction.REVERSE);
                		hits.add(new PrimerHit(fasta.getId(), range));
                	}
	                   

                }
            }
        }
        return hits;
        } catch (DataStoreException e) {
			throw new IllegalStateException("error iterating over nucleotide sequences",e);
		}finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
    }

   

	public static final class PrimerHit{
    	private final String id;
    	private final DirectedRange directedRange;
    	
		private PrimerHit(String id, DirectedRange directedRange) {
			this.id = id;
			this.directedRange = directedRange;
		}

		public String getId() {
			return id;
		}

		public DirectedRange getDirectedRange() {
			return directedRange;
		}

		@Override
		public String toString() {
			return "PrimerHit [id=" + id + ", directedRange=" + directedRange
					+ "]";
		}
    }
	
	public static PrimerDetector create(int minLength, int maxAllowedMismatches){
		return new PrimerDetector(minLength, maxAllowedMismatches, true, -200);
	}
	/**
	 * {@code NullAlignment} is a Null Object singleton
	 * implementation of a {@link NucleotidePairwiseSequenceAlignment}
	 * when we want to represent that no alignment exist.
	 * @author dkatzel
	 *
	 */
    private enum  NullAlignment implements NucleotidePairwiseSequenceAlignment{

    	INSTANCE
    	;
		@Override
		public float getScore() {
			return 0;
		}

		@Override
		public double getPercentIdentity() {
			return 0;
		}

		@Override
		public int getAlignmentLength() {
			return 0;
		}

		@Override
		public int getNumberOfMismatches() {
			return 0;
		}

		@Override
		public int getNumberOfGapOpenings() {
			return 0;
		}

		@Override
		public NucleotideSequence getGappedQueryAlignment() {
			return null;
		}

		@Override
		public NucleotideSequence getGappedSubjectAlignment() {
			return null;
		}

		@Override
		public DirectedRange getQueryRange() {
			return null;
		}

		@Override
		public DirectedRange getSubjectRange() {
			return null;
		}
    	
    };
}
