package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;

public class AceTagsFromDefaultAceFileDataStore extends AbstractAceTagsFromAceFileDataStore{

	@Override
	protected AceFileContigDataStore createDataStoreFor(File aceFile) throws IOException {
		return DefaultAceFileDataStore.create(aceFile);
	}

}
