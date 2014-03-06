package org.jcvi.jillion.maq;

import org.jcvi.jillion.maq.bfa.TestBfaDataStores;
import org.jcvi.jillion.maq.bfa.TestBinaryFastaFileParser;
import org.jcvi.jillion.maq.bfa.TestBinaryFastaFileWriter;
import org.jcvi.jillion.maq.bfq.TestBinaryFastqDataStore;
import org.jcvi.jillion.maq.bfq.TestBinaryFastqFileWriter;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestBinaryFastqDataStore.class,
    	
    	TestBinaryFastqFileWriter.class,
    	
    	TestBinaryFastaFileParser.class,
    	TestBinaryFastaFileWriter.class,
    	TestBfaDataStores.class
    }
    )
public class AllMaqTests {

}
