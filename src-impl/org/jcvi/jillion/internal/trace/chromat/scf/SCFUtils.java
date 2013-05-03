/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 15, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf;

import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Section;

/**
 * <code>SCFUtils</code> is an Utility class
 * that contains helper methods for SCF Parsing
 * and Encoding.
 * @author dkatzel
 *
 *
 */
public final class SCFUtils {

    
    /**
     * This is the size in bytes of the SCF Header
     * as defined in the SCF File Format Specification.
     */
    public static final int HEADER_SIZE = 128;
    /**
     * This is the order the {@link Section}s
     * should be encoded in a SCF File.
     */
    public static final List<Section> ORDER_OF_SECTIONS =
                                                Arrays.asList(Section.SAMPLES,
                                                Section.BASES,
                                                Section.COMMENTS,
                                                Section.PRIVATE_DATA);


    /**
     * Magic number of a SCF Header that tells parser
     * that this is indeed an SCF file.
     * The actual values are ASCII for <code>.scf</code>.
     */
    private static final byte[] MAGIC_NUMBER = new byte[]{0x2E,0x73,0x63,0x66};
    /**
     * private constructor so no one can create it.
     */
    private SCFUtils(){
    	//can not instantiate
    }
    /**
     * Get the magic number that all SCF encoded
     * files need to have in its header
     * to declare itself an SCF encoded file.
     * @return the magic number as a byte array.
     */
    public static final byte[] getMagicNumber(){
    	//defensive copy since java arrays are mutable even if declared final
    	//(someone can still modify the contents just not the size)
        byte[] ret = new byte[MAGIC_NUMBER.length];
        System.arraycopy(MAGIC_NUMBER, 0, ret, 0, ret.length);
        return ret;
    }
    /**
     * Is the given byte array a valid SCF magic number?
     * @param magicNumber the magic number as a byte array.
     * @return {@code true} if the magic number is correct;
     * {@code false} otherwise.
     */
    public static boolean isMagicNumber(byte[] magicNumber){
        return Arrays.equals(MAGIC_NUMBER, magicNumber);
    }
    /**
     * my own implementation of SCF's delta delta algorithm.
     * <br/>psuedo code:
     * <pre>position[i]+= 2*position[i-1]-position[i-2]</pre>
     * I think mine is more clear than the "fast" version provided
     * in IO_Lib's misc_scf.c which is:
     * <pre>
        uint_2 p_sample1, p_sample2;
        p_sample1 = p_sample2 = 0;
        for (i = 0; i < num_samples; i++) {
            &nbsp;&nbsp;&nbsp;p_sample1  = p_sample1 + samples[i];
            &nbsp;&nbsp;&nbsp;samples[i] = p_sample1 + p_sample2;
            &nbsp;&nbsp;&nbsp;p_sample2  = samples[i];
        }
        </pre>
     * @param positions the positions data to be encoded.
     */
    public static void deltaDeltaDecode(short[] positions) {
        //special cases when i<2;
        // i=0 is not changed
        //i=1 can only take into account the previous
        //position since there is no index =-1
        positions[1]+= 2*positions[0];
        //now handle where i>=2
        for(int i=2; i<positions.length; i++){
            positions[i] += (short)(2*positions[i-1]-positions[i-2]);
        }
    }

    public static ShortBuffer deltaDeltaEncode(ShortBuffer original){
        ShortBuffer buffer=copy(original);
        for(int i=buffer.limit()-1; i>1; i--){
            final short deltaDelta = (short)(buffer.get(i) - 2*buffer.get(i-1)+buffer.get(i-2));
            buffer.put(i, deltaDelta);
        }
        //special case i=1
        buffer.put(1, (short)(buffer.get(1) - 2*buffer.get(0)));
        //leave i=0 as is
        //reset position to beginning.
         return (ShortBuffer)buffer.rewind();
    }
    public static ShortBuffer deltaDeltaEncode(PositionSequence original){
    	ShortBuffer buffer = ShortBuffer.allocate((int)original.getLength());
    	for(Position pos : original){
    		buffer.put(IOUtil.toSignedShort(pos.getValue()));
    	}
    	buffer.rewind();
    	return  deltaDeltaEncode(buffer);
    }
    public static short[] deltaDeltaEncode(short[] original){
        return deltaDeltaEncode(ShortBuffer.wrap(original)).array();
    }

    public static ShortBuffer copy(ShortBuffer original) {
        ShortBuffer aCopy=ShortBuffer.allocate(original.remaining());
        aCopy.put(original);
        return (ShortBuffer)aCopy.rewind();
    }

}
