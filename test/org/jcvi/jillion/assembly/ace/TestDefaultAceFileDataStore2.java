package org.jcvi.jillion.assembly.ace;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.junit.BeforeClass;

public class TestDefaultAceFileDataStore2 extends TestAbstractAceParserMatchesAce2ContigMultipleContigs{


	@BeforeClass
	public static void createAceDataStoreFor()
			throws IOException {
		sut= DefaultAceFileDataStore2.create(ACE_FILE,DataStoreFilters.alwaysAccept());
	}


}