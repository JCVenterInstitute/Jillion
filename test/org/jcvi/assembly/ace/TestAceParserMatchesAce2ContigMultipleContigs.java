/*
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.IOException;

public class TestAceParserMatchesAce2ContigMultipleContigs extends AbstractTestAceParserMatchesAce2Contig{
    private static final String ACE_FILE = "files/fluSample.ace";
    private static final String CONTIG_FILE = "files/fluSample.contig";
    public TestAceParserMatchesAce2ContigMultipleContigs() throws IOException {
        super(ACE_FILE,CONTIG_FILE);
       
    }

}
