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
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.jcvi.io.IOUtil;
import org.jcvi.io.IOUtil.ENDIAN;

public final class CasUtil {

    private CasUtil(){}
    
    public static int numberOfBytesRequiredFor(long numberOfContigs){
        if(numberOfContigs < 1){
            throw new IllegalArgumentException("number of contigs must be > 0 : " + numberOfContigs);
        }
        if(numberOfContigs ==1){
            return 1;
        }
        return (int)Math.ceil(Math.log(numberOfContigs)/Math.log(256));
    }
    /**
     * To save space, CAS files have a variable field for byte counts
     * which range from 1 to 5 bytes long.
     * @param in
     * @return
     * @throws IOException
     */
    public static long parseByteCountFrom(InputStream in) throws IOException{
        
        int firstByte =in.read();
        if(firstByte<254){
            return firstByte;
        }
        
        if(firstByte ==254){
            //read next 2 bytes
           return readCasUnsignedShort(in);
        }
        return readCasUnsignedInt(in);
    }
    /**
     * CAS files store strings in Pascal like format with 
     * the length of the string first, followed by the
     * characters in the string. (no terminating char)
     * @param in
     * @return
     * @throws IOException 
     */
    public static String parseCasStringFrom(InputStream in) throws IOException{
        int length = (int)parseByteCountFrom(in);
       
        byte bytes[] = IOUtil.readByteArray(in, length);
        
        return new String(bytes);
        
    }
    public static short readCasUnsignedByte(InputStream in) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, 1, ENDIAN.LITTLE)).shortValue();
     }
    public static int readCasUnsignedShort(InputStream in) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, 2, ENDIAN.LITTLE)).intValue();
     }
    public static long readCasUnsignedInt(InputStream in) throws IOException{
       return readCasUnsignedInt(in, 4);
    }
    public static long readCasUnsignedInt(InputStream in, int numberOfBytesInNumber) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, numberOfBytesInNumber, ENDIAN.LITTLE)).longValue();
     }
    public static BigInteger readCasUnsignedLong(InputStream in) throws IOException{
        return new BigInteger(1,
                 IOUtil.readByteArray(in, 8, ENDIAN.LITTLE));
     }
}
