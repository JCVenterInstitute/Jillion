package org.jcvi.jillion.assembly.util;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;

public class TestParallelSliceCollector extends AbstractTestSliceMap{

    @Override
    protected SliceMap createSliceMapFor(Contig<AssembledRead> contig,
            QualitySequenceDataStore qualityDatastore,
            GapQualityValueStrategy qualityValueStrategy) {
        
        return contig.reads()
                .parallel()
                        .collect(SliceMapCollector.toSliceMap(contig.getConsensusSequence(), qualityValueStrategy, qualityDatastore));
    }

}
