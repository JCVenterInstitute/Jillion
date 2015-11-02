/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.io;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestInputStreamSupplierForFile {

    private static ResourceHelper helper;
    private static File originalFile;
    private static byte[] originalBytes;
    
    private final InputStreamSupplier sut;
    private final InputStream correctDecoding;
    
    @Parameters
    public static List<Object[]> implementations() throws IOException{
        helper = new ResourceHelper(TestInputStreamSupplierForFile.class);
        originalFile = helper.getFile("files/lorem_ipsum.txt");
        originalBytes = IOUtil.toByteArray(originalFile);
        
        List<Object[]> list = new ArrayList<>();
        //raw
        list.add(new Object[]{InputStreamSupplier.forFile(originalFile), new FileInputStream(originalFile)});
        //.zip
        File zipFile = helper.getFile("files/lorem_ipsum.zip");
        list.add(new Object[]{InputStreamSupplier.forFile(zipFile), getZippedInputStream(zipFile)});
        //gzip
        File gzipFile = helper.getFile("files/lorem_ipsum.txt.gz");
        list.add(new Object[]{InputStreamSupplier.forFile(gzipFile), new GZIPInputStream(new FileInputStream(gzipFile))
                });
        
        return list;
    }
    
    private static InputStream getZippedInputStream(File zipFile) throws IOException{
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
        zipIn.getNextEntry();
        return zipIn;
    }
    
    public TestInputStreamSupplierForFile(InputStreamSupplier sut, InputStream correctDecoding){
        this.sut = sut;
        this.correctDecoding = correctDecoding;
    }
   
    @Test
    public void decodedCorrectly() throws IOException{
        byte[] actual = getBytes(correctDecoding); 
        assertArrayEquals(originalBytes, actual);
    }
    @Test
    public void matchesRaw() throws IOException{
       byte[] actual = getBytes(sut.get()); 
       assertArrayEquals(originalBytes, actual);
    }
    
    @Test
    public void canBeCalledMultipletimes() throws IOException{
        matchesRaw();
    }
    
    private byte[] getBytes(InputStream in) throws IOException{
        try{
            return IOUtil.toByteArray(in); 
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    
    @Test
    public void startFromOffset() throws IOException{
        int offset = originalBytes.length/2;
        byte[] expected = Arrays.copyOfRange(originalBytes, offset, originalBytes.length);
        
        byte[] actual = getBytes(sut.get(offset)); 
        assertArrayEquals(expected, actual);
    }
    
    
    @Test
    public void getRange() throws IOException{
        int start = originalBytes.length/2;
        int end = 3* originalBytes.length/4;
        //copyOfRange has exclusive end which is why we +1
        byte[] expected = Arrays.copyOfRange(originalBytes, start, end+1);
        
        byte[] actual = getBytes(sut.get(Range.of(start, end))); 
        assertArrayEquals(expected, actual);
    }
}
