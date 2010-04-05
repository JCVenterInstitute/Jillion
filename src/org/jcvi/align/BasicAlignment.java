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
 * Alignment.java
 *
 * Created: Aug 10, 2009 - 2:42:38 PM (jsitz)
 *
 * Copyright 2009 J. Craig Venter Institute
 */
package org.jcvi.align;


/**
 * A <code>BasicAlignment</code> is a simple implementation of the {@link Alignment} interface
 * intended for use in simple reference-query alignments.
 *
 * @author jsitz@jcvi.org
 */
public class BasicAlignment implements Alignment
{
    /** The sequence alignment data for the query sequence. */
    private final SequenceAlignment queryAlignment;
    /** The sequence alignment data for the reference sequence. */
    private final SequenceAlignment referenceAlignment;
    /** The absolute score for the alignment. */
    private final double score;
    /** The percentage identity for the alignment. */
    private final double identity;
    /** The percentage match for the alignment. */
    private final double match;
    
    /**
     * Creates a new <code>BasicAlignment</code>.
     * 
     * @param queryAlignment The {@link SequenceAlignment} for the query sequence.
     * @param referenceAlignment The {@link SequenceAlignment} for the reference sequence.
     * @param score The alignment score.
     * @param identity The percentage identity as a double-encoded percentage greater than or
     * equal to <code>0.0</code> and less than or equal to <code>1.0</code>.
     * @param match The match score percentage as a double-encoded percentage greater than or
     * equal to <code>0.0</code> and less than or equal to <code>1.0</code>.
     */
    public BasicAlignment(SequenceAlignment queryAlignment,
            SequenceAlignment referenceAlignment, double score, double identity, double match)
    {
        super();
        
        this.queryAlignment = queryAlignment;
        this.referenceAlignment = referenceAlignment;
        this.score = score;
        this.identity = identity;
        this.match = match;
    }

    /* (non-Javadoc)
     * @see org.jcvi.align.Alignment#getQueryAlignment()
     */
    public SequenceAlignment getQueryAlignment()
    {
        return this.queryAlignment;
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.Alignment#getReferenceAlignment()
     */
    public SequenceAlignment getReferenceAlignment()
    {
        return this.referenceAlignment;
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.Alignment#getIdentity()
     */
    public double getIdentity()
    {
        return this.identity;
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.Alignment#getMatch()
     */
    @Override
    public double getMatch() 
    {
        return this.match;
    }
    
    /* (non-Javadoc)
     * @see org.jcvi.align.Alignment#getScore()
     */
    public double getScore()
    {
        return this.score;
    }
}
