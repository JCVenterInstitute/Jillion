/*
 * Created on Oct 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CheckSumUtil {
    /**
     * Current supported Crytpo Hash Algorithms as defined
     * in the Java Crypto Spec Appendix.
     * @author dkatzel
     * @see <a href ="http://java.sun.com/j2se/1.4.2/docs/guide/security/CryptoSpec.html#AppA">Java Crypto Spec Appendix</a>
     *
     */
    public enum HashAlgorithm{
        SHA_1("SHA-1"),
        MD5("MD5"),        
        MD2("MD2"),
        SHA_256("SHA-256"),
        SHA_384("SHA-384"),
        SHA_512("SHA-512")
        ;
        private final String desc;
        private HashAlgorithm(String description){
            desc = description;
        }
        @Override
        public String toString() {
            return desc;
        }
        
    }
    /**
     * Computes the checksum of the given input using the given HashAlgorithm.
     * @param input the input to checksum.
     * @param algorithm the hash algorithm to use to compute the checksum.
     * @return a String representing the checksum as hex. 
     * @throws IOException if there is a problem reading the InputStream.
     * @throws NullPointerException if any parameters are null.
     */
    public static String getChecksumAsHex(InputStream input, HashAlgorithm algorithm) throws IOException{
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(algorithm.toString());
        } catch (NoSuchAlgorithmException e) {
            //this shouldn't happen... since the enum is controlled.
            throw new IllegalArgumentException("invalid format", e);
        }
        md.update(IOUtil.readStreamAsBytes(input));
        byte[] digest =md.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        return  bigInt.toString(16);

    }
    
    
}
