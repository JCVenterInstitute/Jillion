package org.jcvi.common.core.seq.fastx.fasta.aa;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestUnCommentedAminoAcidSequenceFastaRecord.class,
    	TestCommentedAminoAcidSequenceFastaRecord.class
    }
    )
public class AllAminoAcidSequenceFastaRecordUnitTests {

}
