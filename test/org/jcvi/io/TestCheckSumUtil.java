/*
 * Created on Oct 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.jcvi.io.CheckSumUtil.HashAlgorithm;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestCheckSumUtil {

    private InputStream input;
    @Before
    public void setup() throws UnsupportedEncodingException{
        input = new ByteArrayInputStream("The quick brown fox jumps over the lazy dog".getBytes("US-ASCII"));
        
    }
    @Test
    public void md2() throws IOException{
        String expectedChecksum = "3d85a0d629d2c442e987525319fc471";
        assertEquals(expectedChecksum, CheckSumUtil.getChecksumAsHex(input, HashAlgorithm.MD2));

    }
    @Test
    public void md5() throws IOException{
        String expectedChecksum = "9e107d9d372bb6826bd81d3542a419d6";
        assertEquals(expectedChecksum, CheckSumUtil.getChecksumAsHex(input, HashAlgorithm.MD5));

    }
    @Test
    public void emptyMd5() throws IOException{
        String expectedChecksum = "d41d8cd98f00b204e9800998ecf8427e";
        assertEquals(expectedChecksum, CheckSumUtil.getChecksumAsHex(new ByteArrayInputStream(new byte[]{}), HashAlgorithm.MD5));

    }
    @Test
    public void sha1() throws IOException{
        String expectedChecksum = "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12";
        assertEquals(expectedChecksum, CheckSumUtil.getChecksumAsHex(input, HashAlgorithm.SHA_1));
        
    }
    @Test
    public void sha256() throws IOException{
       String expectedChecksum = "d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592";
       assertEquals(expectedChecksum, CheckSumUtil.getChecksumAsHex(input, HashAlgorithm.SHA_256));
    }
    @Test
    public void sha384() throws IOException{
       String expectedChecksum = "ca737f1014a48f4c0b6dd43cb177b0afd9e5169367544c494011e3317dbf9a509cb1e5dc1e85a941bbee3d7f2afbc9b1";
       assertEquals(expectedChecksum, CheckSumUtil.getChecksumAsHex(input, HashAlgorithm.SHA_384));
      
    }
    @Test
    public void sha512() throws IOException{
       String expectedChecksum = "7e547d9586f6a73f73fbac0435ed76951218fb7d0c8d788a309d785436bbb642e93a252a954f23912547d1e8a3b5ed6e1bfd7097821233fa0538f3db854fee6";
       assertEquals(expectedChecksum, CheckSumUtil.getChecksumAsHex(input, HashAlgorithm.SHA_512));
      
    }
    
    @Test(expected = NullPointerException.class)
    public void nullInputShouldthrowNullPointerException() throws IOException{
        CheckSumUtil.getChecksumAsHex(null, HashAlgorithm.MD5);
    }
    @Test(expected = NullPointerException.class)
    public void nullAlgorithmShouldthrowNullPointerException() throws IOException{
        CheckSumUtil.getChecksumAsHex(input, null);
    }
}
