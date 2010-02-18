/*
 * Created on Dec 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.util.Arrays;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.data.RawData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRawData {

    byte[] data = new byte[]{1,2,3,4,5,6,7};
    
    @Test
    public void parseReturnsSameDataAsInput() throws TraceDecoderException{
        assertTrue(Arrays.equals(new RawData().parseData(data), data));
    }
}
