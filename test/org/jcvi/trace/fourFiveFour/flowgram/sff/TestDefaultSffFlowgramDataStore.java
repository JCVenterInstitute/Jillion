/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.FileInputStream;

import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

public class TestDefaultSffFlowgramDataStore extends TestReadExampleSffFile{

    private DefaultSffFileDataStore dataStore;
    @Override
    protected Flowgram getFlowgram(String id) throws Exception {
        return dataStore.get(id);
    }

    @Override
    protected int getNumberOfFlowgrams() throws Exception {
        return dataStore.size();
    }

    @Override
    protected void parseSff(File f) throws Exception {
       
        dataStore = new DefaultSffFileDataStore(new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE));
        final FileInputStream in = new FileInputStream(f);
        try{
            SffParser.parseSFF(in, dataStore);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
        
    }

}
