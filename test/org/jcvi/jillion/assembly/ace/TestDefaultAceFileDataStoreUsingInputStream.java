package org.jcvi.jillion.assembly.ace;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;

public class TestDefaultAceFileDataStoreUsingInputStream extends TestAbstractAceParserMatchesAce2ContigMultipleContigs{


	@BeforeClass
	public static void createAceDataStoreFor()
			throws IOException {
		InputStream in = new FileInputStream(ACE_FILE);
		sut= DefaultAceFileDataStore.create(in);
	}

}
