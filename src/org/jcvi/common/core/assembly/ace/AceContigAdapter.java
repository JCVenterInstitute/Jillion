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

package org.jcvi.common.core.assembly.ace;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

/**
 * @author dkatzel
 *
 *
 */
public class AceContigAdapter implements AceContig{

    private final Contig<? extends PlacedRead> delegate;
    private final Date phdDate;
    private final boolean isComplimented;
    
    
    public AceContigAdapter(Contig<? extends PlacedRead> delegate, Date phdDate, boolean isComplimented) {
        this.delegate = delegate;
        this.phdDate = new Date(phdDate.getTime());
        this.isComplimented = isComplimented;
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
    public Set<AcePlacedRead> getPlacedReads() {
        Set<AcePlacedRead> set = new LinkedHashSet<AcePlacedRead>();
        for(PlacedRead r : delegate.getPlacedReads()){
            set.add(adaptPlacedRead(r.getId(), r));
        }
        return set;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideSequence getConsensus() {
        return delegate.getConsensus();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public AcePlacedRead getPlacedReadById(String id) {
        PlacedRead placedRead = delegate.getPlacedReadById(id);
        return adaptPlacedRead(id, placedRead);
    }

    AcePlacedRead adaptPlacedRead(String id, PlacedRead placedRead) {
        PhdInfo info =new DefaultPhdInfo(id, id+".phd.1", phdDate);
        return new AcePlacedReadAdapter(placedRead,info);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean containsPlacedRead(String placedReadId) {
        return delegate.containsPlacedRead(placedReadId);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isComplemented() {
        return isComplimented;
    }

}
