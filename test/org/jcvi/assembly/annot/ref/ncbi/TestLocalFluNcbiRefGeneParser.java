/*
 * Created on Dec 19, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref.ncbi;

import java.io.IOException;
import java.io.InputStream;

public class TestLocalFluNcbiRefGeneParser extends
        AbstractTestFluNcbiRefGeneParser {

    @Override
    InputStream getStreamForOneGeneGbf() throws IOException {
        return TestLocalFluNcbiRefGeneParser.class.getResourceAsStream("files/oneGene.fcgi");
    }

    @Override
    InputStream getStreamFortwoGenesGbf() throws IOException {
        return TestLocalFluNcbiRefGeneParser.class.getResourceAsStream("files/twoGenes.fcgi");
        
    }

}
