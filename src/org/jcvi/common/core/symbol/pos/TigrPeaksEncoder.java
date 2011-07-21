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
/**
 * 
 */
package org.jcvi.common.core.symbol.pos;

import java.nio.CharBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import org.jcvi.common.core.symbol.IllegalEncodedValueException;

/**
 * The <code>DefaultPositionsEncoder</code> provides a default implementation of
 * a {@link PositionsEncoder}.  This encoder provides pretty decent 
 * compression of data, usually capable of expressing a single position point
 * in just one byte of data, while still supporting large peak spacings with
 * complete accuracy.
 * <p>
 * The encoding scheme is based on a character representation of a delta 
 * encoding.  Instead of encoding the raw peak positions, the difference
 * between positions is used to index into the set of printable characters.
 * <p>
 * In the case that the difference between positions exceeds the number of
 * printable characters, a special escape sequence is used.  In place of the
 * delta-encoded character, a string composed of a escape flag character
 * followed by a uuencoded version of the absolute peak position is inserted
 * into the encoded string.  Any following characters are assumed to be in 
 * normal delta encoding format unless similarly escaped.
 * 
 * @author jsitz
 * @author dkatzel
 */
public class TigrPeaksEncoder
{
    public static final int DECODE_FULL_LENGTH = -1;
    /**
     * The character which acts as the offset origin for encoding
     */
    static final int ENCODING_ORIGIN = 0x20;

    /**
     * The maximum delta to encode without uuencoding
     */
    static final int MAX_BARE_DELTA = 62;
    
    /** 
     * The character used to flag uuencoded deltas in the encoded string (Hex 0x5F) 
     */
    static final char FLAG_CHAR = '_';
    
    /** 
     * The character used to signal the end of the encoded string (Hex 0x60) 
     */
    static final char TERMINATOR_CHAR = '`';

    
    /**
     * Uuencodes a single <code>int</code> value.
     * 
     * @param value The value to encode.
     * @return An array of four <code>char</code>s.
     */
    static char[] uuencode(int value)
    {
        final char[] encoded = new char[4];
        
        encoded[0] = (char)(((value >> 18) & 0x3F) + TigrPeaksEncoder.ENCODING_ORIGIN);
        encoded[1] = (char)(((value >> 12) & 0x3F) + TigrPeaksEncoder.ENCODING_ORIGIN);
        encoded[2] = (char)(((value >>  6) & 0x3F) + TigrPeaksEncoder.ENCODING_ORIGIN);
        encoded[3] = (char)(( value        & 0x3F) + TigrPeaksEncoder.ENCODING_ORIGIN);
        
        return encoded;
    }
    
    /**
     * Uudecodes a single <code>int</code> value.
     * 
     * @param octet An array of uuencoded characters.  The length of the array
     * is assumed to be at least 4.
     * @return The decoded value.
     */
    static int uudecode(char[] octet)
    {
       return ((octet[0] - TigrPeaksEncoder.ENCODING_ORIGIN) << 18) |
               ((octet[1] - TigrPeaksEncoder.ENCODING_ORIGIN) << 12) |
               ((octet[2] - TigrPeaksEncoder.ENCODING_ORIGIN) <<  6) |
               ((octet[3] - TigrPeaksEncoder.ENCODING_ORIGIN)      );
        
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.flim.projectdb.PositionsEncoder#encode(short[])
     */
    public static String encode(short[] positions)
    {
        final StringBuilder encoded = new StringBuilder();
        
        short last = 0;
        for (short current : positions)
        {
            final short delta = (short)(current - last);
            
            if (delta > TigrPeaksEncoder.MAX_BARE_DELTA ||
                delta < 0)
            {
                encoded.append(TigrPeaksEncoder.FLAG_CHAR);
                encoded.append(uuencode(delta));
            }
            else
            {
                encoded.append((char)(delta + TigrPeaksEncoder.ENCODING_ORIGIN));
            }
            
            // Update the last position
            last = current;
        }

        // Add a terminator
        encoded.append(TigrPeaksEncoder.TERMINATOR_CHAR);
        
        return encoded.toString();
    }
    public static short[] decode(CharSequence encodedString){
        return decode(encodedString, DECODE_FULL_LENGTH);
    }
    /* (non-Javadoc)
     * @see org.jcvi.flim.projectdb.PositionsEncoder#decode(java.lang.CharSequence)
     */
    public static short[] decode(CharSequence encodedString, int numberOfPeaksToDecode) throws IllegalEncodedValueException
    {
        
        sanityCheckEncodedString(encodedString);
        final CharBuffer encoded = CharBuffer.wrap(encodedString);
       
        /*
         * NOTE: There is some inaccuracy here.  Instead of taking the time to
         * calculate just how many positions there will be, we assume that the
         * string length cannot be longer than the number of positions and that
         * the difference is not excessive.
         * 
         * dkatzel - made allocation remaining() -1 because we dont want to count the
         * terminator
         */
        final ShortBuffer decoded = ShortBuffer.allocate(encoded.remaining()-1);
        
        short position = 0;
        while(encoded.hasRemaining() && decoded.position() != numberOfPeaksToDecode)
        {
            final char codedChar = encoded.get();
            
            int delta;
            if (codedChar == TigrPeaksEncoder.FLAG_CHAR)
            {
                char[] codedDelta = new char[4];
                encoded.get(codedDelta);
                delta = uudecode(codedDelta);
                
                //handle negative deltas
                if (((delta >> 16) & 0xff) == 0xff) {
                    
                    delta = (delta & 0xffff) - 65536;
                }
            }
            else if (codedChar == TigrPeaksEncoder.TERMINATOR_CHAR)
            {
                // Break out of the loop
                break;
            }
            else
            {
                /*
                 * We've got a standard character.  Decode it.
                 */
                delta = codedChar - TigrPeaksEncoder.ENCODING_ORIGIN;
            }
            
            /*
             * Check to see that delta makes sense
             */
            // TODO Check the range
            
            /*
             * Calculate the new position
             */
            position += delta;
            
            /*
             * Add it to the buffer
             */
            decoded.put(position);
        }
        
        decoded.flip();
        //if there are uuencoded deltas, then there will be
        //trailing 0's in the full array (the # is capacity-limit)
        //lop those off
        return Arrays.copyOfRange(decoded.slice().array(), 0, decoded.limit());
    }

    /**
     * Perform value and constraint checking on the encoded value.
     * 
     * @param encodedString The {@link String} value.
     * @throws IllegalEncodedValueException If the value contains illegal 
     * characters or no data at all.
     */
    private static void sanityCheckEncodedString(CharSequence encodedString)
            throws IllegalEncodedValueException {
        if(encodedString ==null){
            throw new NullPointerException("encodedString can not be null");
        }
        if(encodedString.length()==0){
            throw new IllegalEncodedValueException("EncodedString can not be empty");
        }
        if(encodedString.charAt(encodedString.length()-1) !=TigrPeaksEncoder.TERMINATOR_CHAR ){
            throw new IllegalEncodedValueException("EncodedString must end with terminator char");
        }
    }
}
