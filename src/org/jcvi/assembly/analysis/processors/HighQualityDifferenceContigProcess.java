/*
 * Created on Feb 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.processors;

import java.util.List;
import java.util.Map.Entry;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.ContigCheckReportBuilder;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.assembly.analysis.issue.QualityDifferenceIssue;
import org.jcvi.assembly.contig.DefaultHighQualityDifferencesContigMap;
import org.jcvi.assembly.contig.HighQualityDifferencesContigMap;
import org.jcvi.assembly.contig.DefaultQualityDifference;
import org.jcvi.assembly.contig.qual.LowestFlankingQualityValueStrategy;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class HighQualityDifferenceContigProcess extends AbstractContigAnalysisProcess<PlacedRead>{

    
    private final PhredQuality qualityThreshold;
    
    public HighQualityDifferenceContigProcess(ContigCheckReportBuilder builder,ContigCheckerStruct struct, PhredQuality qualityThreshold){
        super(struct,builder );
        this.qualityThreshold = qualityThreshold;
    }
    @Override
    public void run() {
        HighQualityDifferencesContigMap map;
        try {
            map = generateHighQualityDifferenceContigMap();
        } catch (DataStoreException e) {
           throw new IllegalStateException("could not generate high quality difference map");
        }
        for( Entry<PlacedRead, List<DefaultQualityDifference>> entry :map.entrySet()){
            getBuilder().addAnalysisIssue(new QualityDifferenceIssue(entry.getKey(), entry.getValue()));
        }
    }
    private HighQualityDifferencesContigMap generateHighQualityDifferenceContigMap() throws DataStoreException {
        HighQualityDifferencesContigMap map = new DefaultHighQualityDifferencesContigMap(getStruct().getContig(), getStruct().getQualityDataStore(), 
                new LowestFlankingQualityValueStrategy(),qualityThreshold);
        return map;
    }

}
