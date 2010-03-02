/*
 * Created on Feb 19, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.DefaultContigQualityClassComputer;
import org.jcvi.assembly.contig.DefaultQualityClassContigMap;
import org.jcvi.assembly.contig.QualityClassComputer;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class InternalSangerContigCheckerStruct<R extends PlacedRead> extends ContigCheckerStruct<R> {
    private DefaultQualityClassContigMap<R> qualityClassMap;
    private final QualityClassComputer<R, NucleotideGlyph> qualityClassComputer;
    
    public InternalSangerContigCheckerStruct(Contig<R> contig,
            QualityDataStore qualityDataStore, PhredQuality qualityThreshold) {
        super(contig, qualityDataStore);
        this.qualityClassComputer = new DefaultContigQualityClassComputer<R>(getQualityValueStrategy(),qualityThreshold);
    }

    public synchronized DefaultQualityClassContigMap<R> getQualityClassMap() {
        if(qualityClassMap ==null){
            qualityClassMap = generateQualityClassContigMap();
        }
        return qualityClassMap;
    }
    private DefaultQualityClassContigMap<R> generateQualityClassContigMap() {
        DefaultQualityClassContigMap<R> qualityClassMap= new DefaultQualityClassContigMap(
                getSequenceCoverageMap(),
                getContig().getConsensus(),
                getQualityDataStore(),
                qualityClassComputer 
                 );
        return qualityClassMap;
    }
}
