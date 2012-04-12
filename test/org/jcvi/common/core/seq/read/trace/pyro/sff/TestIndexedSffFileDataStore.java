package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.IOException;

import org.jcvi.common.core.seq.read.trace.pyro.FlowgramDataStore;
import org.jcvi.common.core.seq.read.trace.pyro.sff.IndexedSffFileDataStore.FullPassIndexedSffFileDataStore;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestIndexedSffFileDataStore extends AbstractTestExampleSffFile{

	@Test
	public void returnManifestIndexedInstanceIfManifestIsPresent() throws IOException{
		FlowgramDataStore datastore = IndexedSffFileDataStore.create(SFF_FILE);
		assertTrue(datastore instanceof Indexed454SffFileDataStore);
	}
	@Test
	public void returnManifestIndexedInstanceIfManifestWithNoXMLIsPresent() throws IOException{
		FlowgramDataStore datastore = IndexedSffFileDataStore.create(SFF_FILE_NO_XML);
		assertTrue(datastore instanceof Indexed454SffFileDataStore);
	}
	
	@Test
	public void returnFullyParsedIndexedInstanceIfNoIndexIsPresent() throws IOException{
		FlowgramDataStore datastore = IndexedSffFileDataStore.create(SFF_FILE_NO_INDEX);
		assertTrue(datastore instanceof FullPassIndexedSffFileDataStore);
	}
}
