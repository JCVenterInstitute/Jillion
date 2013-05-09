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
package org.jcvi.jillion.assembly.ca.asm;

import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
final class DefaultAsmUnitig implements AsmUnitig{

    private final Contig<AsmAssembledRead> delegate;

    public DefaultAsmUnitig(Contig<AsmAssembledRead> delegate) {
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
    public long getNumberOfReads() {
        return delegate.getNumberOfReads();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<AsmAssembledRead> getReadIterator() {
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
    public AsmAssembledRead getRead(String id) {
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
