/*
 * Created on Apr 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jcvi.io.IOUtil;
import static org.junit.Assert.*;
public class TestSFFCodecParseActualSFFFile extends AbstractTestSffFileDataStore{

    @Override
    protected SffDataStore parseDataStore(File file) throws SFFDecoderException{
        
        InputStream in=null;
        try {
            in = new FileInputStream(file);
            
            DefaultSffFileDataStore dataStore = new DefaultSffFileDataStore(runLengthQualityCodec);
            SffParser.parseSFF(in, dataStore);
            return dataStore;
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
            throw new RuntimeException("could not open file ",e);
         }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
   
}
