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

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.InputStreamSupplier.InputStreamReadOptions;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.tukaani.xz.XZInputStream;

@RunWith(Parameterized.class)
public class TestInputStreamSupplierForFile {

    private static ResourceHelper helper;
    private static File originalFile;
    private static byte[] originalBytes;
    
    private final InputStreamSupplier sut;
    private final InputStream correctDecoding;
    private final String inputType;

    @Parameters(name="{2}")
    public static List<Object[]> implementations() throws IOException{
        helper = new ResourceHelper(TestInputStreamSupplierForFile.class);
        originalFile = helper.getFile("files/lorem_ipsum.txt");
        originalBytes = IOUtil.toByteArray(originalFile);
        
        List<Object[]> list = new ArrayList<>();
        //raw
        list.add(new Object[]{InputStreamSupplier.forFile(originalFile),
                              new FileInputStream(originalFile),
                              "raw"});
        //.zip
        File zipFile = helper.getFile("files/lorem_ipsum.zip");
        list.add(new Object[]{InputStreamSupplier.forFile(zipFile),
                              getZippedInputStream(zipFile),
                              "zip"});
        //gzip
        File gzipFile = helper.getFile("files/lorem_ipsum.txt.gz");
        list.add(new Object[]{InputStreamSupplier.forFile(gzipFile),
                              new GZIPInputStream(new FileInputStream(gzipFile)),
                              "gzip"});
        //xzip
        File xzFile = helper.getFile("files/lorem_ipsum.txt.xz");
        list.add(new Object[]{InputStreamSupplier.forFile(xzFile),
                              new XZInputStream(new FileInputStream(xzFile)),
                              "xz"});
        //tar
        File tarFile = helper.getFile("files/lorem_ipsum.txt.tar");
        list.add(new Object[]{InputStreamSupplier.forFile(tarFile),
                               getTarredInputStream(tarFile),
                              "tar"});
        //nested!!
        File tarGzFile = helper.getFile("files/lorem_ipsum.txt.tar.gz");
        list.add(new Object[]{InputStreamSupplier.forFile(tarGzFile),
                               getTarredInputStream( new GZIPInputStream(new FileInputStream(tarGzFile))),
                              "tar.gz"});
        
        File tarXzFile = helper.getFile("files/lorem_ipsum.txt.tar.xz");
        list.add(new Object[]{InputStreamSupplier.forFile(tarXzFile),
                               getTarredInputStream( new XZInputStream(new FileInputStream(tarXzFile))),
                              "tar.xz"});
        return list;
    }
    
    private static InputStream getZippedInputStream(File zipFile) throws IOException{
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
        zipIn.getNextEntry();
        return zipIn;
    }
    private static InputStream getTarredInputStream(File tarFile) throws IOException{
    	return getTarredInputStream(new FileInputStream(tarFile));
       
    }
    private static InputStream getTarredInputStream(InputStream in) throws IOException{
    	TarArchiveInputStream zipIn = new TarArchiveInputStream(in);
        zipIn.getNextEntry();
        return zipIn;
    }
    
    public TestInputStreamSupplierForFile(InputStreamSupplier sut, InputStream correctDecoding, String inputType){
        this.sut = sut;
        this.correctDecoding = correctDecoding;
        this.inputType = inputType;
    }
   
    @Test
    public void decodedCorrectly() throws IOException{
        byte[] actual = getBytes(correctDecoding); 
        assertArrayEquals(originalBytes, actual);
    }
    @Test
    public void matchesRaw() throws IOException{
       byte[] actual = getBytes(sut.get(InputStreamReadOptions.builder().nestedDecompress(true).build())); 
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
        
        byte[] actual = getBytes(sut.get(InputStreamReadOptions.builder().nestedDecompress(true).start(offset).build())); 
        assertArrayEquals(expected, actual);
    }
    
    
    @Test
    public void getRange() throws IOException{
        int start = originalBytes.length/2;
        int end = 3* originalBytes.length/4;
        //copyOfRange has exclusive end which is why we +1
        byte[] expected = Arrays.copyOfRange(originalBytes, start, end+1);
        
        byte[] actual = getBytes(sut.get(InputStreamReadOptions.builder().nestedDecompress(true).range(Range.of(start, end)).build())); 
        assertArrayEquals(expected, actual);
    }

}
