package org.jcvi.jillion.assembly.util;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;

public class TestSliceMapBuilderUsingQualityDataStore extends AbstractTestSliceMap{

	@Override
	protected SliceMap createSliceMapFor(Contig<AssembledRead> contig,
			QualitySequenceDataStore qualityDatastore,
			GapQualityValueStrategy qualityValueStrategy) {
		return new SliceMapBuilder<AssembledRead>(contig, qualityDatastore)
					.gapQualityValueStrategy((GapQualityValueStrategy)qualityValueStrategy)
					.build();
	}

}
