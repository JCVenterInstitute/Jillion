package org.jcvi.jillion.assembly.util.slice;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;

public class TestSliceMapBuilderUsingQualityDataStore extends AbstractTestSliceMap{

	@Override
	protected SliceMap createSliceMapFor(Contig<AssembledRead> contig,
			QualitySequenceDataStore qualityDatastore,
			QualityValueStrategy qualityValueStrategy) {
		return new SliceMapBuilder<AssembledRead>(contig, qualityDatastore)
					.gapQualityValueStrategy((GapQualityValueStrategies)qualityValueStrategy)
					.build();
	}

}
