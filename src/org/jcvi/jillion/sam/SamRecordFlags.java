package org.jcvi.jillion.sam;

import java.util.EnumSet;

public enum SamRecordFlags {

	/*
	 * 0x1 template having multiple segments in sequencing
0x2 each segment properly aligned according to the aligner
0x4 segment unmapped
0x8 next segment in the template unmapped
0x10 SEQ being reverse complemented
0x20 SEQ of the next segment in the template being reversed
0x40 the rst segment in the template
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
	HAS_MULT_SEGMENTS(1),
	EACH_SEGMENT_PROPERLY_ALIGNED(2),
	/**
	 * This is the only reliable place to 
	 * tell whether the read is unmapped. If {@link UNMAPPED} is set,
	 *  no assumptions can be made about RNAME, POS, CIGAR, MAPQ, bits 
	 *  {@link EACH_SEGMENT_PROPERLY_ALIGNED}, {@link REVERSE_COMPLEMENTED},
	 *  {@link SECONDARY_ALIGNMENT}, {@link SUPPLEMENTARY_ALIGNMENT}
	 *  and {@link NEXT_SEGMENT_IN_TEMPLATE_REVERSE_COMPLEMENTED}
	 * of the previous read in the template.
	 */
	UNMAPPED(4),
	NEXT_SEGMENT_IN_TEMPLATE_UNMAPPED(8),
	REVERSE_COMPLEMENTED(0x10),
	NEXT_SEGMENT_IN_TEMPLATE_REVERSE_COMPLEMENTED(0x20),
	FIRST_SEGMENT_IN_TEMPLATE(0x40),
	LAST_SEGMENT_IN_TEMPLATE(0x80),
	/**
	 * Marks the alignment not to be used in certain 
	 * analyses when the tools in use are
	 * aware of this. It is typically used to 
	 * flag alternative mappings when multiple mappings
	 * are presented in a SAM.
	 */
	SECONDARY_ALIGNMENT(0x100),
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
	
	public static EnumSet<SamRecordFlags> parseFlags(int bitFlags){
		EnumSet<SamRecordFlags> set = EnumSet.noneOf(SamRecordFlags.class);
		for(SamRecordFlags flag : values()){
			if(flag.matches(bitFlags)){
				set.add(flag);
			}
		}
		return set;
	}
}
