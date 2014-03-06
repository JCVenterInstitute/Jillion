package org.jcvi.jillion.maq.bfq;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;

public class TestDefaultBinaryFastqDataStore extends AbstractTestBinaryFastqDataStore{

	@Override
	protected DataStoreProviderHint getProviderHint() {
		return DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED;
	}

}
