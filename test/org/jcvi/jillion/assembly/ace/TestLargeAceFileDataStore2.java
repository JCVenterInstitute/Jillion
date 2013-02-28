package org.jcvi.jillion.assembly.ace;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.junit.BeforeClass;

public class TestLargeAceFileDataStore2  extends TestAbstractAceParserMatchesAce2ContigMultipleContigs{


	@BeforeClass
	public static void createAceDataStoreFor()
			throws IOException {
		sut= LargeAceFileDataStore2.create(ACE_FILE,DataStoreFilters.alwaysAccept());
	}

}
