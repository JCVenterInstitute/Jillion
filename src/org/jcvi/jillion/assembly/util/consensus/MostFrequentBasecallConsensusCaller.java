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
/*
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.assembly.util.columns.AssemblyColumn;
import org.jcvi.jillion.assembly.util.columns.AssemblyColumnConsensusCaller;
import org.jcvi.jillion.assembly.util.columns.QualifiedAssemblyColumn;
import org.jcvi.jillion.assembly.util.columns.QualifiedAssemblyColumnElement;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.MapValueComparator;
import org.jcvi.jillion.core.util.SingleThreadAdder;
/**
 * {@code MostFrequentBasecallConsensusCaller} is a {@link ConsensusCaller}
 * implementation that will return the most frequent basecall in
 * a Slice and the Consensus Quality is the sum of all the qualities of
 * the reads with that basecall minus the sum of the reads 
 * without that basecall.  If the Slice is empty, then the ConsensusResult
 * is N with 0 quality.  If multiple bases share the "most frequent basecall count",
 * then the basecall with the highest cumulative quality value will be picked.
 *  
 * 
 * @author dkatzel
 *
 */
public enum MostFrequentBasecallConsensusCaller implements ConsensusCaller{
	INSTANCE;
	
	@SuppressWarnings("unchecked")
	public static <E extends QualifiedAssemblyColumnElement, C extends QualifiedAssemblyColumn<E>> AssemblyColumnConsensusCaller<E, C> instance(){
		return (AssemblyColumnConsensusCaller<E, C>) INSTANCE;
	}
	@SuppressWarnings("unchecked")
	public static ConsensusCaller sliceInstance(){
		return (ConsensusCaller) INSTANCE;
	}
    @Override
    public ConsensusResult callConsensus(Slice slice) {
        if(slice==null){
            return new DefaultConsensusResult(Nucleotide.Unknown, 0);
        }
        Map<Nucleotide, SingleThreadAdder> histogramMap = new EnumMap<Nucleotide, SingleThreadAdder>(Nucleotide.class);
        Map<Nucleotide, SingleThreadAdder> qualitySums = new EnumMap<Nucleotide, SingleThreadAdder>(Nucleotide.class);
        for(QualifiedAssemblyColumnElement sliceElement : slice){
            Nucleotide base =sliceElement.getBase();
            SingleThreadAdder sum = histogramMap.get(base);
            if(sum ==null){
            	histogramMap.put(base, new SingleThreadAdder(1));
            	qualitySums.put(base, new SingleThreadAdder(sliceElement.getQualityScore()));
            }else{
            	sum.increment();
            	qualitySums.get(base).add(sliceElement.getQualityScore());
            }
                   
        }
        Nucleotide consensus= findMostOccuringBaseWithHighestQvs(histogramMap, qualitySums);
        int sum = getCumulativeQualityConsensusValue(qualitySums, consensus);
        return new DefaultConsensusResult(consensus, sum);
    }


	private int getCumulativeQualityConsensusValue(
			Map<Nucleotide, SingleThreadAdder> qualitySums, Nucleotide consensus) {
		int sum=0;
        for(Entry<Nucleotide, SingleThreadAdder> entry : qualitySums.entrySet()){
            if(entry.getKey() == consensus){
                sum+= entry.getValue().intValue();
            }
            else{
                sum -= entry.getValue().intValue();
            }
        }
		return sum;
	}

    
    /**
     * Get the most occurring basecall,
     * if multiple basecalls have the same highest frequency,
     * then pick the one with the highest cumulative quality score.
     * @param histogramMap
     * @param qualitySums
     * @return
     */
    private Nucleotide findMostOccuringBaseWithHighestQvs(Map<Nucleotide, SingleThreadAdder> histogramMap,Map<Nucleotide, SingleThreadAdder> qualitySums ){
        if(histogramMap.isEmpty()){
            return Nucleotide.Unknown;
        }
      
       
        SortedMap<Nucleotide, SingleThreadAdder> sortedMap = MapValueComparator.sortDescending(histogramMap);
        
        Iterator<Entry<Nucleotide, SingleThreadAdder>> iter = sortedMap.entrySet().iterator();
        //has to have at least one
        Entry<Nucleotide, SingleThreadAdder> most = iter.next();
        Nucleotide consensus = most.getKey();
        int count = most.getValue().intValue();
        int bestQv = qualitySums.get(consensus).intValue();
        while(iter.hasNext()){
        	Entry<Nucleotide, SingleThreadAdder> next = iter.next();
        	if(next.getValue().intValue() <count){
        		break;
        	}
        	int currentQv = qualitySums.get(next.getKey()).intValue();
			if(currentQv > bestQv){
        		bestQv = currentQv;
        		consensus = next.getKey();
        	}
        }
        return consensus;
       
    }
}
