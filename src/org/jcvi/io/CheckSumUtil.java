/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
