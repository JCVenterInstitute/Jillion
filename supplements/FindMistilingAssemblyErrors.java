import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.consed.ace.AceAssembledRead;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStoreBuilder;
import org.jcvi.jillion.assembly.util.ContigCoverageMapBuilder;
import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.assembly.util.CoverageMapBuilder;
import org.jcvi.jillion.assembly.util.CoverageRegion;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;


public class FindMistilingAssemblyErrors<P extends AssembledRead, C extends Contig<P>> {

	/**
	 * @param args
	 *  args[0] = path to fasta
	 */
	public static void main(String[] args) throws DataStoreException, IOException {
		File aceFile = new File(args[0]);
		File outputNavFile = new File(args[2]);
		
		FindMistilingAssemblyErrors<AceAssembledRead, AceContig> detector = new FindMistilingAssemblyErrors<AceAssembledRead, AceContig>(
																		DEFAULT_CLUSTER_DISTANCE, 
																		DEFAULT_MIN_ERROR_LENGTH, 
																		DEFAULT_GAP_PERCENTAGE);
		
		StreamingIterator<AceContig> iter =null;
		try{
			iter = new AceFileDataStoreBuilder(aceFile)
									.hint(DataStoreProviderHint.ITERATION_ONLY)
									.build()
									.iterator();
			
			while(iter.hasNext()){
				AceContig contig = iter.next();
				List<Range> errors =detector.findAbacusErrors(contig);
				
			}
		}finally{
			if(iter !=null){
				try {
					iter.close();
				} catch (IOException ignored) {

				}
			}
		}

	}
	public static final double DEFAULT_GAP_PERCENTAGE = .5D;
	public static final int DEFAULT_CLUSTER_DISTANCE = 5;
	public static final int DEFAULT_MIN_ERROR_LENGTH = 3;
	
	private final int clusterDistance;
    private final int minAbacusLength;
    private final double percentGap;
   
    public FindMistilingAssemblyErrors(int clusterDistance, int minAbacusLength,double percentGap){
        this.clusterDistance = clusterDistance;
        this.minAbacusLength = minAbacusLength;
        this.percentGap = percentGap;
    }
    private  List<Range> filterCandidates(C contig, List<Range> ungappedCandidateRanges) {
        CoverageMap<P> coverageMap = new ContigCoverageMapBuilder<P>(contig).build();
        NucleotideSequence consensus = contig.getConsensusSequence();
        List<Range> errorRanges = new ArrayList<Range>(ungappedCandidateRanges.size());
        for(Range ungappedCandidateRange : ungappedCandidateRanges){           
            int gappedStart = consensus.getGappedOffsetFor((int)ungappedCandidateRange.getBegin())+1;
            int gappedEnd = consensus.getGappedOffsetFor((int)ungappedCandidateRange.getEnd()+1) -1;
            Range gappedCandidateRange = Range.of(gappedStart, gappedEnd);
            Set<String> readIds = new HashSet<String>();
            for(CoverageRegion<P> region : coverageMap.getRegionsWhichIntersect(gappedCandidateRange)){
                for(P read : region){
                    readIds.add(read.getId());
                }
            }
            boolean isAbacusError=true;
            for(String readId : readIds){
                P read =contig.getRead(readId);              
                long adjustedStart = Math.max(gappedCandidateRange.getBegin(), read.getGappedStartOffset());
                long adjustedEnd = Math.min(gappedCandidateRange.getEnd(), read.getGappedEndOffset());
                boolean spansEntireRegion = (adjustedStart == gappedCandidateRange.getBegin()) && (adjustedEnd == gappedCandidateRange.getEnd());
                //only the reads that span the entire region
                //can be used to test if the bug is an abacus error.
                //reads that don't span the entire region won't have enough bases or gaps.
                if(!spansEntireRegion){
                	continue;
                }
                Range rangeOfInterest = Range.of(
                        read.toGappedValidRangeOffset(adjustedStart),
                        read.toGappedValidRangeOffset(adjustedEnd));
               
               NucleotideSequence sequenceOfInterest =new NucleotideSequenceBuilder(read.getNucleotideSequence())
														.trim(rangeOfInterest)
														.build();
               double numGaps= sequenceOfInterest.getNumberOfGaps();
               
                double percentGaps = numGaps/rangeOfInterest.getLength();
                //if the read spans the entire region AND
                //has enough non-gaps in the region of interest
                //then it can't be an abacus error.
                if(percentGaps <percentGap){
                    isAbacusError=false;
                    break;
                }                    

            }
            if(isAbacusError){
                errorRanges.add(ungappedCandidateRange);
            }
            
        }
        return errorRanges;
    }

    private List<Range> convertToUngappedRanges(List<Range> abacusErrors,
            NucleotideSequence consensus) {
        List<Range> ungappedRanges = new ArrayList<Range>(abacusErrors.size());
        for(Range error : abacusErrors){
            if(error.getLength() >=5){
                int ungappedStart =consensus.getUngappedOffsetFor((int)error.getBegin());
                int ungappedEnd =consensus.getUngappedOffsetFor((int)error.getEnd());
                
                ungappedRanges.add(Range.of(ungappedStart, ungappedEnd)); 
            }
        }

        List<Range> candidateRanges = Ranges.merge(ungappedRanges);
        return candidateRanges;
    }
    public List<Range>  findAbacusErrors(C contig){
        List<Range> ungappedCandidateRanges = getUngappedCandidateRanges(contig);
        return filterCandidates(contig, ungappedCandidateRanges) ;
        
    }
    private List<Range> getUngappedCandidateRanges(C contig) {
        
        List<Range> gapRangesPerRead = new ArrayList<Range>((int)contig.getNumberOfReads());
        StreamingIterator<P> readIterator = null;
        try{
        	readIterator = contig.getReadIterator();
        	while(readIterator.hasNext()){         
	            P placedRead = readIterator.next();
        		List<Range> gaps = new ArrayList<Range>(placedRead.getNucleotideSequence().getNumberOfGaps());
	            for(Integer gapOffset : placedRead.getNucleotideSequence().getGapOffsets()){
	                Range buildRange = Range.of(gapOffset.intValue() + placedRead.getGappedStartOffset());
	                gaps.add(buildRange);
	            }
	            List<Range> mergeRanges = Ranges.merge(gaps);
	            for(Range mergedRange: mergeRanges ){               
	                if(mergedRange.getLength() >=minAbacusLength){
	                    gapRangesPerRead.add(mergedRange);
	                }
	            }
	        }
        }finally{
        	IOUtil.closeAndIgnoreErrors(readIterator);
        }
        CoverageMap<Range> clusteredGapCoverage = new CoverageMapBuilder<Range>(gapRangesPerRead).build();
    
        List<Range> abacusErrors = new ArrayList<Range>();
       
        for(CoverageRegion<Range> gapRegion : clusteredGapCoverage){          
            if(gapRegion.getCoverageDepth() >0){
                abacusErrors.add(gapRegion.asRange());
            }            
        }
        
        List<Range> ungappedCandidateRanges = convertToUngappedRanges(Ranges.merge(abacusErrors,clusterDistance), contig.getConsensusSequence());
        return ungappedCandidateRanges;
    }
}
