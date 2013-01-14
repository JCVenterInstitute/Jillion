package org.jcvi.common.core.assembly;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.util.Builder;

public interface ScaffoldDataStoreBuilder extends Builder<ScaffoldDataStore>{

	ScaffoldDataStoreBuilder addScaffold(Scaffold scaffold);
	
	ScaffoldDataStoreBuilder addPlacedContig(String scaffoldId, String contigId, DirectedRange directedRange);
}
