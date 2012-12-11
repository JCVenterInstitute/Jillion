package org.jcvi.common.core.datastore;

import java.util.Arrays;

public class TestChainedDataStore extends TestMultipleDataStoreWrapper{

	@Override
	protected <T, D extends DataStore<T>> D createSut(Class<D> clazz,
			D... dataStores) {
		
		return ChainedDataStore.create(clazz, Arrays.asList(dataStores));
	}

}
