/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.cigar;
/**
 * {@code CigarOperation} is an enumeration
 * of all the different kinds of trimming and alignment
 * operations that can be expressed in a CIGAR string.
 * 
 * @author dkatzel
 *
 */
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
	SOFT_CLIP('S'){

		@Override
		public boolean isClip() {
			return true;
		}
		
	},
	/**
	 * Clipped sequences NOT present in the sequence.
	 */
	HARD_CLIP('H'){

		@Override
		public boolean isClip() {
			return true;
		}
		
	},
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
	
	private static final CigarOperation[] SAM_CHARS;
	
	static{
		//from '=' to 'X' is 28 elements
		SAM_CHARS = new CigarOperation[28];
		for(int i=0; i<VALUES.length; i++){
			CigarOperation cigarOperation = VALUES[i];
			char opCode = cigarOperation.opCode;
			int offset = opCode - '=';
			SAM_CHARS[offset] = cigarOperation;
		}
	}
	private final char opCode;
	
	CigarOperation(char opCode){
		this.opCode = opCode;
	}
	
	
	/**
	 * Get this opCode for this operation
	 * that is used in SAM files.
	 * 
	 * @return an opCode as a single character.
	 */
	public char getOpCode() {
		return opCode;
	}
	/**
	 * Get this opCode for this operation
	 * that is used in BAM files.
	 * 
	 * @return an opCode as an int.
	 */
	public int getBinaryOpCode() {
		return ordinal();
	}
	/**
	 * Parse the binary op code from a BAM encoded
	 * file into the {@link CigarOperation}.
	 * @param bit the binary opcode;
	 * @return the {@link CigarOperation}
	 * 
	 * @throws IndexOutOfBoundsException if the opcode is invalid.
	 */
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
		int offset = op - '=';
		if(offset >=0 && offset < SAM_CHARS.length){
			CigarOperation cigarOperation = SAM_CHARS[offset];
			if(cigarOperation !=null){
				return cigarOperation;
			}
		}
		throw new IllegalArgumentException("invalid cigar opcode : " + op);
	}
	/**
	 * Is a Clipping Operation.
	 * @return {@code true} for {@link #HARD_CLIP} and {@link #SOFT_CLIP}
	 * {@code false} for everything else.
	 * 
	 * @since 6.0
	 */
	public boolean isClip() {
		return false;
	}
}
