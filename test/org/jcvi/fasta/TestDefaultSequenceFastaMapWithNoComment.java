/*
 * Created on Apr 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.IOException;


public class TestDefaultSequenceFastaMapWithNoComment extends AbstractTestSequenceFastaMapWithNoComment{

    @Override
    protected DefaultNucleotideFastaFileDataStore buildMap(
            File file) throws IOException {
        return new DefaultNucleotideFastaFileDataStore(file);
    }
    

}
