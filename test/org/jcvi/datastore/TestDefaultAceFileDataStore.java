/*
 * Created on May 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.assembly.ace.TestAceParserMatchesAce2ContigMultipleContigs;
import static org.junit.Assert.fail;
public class TestDefaultAceFileDataStore extends TestAceParserMatchesAce2ContigMultipleContigs{

    public TestDefaultAceFileDataStore() throws IOException {
        super();        
    }

    @Override
    protected List<AceContig> getContigList(File aceFile)
            throws IOException {
        DefaultAceFileDataStore dataStore= new DefaultAceFileDataStore();
        AceFileParser.parseAceFile(aceFile, dataStore);
        List<AceContig> contigs = new ArrayList<AceContig>(dataStore.size());
        for(Iterator<String> iter = dataStore.getIds(); iter.hasNext();){
            String id = iter.next();
            try {
                contigs.add(dataStore.get(id));
            } catch (DataStoreException e) {
                fail("error getting contig " + id);
            }
        }
        return contigs;
    }

}
