package org.jcvi.jillion.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.ace.AceFileContigDataStore;
import org.jcvi.jillion.assembly.ace.LargeAceFileDataStore;

public class AceTagsFromLargeAceFileDataStore extends AbstractAceTagsFromAceFileDataStore{

	@Override
	protected AceFileContigDataStore createDataStoreFor(File aceFile) throws IOException {
		return LargeAceFileDataStore.create(aceFile);
	}

}
