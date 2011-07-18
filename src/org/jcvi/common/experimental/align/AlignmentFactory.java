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
 * AlignmentFactory.java
 *
 * Created: Aug 11, 2009 - 3:47:17 PM (jsitz)
 *
 * Copyright 2009 J. Craig Venter Institute
 */
package org.jcvi.common.experimental.align;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;

/**
 * An <code>AlignmentFactory</code> is a factory object capable of collecting information needed
 * to build {@link Alignment} object.  The factory pattern allows the {@link Alignment} objects
 * to be immutable and thread-safe despite the often complicated process of discovering the full
 * set of data needed to describe the alignment.
 * <p>
 * <em>Note:</em> Each class instance keeps internal state containing all reported data.  Thus,
 * the object is explicitly <em>not</em> thread-safe.
 *
 * @author jsitz@jcvi.org
 */
public class AlignmentFactory
{
    /** The coordinate in the query sequence where the alignment begins. */
    private int queryStart;
    /** The coordinate in the query sequence where the alignment ends. */
    private int queryStop;
    /** The full length of the query sequence. */
    private int queryLength;
    /** The coordinate in the reference sequence where the alignment begins. */
    private int refStart;
    /** The coordinate in the reference sequence where the alignmetn ends. */
    private int refStop;
    /** The full length of the reference sequence. */
    private int refLength;
    
    /** The absolute, ungapped, space-based location of the last gap reported in the query. */
    private int lastQueryGap;
    /** A {@link List} of gaps in the query sequence when aligned against the reference. */
    private final List<Integer> queryGaps;
    
    /** The absolute, ungapped, space-based location of the last gap reported in the reference. */
    private int lastReferenceGap;
    /** A {@link List} of gaps in the reference sequence when aligned against the query. */
    private final List<Integer> referenceGaps;
    
    /** The score assigned to the alignment. */
    private double score;
    /** The identity score of the alignment. */
    private double identity;
    /** The percentage match for the alignment. */
    private double match;
    
    /**
     * Creates a new <code>AlignmentFactory</code>.
     */
    public AlignmentFactory()
    {
        super();
        
        this.queryGaps = new ArrayList<Integer>();
        this.referenceGaps = new ArrayList<Integer>();
        
        this.reset();
    }
    
    /**
     * Resets the internal state of this factory.  This method should be called each time a 
     */
    public void reset()
    {
        this.lastQueryGap = 0;
        this.queryGaps.clear();
        
        this.lastReferenceGap = 0;
        this.referenceGaps.clear();
    }
    
    /**
     * Builds a new Alignment based on the data supplied.  Building an alignment does not clear
     * the internal state of this factory.  In order to clear the state and begin building a 
     * new alignment, the {@link #reset()} method should be used.
     * 
     * @return A new Alignment.
     */
    public Alignment build()
    {
        final Range queryRange = Range.buildRange(this.queryStart, this.queryStop);
        final SequenceAlignment queryAlignment = new SequenceAlignment(queryRange, 
                                                                       this.buildStaticGapList(this.queryGaps), 
                                                                       this.queryLength);
        
        final Range referenceRange = Range.buildRange(this.refStart, this.refStop);
        final SequenceAlignment referenceAlignment = new SequenceAlignment(referenceRange, 
                                                                           this.buildStaticGapList(this.referenceGaps),
                                                                           this.refLength);
        
        return new BasicAlignment(queryAlignment, referenceAlignment, this.score, this.identity, this.match);
    }
    
    /**
     * Adds a record for a gap in the query alignment which occurs at a specific offset from 
     * the most recent gap.  In the case that this is the first gap reported, its location is
     * considered to be relative to the space before the first base in the sequence.
     * 
     * @param offset The offset in bases from the last gap or the start of the sequence if no
     * previous gaps exist.
     */
    public void addRelativeQueryGap(int offset)
    {
        this.queryGaps.add(Integer.valueOf(offset));
        this.lastQueryGap += offset;
    }
    
    /**
     * Adds a record for a gap which occurs following the query location specified.  This
     * is the index of the space (in space-based coordinates) where the gap should be placed,
     * however, the offsets of any calls after this are not changed by the fact that the gap 
     * has been publicized.  All locations reported in this function must be reported as 
     * ungapped locations.
     * 
     * @param location The ungapped, space-based location of the gap to report.
     */
    public void addAbsoluteQueryGap(int location)
    {
        this.addRelativeQueryGap(location - this.lastQueryGap);
    }
    
    /**
     * Adds a record for a gap in the reference alignment which occurs at a specific offset from 
     * the most recent gap.  In the case that this is the first gap reported, its location is
     * considered to be relative to the space before the first base in the sequence.
     * 
     * @param offset The offset in bases from the last gap or the start of the sequence if no
     * previous gaps exist.
     */
    public void addRelativeReferenceGap(int offset)
    {
        this.referenceGaps.add(Integer.valueOf(offset));
        this.lastReferenceGap += offset;
    }
    
    /**
     * Adds a record for a gap which occurs following the reference location specified.  This
     * is the index of the space (in space-based coordinates) where the gap should be placed,
     * however, the offsets of any calls after this are not changed by the fact that the gap 
     * has been publicized.  All locations reported in this function must be reported as 
     * ungapped locations.
     * 
     * @param location The ungapped, space-based location of the gap to report.
     */
    public void addAbsoluteReferenceGap(int location)
    {
        this.referenceGaps.add(Integer.valueOf(location));
    }
    
    /**
     * Sets the coordinate of the first base which aligns to the reference.
     * 
     * @param coord The 1-based coordinate of the base.
     */
    public void setQueryBegin(int coord)
    {
        this.queryStart = coord;
    }
    
    /**
     * Sets the coordinate of the last base which aligns to the reference.  If this is set to
     * a value beyond the recorded length of the sequence, the length will be extended to cover
     * at least this range of the sequence.
     * 
     * @param coord The 1-based coordinate of the base.
     */
    public void setQueryEnd(int coord)
    {
        this.queryStop = coord;
        if (coord > this.queryLength){
            this.setQueryLength(coord);
        }
    }
    
    /**
     * Sets the coordinate of the first base which aligned with the query.
     * 
     * @param coord The 1-based coordinate of the base.
     */
    public void setReferenceBegin(int coord)
    {
        this.refStart = coord;
    }
    
    /**
     * Sets the coordinate of teh last base which aligned with the query. If this is set to
     * a value beyond the recorded length of the sequence, the length will be extended to cover
     * at least this range of the sequence.
     * 
     * @param coord The 1-based coordinate of the base.
     */
    public void setReferenceEnd(int coord)
    {
        this.refStop = coord;
        if (coord > this.refLength){
            this.setReferenceLength(coord);
        }
    }
    
    /**
     * Sets the full length of the query sequence.
     * 
     * @param queryLength The full length of the sequence, in bases.
     */
    public void setQueryLength(int queryLength)
    {
        this.queryLength = queryLength;
    }
    
    /**
     * Sets the full lenght of the reference sequence.
     * 
     * @param refLength The full length of the sequence, in bases.
     */
    public void setReferenceLength(int refLength)
    {
        this.refLength = refLength;
    }
    
    /**
     * Sets the alignment score for this alignment.
     * 
     * @param score The score of this alignment, as a <code>double</code>.
     */
    public void setScore(double score)
    {
        this.score = score;
    }
    
    /**
     * Sets the alignment score for this alignment.  This method accepts a <code>long</code>,
     * but the native storage is done in the more accepting <code>double</code> format.  The
     * score provided here will be converted to a <code>double</code> along with the implicit
     * possible loss of precision.  However, as scores are not declared on an absolute scale, 
     * only comparability needs to be preserved, and in no case does the conversion from 
     * <code>long</code> to <code>double</code> alter result of the <em>greater-than</em> or
     * <em>less-than</em> operations.  However, the <em>equals</em> operation cannot be 
     * guaranteed to work when comparing the converted value.
     * 
     * @param score The score of this alignment, as a <code>long</code>.
     */
    public void setScore(long score)
    {
        this.score = score;
    }
    
    /**
     * Sets the identity score of the alignment.  This is normally defined as the number of 
     * locations in the alignment which have identical values in both the reference and the
     * query divided by the total number of locations.
     * 
     * @param identity The alignment identity.
     */
    public void setIdentity(double identity)
    {
        this.identity = identity;
    }
    
    /**
     * Sets the match score of the alignment.  This is calculated in a similar manner to the 
     * {@link #setIdentity(double) identity} value, but it provides for partial scoring to 
     * matches against ambiguous bases.
     * 
     * @param match The match score for the alignment.
     */
    public void setMatch(double match)
    {
        this.match = match;
    }
    
    /**
     * Builds an array-based gap list based on the supplied Collections-based gap list.
     * 
     * @param gaps A {@link List} of delta-encoded {@link Integer} gaps.
     * @return An array of integers, containing the delta-encoded gaps.
     */
    protected int[] buildStaticGapList(List<Integer> gaps)
    {
        final int[] gapArray = new int[gaps.size()];
        
        for (int i = 0; i < gapArray.length; i++)
        {
            gapArray[i] = gaps.get(i).intValue();
        }
        
        return gapArray;
    }
}
