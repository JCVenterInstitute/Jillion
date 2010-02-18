/*
 * Created on Apr 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.assembly.Placed;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class UngappedCoverageMap <V extends Placed,T extends CoverageRegion<V>> extends DefaultCoverageMap<V,T> {

    public static <V extends Placed,T extends CoverageRegion<V>> UngappedCoverageMap<V, T> 
        buildUngappedCoverageMap(CoverageMap<CoverageRegion<V>> gappedCoverageMap, NucleotideEncodedGlyphs gappedConsensus){
        return new Builder(gappedCoverageMap, gappedConsensus).build();
    }
    protected UngappedCoverageMap(List<T> regions) {
        super(regions);
    }

   public static class Builder<V extends Placed>{
       private final CoverageMap<CoverageRegion<V>> gappedCoverageMap;
       private final NucleotideEncodedGlyphs gappedConsensus;
       
       public Builder(CoverageMap<CoverageRegion<V>> gappedCoverageMap, NucleotideEncodedGlyphs gappedConsensus){
           this.gappedConsensus = gappedConsensus;
           this.gappedCoverageMap = gappedCoverageMap;           
       }
       
       public UngappedCoverageMap<V,CoverageRegion<V>> build(){
           int gapsBeforeCurrentRegion =0;
           List<CoverageRegion<V>> ungappedRegions = new ArrayList<CoverageRegion<V>>();
           List<Integer> consensusGapIndexes = gappedConsensus.getGapIndexes();
           for(CoverageRegion<V> currentRegion : gappedCoverageMap){
               int numberOfGaps = computeNumberOfGapsInRegion(consensusGapIndexes,currentRegion);
               long ungappedStart = currentRegion.getStart()-gapsBeforeCurrentRegion;
               CoverageRegion<V> ungappedRegion = createUngappedCoverageRegion(currentRegion, numberOfGaps, ungappedStart);
               if(ungappedRegion.getLength() >0){
                   ungappedRegions.add(ungappedRegion);
               }
               gapsBeforeCurrentRegion += numberOfGaps;
           }
           
           return new UngappedCoverageMap<V, CoverageRegion<V>>(ungappedRegions);
       }

    private CoverageRegion<V> createUngappedCoverageRegion(
            CoverageRegion<V> currentRegion, int numberOfGaps, long ungappedStart) {
        long shiftedOffset = currentRegion.getStart() - ungappedStart;
        DefaultCoverageRegion.Builder<V> ungappedRegion = new DefaultCoverageRegion.Builder<V>(
                                       ungappedStart,
                                       currentRegion.getElements());
           ungappedRegion.end(currentRegion.getEnd()-numberOfGaps-shiftedOffset);
        return ungappedRegion.build();
    }

    private int computeNumberOfGapsInRegion(List<Integer> consensusGapIndexes,
            CoverageRegion<V> currentRegion) {
        int numberOfGaps=0;
           for(int i= (int)currentRegion.getStart(); i<=currentRegion.getEnd(); i++){
               if(consensusGapIndexes.contains(Integer.valueOf(i))){
                   numberOfGaps++;
               }
           }
        return numberOfGaps;
    }
       
   }
}
