/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
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
 * 
 */
package org.jcvi.common.core.seq.fastx.fasta;

import org.jcvi.common.core.seq.fastx.FastXRecord;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;



/**
 * {@code FastaRecord} is an interface for interacting
 * with a single FASTA record.
 * @param <T> the type used as the value of the record
 * @author jsitz
 * @author dkatzel
 */
public interface FastaRecord<S extends Symbol,T extends Sequence<S>> extends FastXRecord<S,T>
{
    
    /**
     * Gets the entire formatted fasta record as a {@link String}.
     * The format slightly varies by implementation but in general
     * will look like:
     * <pre>
     * >id optional-comment
     * multi-line 
     * sequence
     * </pre>
     * The last character in the string is a line separator
     * character.
     * @return this fasta record formatted in fasta format.
     * 
     */
    String toFormattedString();
    /**
     * Delegates to {@link #toFormattedString()}.
     * @return a correctly formatted FASTA record
     * as a String.
     */
    @Override
    String toString();
    /**
     * Two FastaRecords are equal
     * if they both have the same id
     * and the same sequence.
     */
    @Override
    public boolean equals(Object o);
}
