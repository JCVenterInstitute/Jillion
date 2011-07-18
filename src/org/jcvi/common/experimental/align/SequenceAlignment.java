/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
 * SequenceAlignmentSpec.java
 *
 * Created: Aug 11, 2009 - 3:56:21 PM (jsitz)
 *
 * Copyright 2009 J. Craig Venter Institute
 */
package org.jcvi.common.experimental.align;

import java.nio.CharBuffer;
import java.util.Arrays;

import org.jcvi.Range;

/**
 * A <code>SequenceAlignment</code> records the specific alignment of one sequence against 
 * another sequence as discovered by an {@link Aligner}.  In a normal {@link Alignment}, there
 * would be two <code>SequenceAlignment</code>s, one for the query and one for the reference.
 * Each <code>SequenceAlignment</code> contains the range and gapping information for just one
 * half of the full alignment.  Thus, the range from one is interpretted with respect to the 
 * range of the other, and one <code>SequenceAlignment</code> alone means very little.
 *
 * @author jsitz@jcvi.org
 */
public class SequenceAlignment
{
    /** The range of this sequence which aligned to other sequences. */
    private final Range aligned;
    /** The gaps in the alignment of this sequence against the other sequences. */
    private final int[] gaps;
    /** The full length of the sequence. */
    private final int fullLength;
    
    /**
     * Creates a new <code>SequenceAlignmentSpec</code>.
     * 
     * @param alignedRange The range the sequence aligned to other sequences.
     * @param gaps The gaps in the alignment of this sequence to the other sequences.
     * @param fullLength The full length of the sequence being aligned.
     */
    public SequenceAlignment(Range alignedRange, int[] gaps, int fullLength)
    {
        super();
        
        this.aligned = alignedRange;
        this.gaps = Arrays.copyOf(gaps, gaps.length);
        this.fullLength = fullLength;
    }
    
    /**
     * Fetches the array of gaps in delta notation.  Each element in the gap array declares the 
     * location of a gap as the distance from the previous gap or the start of the sequence if
     * no previous gap exists.
     * 
     * @return An array of gaps in delta notation.
     */
    public int[] getGaps()
    {
        return Arrays.copyOf(this.gaps, gaps.length);
    }
    
    /**
     * Fetches the index of the first base (1s-based) in this sequence which aligned to the target 
     * sequences.
     * 
     * @return The integer index of the first aligning base.
     */
    public int getStart()
    {
        return (int)this.aligned.getLocalStart();
    }
    
    /**
     * Fetches the index of the last base (1s-based) in this sequence which aligned to the target 
     * sequences.
     * 
     * @return The integer index of the last aligning base.
     */
    public int getStop()
    {
        return (int)this.aligned.getLocalEnd();
    }
    
    /**
     * Calculates the length of the aligned section of the sequence.  This counts the number of
     * real entities in the sequence, not including the number of gaps inserted in order to
     * get the sequence to align.
     * 
     * @return The number of bases which aligned to the other sequences.
     */
    public int getAlignmentLength()
    {
        return (int)this.aligned.size();
    }
    
    /**
     * Fetches the full length of the source sequence in this alignment.
     * 
     * @return The full length of the sequence represented.
     */
    public int getFullLength()
    {
        return this.fullLength;
    }
    
    /**
     * Return a gapped version of the supplied sequence.  In order to have any meaning, this
     * must be the same sequence which acted as the subject of the alignment.  This is supplied
     * externally in order to allow the external code to control the memory usage policy, as
     * aligned sequence strings may get very large.
     * <p><em>Note:</em> The return value of this method is a {@link CharSequence} rather than
     * one of the comcrete implementations to allow different implementations to decide the
     * most efficient way to build the gapped sequence.  In practice, this interface provides
     * most of the API calls that people are actually interested in.  However, if a concrete
     * {@link String} truly is needed, then conversion using {@link CharSequence#toString()} 
     * is usually going to be fairly efficient.
     * 
     * @param seq The sequence to gap.
     * @return A gapped {@link CharSequence}.
     */
    public CharSequence getGappedSequence(CharSequence seq)
    {
        final CharBuffer buffer = CharBuffer.wrap(seq);
        
        buffer.position(this.getStart()-1);
        buffer.limit(this.getStop());
        
        final int gappedLength = buffer.remaining() + this.getGaps().length;
        final CharBuffer gapped = CharBuffer.allocate(gappedLength);
        
        for(final int gap : this.getGaps())
        {
            /*
             * Copy one character per gap offset character to the gapped result.
             */
            for(int i = 0; i < gap; i++)
            {
                gapped.put(buffer.get());
            }
            
            /*
             * Copy one gap character
             */
            gapped.put(Alignment.GAP_CHARACTER);
        }
        
        /*
         * Copy the remaining sequence
         */
        gapped.put(buffer);
        
        /*
         * Flip and return the buffer.
         */
        gapped.flip();
        return gapped;
    }
    
}
