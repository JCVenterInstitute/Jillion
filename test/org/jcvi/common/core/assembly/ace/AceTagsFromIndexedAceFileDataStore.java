package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.assembly.ace.AceFileContigDataStoreFactory.AceFileDataStoreType;

public class AceTagsFromIndexedAceFileDataStore extends AbstractAceTagsFromAceFileDataStore{

	@Override
	protected AceFileContigDataStore createDataStoreFor(File aceFile) throws IOException {
		return AceFileContigDataStoreFactory.create(aceFile, AceFileDataStoreType.INDEXED);
	}

}
