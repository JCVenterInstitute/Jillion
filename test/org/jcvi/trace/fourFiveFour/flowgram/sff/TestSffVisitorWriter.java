/*
 * Created on Nov 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.jcvi.io.IOUtil;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSffVisitorWriter {

    ResourceFileServer resources = new ResourceFileServer(TestSffVisitorWriter.class);
    
    
    @Test
    public void write() throws IOException, SFFDecoderException{
        InputStream in = resources.getFileAsStream("files/5readExample.sff");
        byte[] expectedBytes =IOUtil.readStreamAsBytes(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        SffFileVisitor sut = new SffVisitorWriter(out);
        SffParser.parseSFF(new ByteArrayInputStream(expectedBytes), sut);

        final byte[] actualBytes = out.toByteArray();
        //must do a sub array because real sff has extra metadata at the end
        //which isn't documented
        assertArrayEquals(Arrays.copyOf(expectedBytes, actualBytes.length), actualBytes);
    }
}
