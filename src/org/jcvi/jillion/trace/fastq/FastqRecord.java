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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.Trace;
/**
 * {@code FastqRecord} is an object representation 
 * of a read from a fastq encoded file.
 * @author dkatzel
 *
 */
public interface FastqRecord extends Trace{
    
	/**
     * 
     * Get the Id of this {@link FastqRecord}.
     * <strong>Note: </strong> It is possible that this
     * id has multiple "words" with whitespace in between
     * if this record was from a CASAVA 1.8 run.
     * This can cause problems with downstream software
     * if whitespace in ids is not allowed.
     * @return This id of this {@link FastqRecord} as a String
     */
	@Override
    String getId();
    /**
     * Gets the {@link NucleotideSequence} of this record.
     * The nucleotide sequence should be the same
     * length as the {@link QualitySequence} returned by
     * {@link #getQualitySequence()}.
     * @return a {@link NucleotideSequence} instance;
     * never null.
     */
	@Override
    NucleotideSequence getNucleotideSequence();
    /**
     * Gets the {@link QualitySequence} of this record.
     * The quality sequence should be the same
     * length as the {@link NucleotideSequence} returned by
     * {@link #getNucleotideSequence()}.
     * @return a {@link QualitySequence} instance;
     * never null.
     */
	@Override
    QualitySequence getQualitySequence();

    /**
     * Get the comment (if any) associated with this record.
     * @return A <code>String</code> of the comment
     * or {@code null} if there is no comment.
     */
    String getComment();

    /**
     * The HashCode of a {@link FastqRecord} is computed using
     * the id, {@link NucleotideSequence} and {@link QualitySequence}
     * values.
     * @return an int.
     */
    @Override
    int hashCode();
    /**
     * Two {@link FastqRecord}s are equal
     * if and only if they have equal
     * ids, {@link NucleotideSequence}s
     * and {@link QualitySequence}s.
     * Any comments returned by {@link #getComment()}
     * are ignored for equality testing.
     * @param obj
     * @return
     */
    @Override
    boolean equals(Object obj);
}
