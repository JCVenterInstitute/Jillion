/*
 * Created on Feb 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.qualClass.QualityClass;
import org.jcvi.sequence.SequenceDirection;

public class DefaultContigQualityClassComputer<P extends PlacedRead> implements QualityClassComputer<P,NucleotideGlyph>{
   private final QualityValueStrategy qualityValueStrategy;
   private final PhredQuality qualityThreshold;
    
    public DefaultContigQualityClassComputer(QualityValueStrategy qualityValueStrategy,PhredQuality qualityThreshold){
        this.qualityValueStrategy = qualityValueStrategy;
        this.qualityThreshold = qualityThreshold;
    }
    @Override
    public QualityClass computeQualityClass( CoverageMap<CoverageRegion<VirtualPlacedRead<P>>> coverageMap,
            DataStore<EncodedGlyphs<PhredQuality>> qualityFastaMap,
    EncodedGlyphs<NucleotideGlyph> consensus,int index) {
        CoverageRegion<VirtualPlacedRead<P>> region = coverageMap.getRegionWhichCovers(index);
        if(region ==null){
            return QualityClass.ZERO_COVERAGE;
        }
        final NucleotideGlyph consensusBase = consensus.get(index);
        
        try {
            return computeQualityClassFor(qualityFastaMap, index,
                    region, consensusBase);
        } catch (DataStoreException e) {
            throw new IllegalStateException("error getting quality values" ,e);
        }      
        
    }
    
    protected QualityClass computeQualityClassFor(
            DataStore<EncodedGlyphs<PhredQuality>> qualityFastaMap, int index,
            CoverageRegion<VirtualPlacedRead<P>> region, final NucleotideGlyph consensusBase) throws DataStoreException {
        QualityClass.Builder builder = new QualityClass.Builder(consensusBase,qualityThreshold);
        return computeQualityClassFor(qualityFastaMap, index, region,
                consensusBase, builder);
    }
    protected QualityClass computeQualityClassFor(
            DataStore<EncodedGlyphs<PhredQuality>> qualityFastaMap, int index,
            CoverageRegion<VirtualPlacedRead<P>> region, final NucleotideGlyph consensusBase,
            QualityClass.Builder builder) throws DataStoreException {
        for(VirtualPlacedRead<P> virtualPlacedRead : region.getElements()){
            P realRead = virtualPlacedRead.getRealPlacedRead();
            final EncodedGlyphs<PhredQuality> qualityRecord = qualityFastaMap.get(realRead.getId());
            if(qualityRecord !=null){
                int indexIntoVirtualRead = (int) (index - virtualPlacedRead.getStart());
                final int indexIntoActualRead = virtualPlacedRead.getRealIndexOf(indexIntoVirtualRead);
                final NucleotideGlyph calledBase = virtualPlacedRead.getEncodedGlyphs().get(indexIntoVirtualRead);
                
                PhredQuality qualityValue =qualityValueStrategy.getQualityFor(realRead, qualityRecord, indexIntoActualRead);
                boolean agreesWithConsensus = isSame(consensusBase, calledBase);
                boolean isHighQuality = isHighQuality(qualityValue);
                SequenceDirection direction =virtualPlacedRead.getSequenceDirection();
                addRead(builder, agreesWithConsensus, isHighQuality,
                        direction);
            }
        }
        return builder.build();
    }
    private boolean isHighQuality(PhredQuality qualityValue) {
        return qualityValue.compareTo(qualityThreshold)>=0;
    }

    private boolean isSame(final NucleotideGlyph base1,
            final NucleotideGlyph base2) {
        return base1 == base2;
    }

    protected void addRead(QualityClass.Builder builder,
            boolean agreesWithConsensus, boolean isHighQuality,
            SequenceDirection direction) {
        if(agreesWithConsensus){
            if(isHighQuality){
                builder.addHighQualityAgreement(direction);
            }
            else{
                builder.addLowQualityAgreement(direction);
            }
        }
        else{
            if(isHighQuality){
                builder.addHighQualityConflict(direction);
            }
            else{
                builder.addLowQualityConflict(direction);
            }
        }
    }
}

