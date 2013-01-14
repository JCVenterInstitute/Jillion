package org.jcvi.jillion.fasta.aa;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestUnCommentedAminoAcidSequenceFastaRecord.class,
    	TestCommentedAminoAcidSequenceFastaRecord.class,
    	
    	TestDefaultAminoAcidSequenceFastaDataStore.class,
    	TestLargeAminoAcidSequenceFastaFileDataStore.class,
    	TestIndexedAminoAcidSequenceFastaFileDataStore.class,
    	
    	TestDefaultAminoAcidSequenceFastaRecordWriter.class
    }
    )
public class AllAminoAcidSequenceFastaRecordUnitTests {

}
