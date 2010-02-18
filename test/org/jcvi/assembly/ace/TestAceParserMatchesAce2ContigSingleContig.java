/*
 * Created on Feb 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;
import java.io.IOException;



public class TestAceParserMatchesAce2ContigSingleContig extends AbstractTestAceParserMatchesAce2Contig{
    private static final String ACE_FILE = "files/sample.ace";
    private static final String CONTIG_FILE = "files/sample.contig";
    public TestAceParserMatchesAce2ContigSingleContig() throws IOException {
        super(ACE_FILE,CONTIG_FILE);
       
    }
   

   
    
}
