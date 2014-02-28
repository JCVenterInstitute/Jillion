package org.jcvi.jillion.maq;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;

public class TestIndexedBinaryFastqDataStore extends AbstractTestBinaryFastqDataStore{

	@Override
	protected DataStoreProviderHint getProviderHint() {
		return DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY;
	}

}
