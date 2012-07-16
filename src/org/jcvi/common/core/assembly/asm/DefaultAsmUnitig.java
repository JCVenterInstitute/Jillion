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

package org.jcvi.common.core.assembly.asm;

import java.util.Set;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultAsmUnitig implements AsmUnitig{

    private final Contig<AsmPlacedRead> delegate;

    public DefaultAsmUnitig(Contig<AsmPlacedRead> delegate) {
        this.delegate = delegate;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public String getId() {
        return delegate.getId();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfReads() {
        return delegate.getNumberOfReads();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<AsmPlacedRead> getReadIterator() {
        return delegate.getReadIterator();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideSequence getConsensusSequence() {
        return delegate.getConsensusSequence();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public AsmPlacedRead getRead(String id) {
        return delegate.getRead(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean containsRead(String placedReadId) {
        return delegate.containsRead(placedReadId);
    }
    
    
}
