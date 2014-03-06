package org.jcvi.jillion.maq.bfq;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;

public class TestLargeBinaryFastqDataStore extends AbstractTestBinaryFastqDataStore{

	@Override
	protected DataStoreProviderHint getProviderHint() {
		return DataStoreProviderHint.ITERATION_ONLY;
	}

}
