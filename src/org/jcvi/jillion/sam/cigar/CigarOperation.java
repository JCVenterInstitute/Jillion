package org.jcvi.jillion.sam.cigar;

public enum CigarOperation {
	/**
	 * Alignment match
	 * (can be a sequence match
	 * or mismatch).
	 */
	ALIGNMENT_MATCH('M'),
	/**
	 * Insertion to the reference. This causes the reference
	 * to insert a gap.  
	 * <p>
	 * For example: this read has an insertion of 2 bp
	 * with respect to the reference:
	 * <pre>
	 * ref		TTAGTAA**GATA
	 * read		TTAGTAAAGGATA
	 * </pre>
	 * </p>
	 */
	INSERTION('I'),
	/**
	 * Insertion from the reference. This causes the READ
	 * to insert a gap.  
	 * <p>
	 * For example: this read has an deletion of 1 bp
	 * with respect to the reference:
	 * <pre>
	 * ref		GATAGCTG
	 * read		GATA*CTG
	 * </pre>
	 * </p>
	 */
	DELETION('D'),
	/**
	 * Skipped region from the reference.
	 * For mRNA-to-genome alignments, this is an intron;
	 * for all other types of alignments, this operation
	 * is undefined.
	 */
	SKIPPED('N'),
	/**
	 * Clipped sequences present in the sequence.
	 */
	SOFT_CLIP('S'),
	/**
	 * Clipped sequences NOT present in the sequence.
	 */
	HARD_CLIP('H'),
	/**
	 * Silent deletion from padded reference. This is 
	 * caused when the read AND the reference both have a 
	 * deletion.  This is caused by another read
	 * giving the reference an insertion:
	 * <p>
	 * For example: this read2 has an padd 1 bp
	 * at position 8:
	 * <pre>
	 * ref		TTAGTAA**GATA
	 * 
	 * read1		TTAGTAAAGGATA
	 * read2		TTAGTAA*GGATA
	 * </pre>
	 */
	PADDING('P'),
	/**
	 * Sequence match.
	 */
	SEQUENCE_MATCH('='),
	/**
	 * Sequence mismatch.
	 */
	SEQUENCE_MISMATCH('X')
	;
	private static final CigarOperation[] VALUES = values();
	private final char opCode;
	
	CigarOperation(char opCode){
		this.opCode = opCode;
	}
	
	
	
	public char getOpCode() {
		return opCode;
	}

	public static CigarOperation parseBinary(int bit){
		return VALUES[bit];
	}

	/**
	 * Parse the first character of the given String into a
	 * {@link CigarOperation}. This is the same as
	 * {@link #parseOp(char) parseOp(op.charAt(0));}
	 * @param op the opcode to parse.
	 * @return the {@link CigarOperation};
	 * will never return null.
	 * @throws IllegalArgumentException if the character
	 * is an invalid opcode.
	 * @throws NullPointerException if op is null.
	 */
	public static CigarOperation parseOp(String op){
		return parseOp(op.charAt(0));
	}
	/**
	 * Parse the given character into a
	 * {@link CigarOperation}.
	 * @param op the opcode to parse.
	 * @return the {@link CigarOperation};
	 * will never return null.
	 * @throws IllegalArgumentException if the character
	 * is an invalid opcode.
	 */
	public static CigarOperation parseOp(char op){
		//This is an optimization to allow the 
    	//compiler to use a tableswitch opcode
    	//instead of the more general purpose
    	//lookupswitch opcode.
    	//tableswitch is an O(1) lookup
    	//while lookupswitch is O(n) where n
    	//is the number of case statements in the switch.
    	//tableswitch requires consecutive case values.
    	//DO NOT CHANGE THE ORDER OF THE CASE STATEMENTS
    	//the cases have to be in ascii order
    	//so the JVM can do offset arithmetic
    	//to jump immediately to the correct case
    	//for an O(1) lookup, changing the order
    	//might not allow that. 
    	//(not sure if compiler is smart enough to re-order)
    	//
    	//for more information:
    	//The book Beautiful Code Chapter 6
    	//or
    	//http://www.artima.com/underthehood/flowP.html
		switch(op){
			
			case '=': return SEQUENCE_MATCH;
			case '>': break;
			case '?': break;
			case '@': break;
			
			case 'A': break;
			case 'B': break;
			case 'C': break;
			case 'D': return DELETION;
			case 'E': break;
			case 'F': break;
			case 'G': break;
			case 'H': return HARD_CLIP;
			case 'I': return INSERTION;
			case 'J': break;
			case 'K': break;
			case 'L': break;
			case 'M': return ALIGNMENT_MATCH;
			case 'N': return SKIPPED;
			case 'O': break;
			case 'P': return PADDING;
			case 'Q': break;
			case 'R': break;
			case 'S': return SOFT_CLIP;
			case 'T': break;
			case 'U': break;
			case 'V': break;
			case 'W': break;
			case 'X': return SEQUENCE_MISMATCH;

			
			
			default: break;
		}
		throw new IllegalArgumentException("invalid cigar opcode : " + op);
	}
}
