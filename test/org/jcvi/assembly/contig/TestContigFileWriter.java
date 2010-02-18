/*
 * Created on Mar 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.datastore.DefaultContigFileDataStore;
import org.jcvi.io.IOUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestContigFileWriter {
    ByteArrayOutputStream out;
    ContigFileWriter sut;
    static DefaultContigFileDataStore dataStore;
    private static String pathToFile = "files/gcv_23918.contig";
    @BeforeClass
    public static void parseContigs(){
        final InputStream resourceAsStream = TestContigFileWriter.class.getResourceAsStream(pathToFile);
       dataStore = new DefaultContigFileDataStore(resourceAsStream);
    }
    @Before
    public void setup(){
        out = new ByteArrayOutputStream();
        sut = new ContigFileWriter(out);
    }
    
    @Test
    public void write() throws IOException{
        for(Contig<PlacedRead> contig : dataStore){
            sut.write(contig);
        }
        
        final InputStream resourceAsStream = TestContigFileWriter.class.getResourceAsStream(pathToFile);
        
        byte[] expected =IOUtil.readStream(resourceAsStream).getBytes();
        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
        fileOut.write(out.toByteArray());
        assertEquals(new String(expected), new String(out.toByteArray()));
    }

}
