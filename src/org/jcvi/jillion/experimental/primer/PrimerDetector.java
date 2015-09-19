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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.align.NucleotideSubstitutionMatrix;
import org.jcvi.jillion.align.NucleotideSubstitutionMatrixBuilder;
import org.jcvi.jillion.align.pairwise.NucleotidePairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.PairwiseAlignmentBuilder;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
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
   // private int gapOpenPenalty=-2;
    private int gapOpenPenalty=-17;
    private int gapExtendPenalty=-5;
    /*
    private static final NucleotideSubstitutionMatrix MATRIX = new NucleotideSubstitutionMatrixBuilder(-1)
																.setMatch(4)
																.ambiguityScore(2)
																.build();
*/
    private static final NucleotideSubstitutionMatrix MATRIX = new NucleotideSubstitutionMatrixBuilder(-14)
								.setMatch(5)
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
    	
    	if(sequence.getLength() ==0){
    		//obviously an empty sequence can't have any hits
    		return Collections.emptyList();
    	}
    	
        List<DirectedRange> ranges = new ArrayList<DirectedRange>();
        StreamingIterator<NucleotideSequence> iter =null; 
        try{
        	iter =primersDataStore.iterator();
        while(iter.hasNext()){
        	NucleotideSequence primer = iter.next();
            if(primer.getLength()>=minLength){
               
            	NucleotidePairwiseSequenceAlignment forwardAlignment =PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(primer, sequence, MATRIX)
																					.gapPenalty(gapOpenPenalty, gapExtendPenalty)
																					.build();
            	
                final NucleotidePairwiseSequenceAlignment reverseAlignment;
                if(alsoCheckReverseCompliment){
                	NucleotideSequence reversePrimer = new NucleotideSequenceBuilder(primer)
												.reverseComplement()
												.build();
					reverseAlignment =  PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(reversePrimer, sequence, MATRIX)
																.gapPenalty(gapOpenPenalty, gapExtendPenalty)
																.build();
                }else{
                    reverseAlignment = NullAlignment.INSTANCE;
                }
                if(maxNumMismatches ==null){
                	boolean forwardValid = forwardAlignment.getPercentIdentity() > minPercentIdentity
                					&& forwardAlignment.getAlignmentLength() >= minLength;
					
                	boolean reverseValid = reverseAlignment.getPercentIdentity() > minPercentIdentity
        					&& reverseAlignment.getAlignmentLength() >= minLength;	
        					
      	           if(forwardValid || reverseValid){
      	        	  final Direction direction;
	                   final NucleotidePairwiseSequenceAlignment bestAlignment;
		               if(forwardValid && !reverseValid){
		            	   bestAlignment = forwardAlignment;
	                   		direction = Direction.FORWARD;
		               }else if(!forwardValid && reverseValid){
		            	   bestAlignment = reverseAlignment;
	                   	direction = Direction.REVERSE;
		               }else{
		            	   //forward AND reverse both valid
		                    if(reverseAlignment.getScore() > forwardAlignment.getScore()){
		                    	bestAlignment = reverseAlignment;
		                    	direction = Direction.REVERSE;
		                    }else{
		                    	bestAlignment = forwardAlignment;
		                    	direction = Direction.FORWARD;
		                    }	                	
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
            NucleotideFastaDataStore primersDataStore) {
        List<PrimerHit> hits = new ArrayList<PrimerHit>();
        StreamingIterator<NucleotideFastaRecord> iter =null; 
        try{
        	iter =primersDataStore.iterator();
        while(iter.hasNext()){
        	NucleotideFastaRecord fasta = iter.next();
        	NucleotideSequence primer = fasta.getSequence();
            if(primer.getLength()>=minLength){

            	NucleotidePairwiseSequenceAlignment forwardAlignment =PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(primer, sequence, MATRIX)
																		.gapPenalty(gapOpenPenalty, -1)
																		.build();

                final NucleotidePairwiseSequenceAlignment reverseAlignment;
                if(alsoCheckReverseCompliment){
                	NucleotideSequence reversePrimer = new NucleotideSequenceBuilder(primer)
												.reverseComplement()
												.build();
					
					reverseAlignment =PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(reversePrimer, sequence, MATRIX)
							.gapPenalty(gapOpenPenalty, -1)
							.build();

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
