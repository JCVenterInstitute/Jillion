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
 * Created on Aug 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Java Implementation of "base64" as defined in the MIME Spec (RFC 2045).
 * There is currently no standard Base 64 encoder/decoder
 * in the Java SDK.  There is a class in sun.misc package
 * but use of the sun packages are highly discouraged
 * because:
 * <ul>
 * <li> sun.* packages are not guaranteed to be on other non-sun JVMs.</li>
 * <li> sun.* packages are not guaranteed to be in the same JVM in different
 * versions and may be removed without notice.</li>
 * </ul>
 * @author dkatzel
 * @see <a href="http://tools.ietf.org/html/rfc2045">RFC 2045</a>
 *
 */
public final class Base64 {


    private static final int SIX_BIT_MASK = 0x3F;
    public static String encode(byte[] toBeEncoded){
        int position =0;
        StringBuilder result = new StringBuilder();
        while(position < toBeEncoded.length -2){
            int triplet = readTriplet(toBeEncoded, position);
            byte[] base64EncodedNumbers = convertTriplet(triplet);
            for(int i=0; i< base64EncodedNumbers.length; i++){
                result.append(base64ToChar(base64EncodedNumbers[i]));
            }
            position +=3;
           if(position %57 ==0){
               result.append(String.format("%n"));
           }
        }
        int numberOfRemainingBytes = toBeEncoded.length - position;
        if(numberOfRemainingBytes >0){
            byte[] encodedFinalTriplet = encodeFinalTriplet(toBeEncoded, numberOfRemainingBytes);
            for(int i=0; i< numberOfRemainingBytes+1; i++){
                result.append(base64ToChar(encodedFinalTriplet[i]));
            }
            for(int i=0; i< 4-encodedFinalTriplet.length; i++){
                result.append('=');
            }
        }
        return result.toString();
    }

    private static byte[] encodeFinalTriplet(byte[] toBeEncoded,
            int numberOfRemainingBytes) {
        int result =0;
        int position = toBeEncoded.length - numberOfRemainingBytes;
        while(position < toBeEncoded.length){
            result +=toBeEncoded[position];
            result <<=8;
            position++;
        }
        result <<= (8 * (2-numberOfRemainingBytes));
       
        byte[] encodedTriplet = convertTriplet(result);
        return Arrays.copyOf(encodedTriplet, numberOfRemainingBytes+1);
    }

    static char base64ToChar(byte b) {
        if(b <0 || b >63){
            throw new IllegalArgumentException("can not be < 0 or  > 63");
        }
        if(b <26){
            return (char)('A' + b);
        }
        if(b < 52){
            return (char)('a' + (b-26));
        }
        if(b< 62 ){
            return (char)('0'+ (b -52));
        }
        if(b == 62){
            return '+';
        }
        return '/';
    }

    static byte[] convertTriplet(int triplet) {
        int currentValue = triplet;
        ByteBuffer result = ByteBuffer.allocate(4);
        while(result.hasRemaining()){
            result.put((byte)(currentValue & SIX_BIT_MASK));
            currentValue >>=6;
        }
        //result is now backwards
        return reverse(result.array());
    }

    static int readTriplet(byte[] toBeEncoded, int position) {
        int result=toBeEncoded[position];
        result <<=8;
        
        result+=toBeEncoded[position+1]&0xff;
        result <<=8;
        
        result +=toBeEncoded[position+2] &0xff;
        return result;
    }
    static byte[] reverse(byte[] input){
    	//use byteBuffer for efficiency
    	//since byteBuffers don't initialize to 0s
        ByteBuffer result = ByteBuffer.allocate(input.length);
        for(int i=input.length-1; i>=0; i--){
            result.put(input[i]);
        }
        return result.array();
    }
    
}
