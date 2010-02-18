/*
 * Created on Apr 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;


public class TestLargeSffFileDataStore extends AbstractTestSffFileDataStore{

    @Override
    protected SffDataStore parseDataStore(File in) throws Exception {
        
        return new LargeSffFileDataStore( in, runLengthQualityCodec);
    }

}
