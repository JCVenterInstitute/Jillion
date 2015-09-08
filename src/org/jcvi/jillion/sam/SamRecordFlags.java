/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam;

import java.util.EnumSet;
import java.util.Set;
/**
 * {@code SamRecordFlags} is an object representation
 * of the SAM bit flags column for each record.
 * @author dkatzel
 *
 */
public enum SamRecordFlags {

	/*
	 * 0x1 template having multiple segments in sequencing
0x2 each segment properly aligned according to the aligner
0x4 segment unmapped
0x8 next segment in the template unmapped
0x10 SEQ being reverse complemented
0x20 SEQ of the next segment in the template being reversed
0x40 the first segment in the template
0x80 the last segment in the template
0x100 secondary alignment
0x200 not passing quality controls
0x400 PCR or optical duplicate
0x800 supplementary alignment
	 */
	
	//primary alignment flag = 100100000000 0x900 & FLAG = 0
	//0x800 = supp align	   100000000000
	//0x100 secondary align 	  100000000
	//                             10100011
	//                              1010011
	//                         100000010000
	/**
	 *  Notes that the read multi-mapped
	 * and so will have multiple alignment records.
	 */
	HAS_MATE_PAIR(1),
	EACH_SEGMENT_PROPERLY_ALIGNED(2),
	/**
	 * This is the only reliable place to 
	 * tell whether the read is unmapped. If {@link READ_UNMAPPED} is set,
	 *  no assumptions can be made about {@link SamRecord#getReferenceName()},
	 *  {@link SamRecord#getStartOffset()}, {@link SamRecord#getCigar()},
	 *  {@link SamRecord#getMappingQuality()} and the {@link SamRecordFlags} :
	 *  {@link EACH_SEGMENT_PROPERLY_ALIGNED}, {@link REVERSE_COMPLEMENTED},
	 *  {@link SECONDARY_ALIGNMENT}, {@link SUPPLEMENTARY_ALIGNMENT}
	 *  and {@link MATE_REVERSE_COMPLEMENTED}
	 * of the previous read in the template.
	 */
	READ_UNMAPPED(4),
	/**
	 * Does this read's mate have its
	 * {@link #READ_UNMAPPED} flag set.
	 */
	MATE_UNMAPPED(8),
	/**
	 * Is this read reverse complemented.
	 * If this flag is present,
	 * then this read's {@link org.jcvi.jillion.core.Direction}
	 * is {@link org.jcvi.jillion.core.Direction#REVERSE};
	 * otherwise it's  {@link org.jcvi.jillion.core.Direction#FORWARD}.
	 */
	REVERSE_COMPLEMENTED(0x10),
	/**
	 * Does this read's mate have its
	 * {@link #REVERSE_COMPLEMENTED} flag set.
	 */
	MATE_REVERSE_COMPLEMENTED(0x20),
	/**
	 * This the first read of a mate 
	 * pair.
	 */
	FIRST_MATE_OF_PAIR(0x40),
	/**
	 * This the second read of a mate pair.
	 */
	SECOND_MATE_OF_PAIR(0x80),
	/**
	 * Marks the alignment not to be used in certain 
	 * analyses when the tools in use are
	 * aware of this. It is typically used to 
	 * flag alternative mappings when multiple mappings
	 * are presented in a SAM.
	 */
	SECONDARY_ALIGNMENT(0x100),
	/**
	 * This read has failed some kind
	 * of QC check.
	 */
	FAILED_QC(0x200),
	DUPLICATE(0x400),
	/**
	 * Indicates that the corresponding alignment line
	 *  is part of a chimeric alignment.
	 *  A line flagged with this is called as a supplementary line.
	 */
	SUPPLEMENTARY_ALIGNMENT(0x800)
	
	;
	
	private final short value;

	private SamRecordFlags(int value) {
		this.value = (short) value;
	}
	
	public boolean matches(int bitflags){
		return value == (bitflags & value);
	}
	
	/**
	 * Parse the given bit flags (as an int)
	 * into the equivalent Set of {@link SamRecordFlags}.
	 * The input of this method should be
	 * the output of {@link #asBits(Set)}
	 * and vice versa.
	 * @param bitFlags the bitFlag representation as an int;
	 * msut be >=0.
	 * @return a Set of {@link SamRecordFlags};
	 * will never be null but may be empty
	 * if no flags are set (bitFlags ==0 ).
	 * @throws IllegalArgumentException if bitFlags is negative.
	 * @see #asBits(Set)
	 */
	public static Set<SamRecordFlags> parseFlags(int bitFlags){
		if(bitFlags <0){
			throw new IllegalArgumentException("bit flags value can not be negative");
		}
		EnumSet<SamRecordFlags> set = EnumSet.noneOf(SamRecordFlags.class);
		for(SamRecordFlags flag : values()){
			if(flag.matches(bitFlags)){
				set.add(flag);
			}
		}
		return set;
	}
	/**
	 * Compute the integer bit flag value
	 * of the given Set of {@link SamRecordFlags}.
	 * The input of this method should be
	 * the output of {@link #parseFlags(int)}
	 * and vice versa.
	 * @param flags the flags to set;
	 * can not be null
	 * @return an int, will never be < 0.
	 * @throws NullPointerException if flags is null.
	 * @see SamRecordFlags#parseFlags(int)
	 */
	public static int asBits(Set<SamRecordFlags> flags){
		int value=0;
		for(SamRecordFlags flag : flags){
			value |= flag.value;
		}
		return value;
	}
}
