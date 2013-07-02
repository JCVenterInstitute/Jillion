package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface TranslationTable {

	   public static enum Frame{
	        ZERO(0),
	        ONE(1),
	        TWO(2);
	        
	        private int frame;
	        
	        public  final int getFrame() {
	            return frame;
	        }
	        Frame(int frame){
	            this.frame = frame;
	        }
	        /**
	         * Parse a {@link Frame} from the given int value.
	         * Valid values are <code>0</code> to <code>2</code>
	         * inclusive.
	         * @param frame
	         * @return a {@link Frame}
	         * @throws IllegalArgumentException if <code> frame < 0 || frame > 2</code>
	         */
	        public static Frame parseFrame(int frame){
	            for(Frame f : Frame.values()){
	                if(f.frame == frame){
	                    return f;
	                }
	            }
	         
	            throw new IllegalArgumentException("unable to parse frame " + frame);
	        }
	    }
	/**
	 * Convenience method for {@link #translate(NucleotideSequence, Frame)}
	 * using {@link Frame#ZERO}.
	 * @param sequence
	 * @return
	 */
	AminoAcidSequence translate(NucleotideSequence sequence);
	/**
	 * Translate the given <strong>ungapped</strong> {@link NucleotideSequence} into
	 * an {@link AminoAcidSequence} using the given {@link Frame}.  If the sequence
	 * in the given frame is not a multiple of 3, then this method will
	 * translate as many bases as possible, any "left over" bases will not be translated.
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
	 * @param frame the Frame to use; can not be null.
	 * @return a new AminoAcidSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 */
	AminoAcidSequence translate(NucleotideSequence sequence, Frame frame);
}
