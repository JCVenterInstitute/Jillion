package org.jcvi.jillion.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ace.AceFileContigDataStore;
import org.jcvi.jillion.assembly.ace.AceFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;

public class AceTagsFromIndexedAceFileDataStore extends AbstractAceTagsFromAceFileDataStore{

	@Override
	protected AceFileContigDataStore createDataStoreFor(File aceFile) throws IOException {
		return new AceFileDataStoreBuilder(aceFile)
		.hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_MEMORY)
		.build();
	}

}