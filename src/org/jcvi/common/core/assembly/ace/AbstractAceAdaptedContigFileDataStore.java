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
/*
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.util.Date;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ctg.AbstractContigFileVisitor;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.FastXRecord;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;


public abstract class AbstractAceAdaptedContigFileDataStore extends AbstractContigFileVisitor{

    private AceContigBuilder contigBuilder;
    private final Date phdDate;
    private final DataStore<? extends FastXRecord> fullLengthFastXDataStore;
    /**
     * Create a new AceAdapted Contig File DataStore using the given phdDate.
     * @param phdDate the date all faked phd files should be timestamped with.
     */
    public AbstractAceAdaptedContigFileDataStore(DataStore<? extends FastXRecord> fullLengthFastXDataStore,Date phdDate) {
        this.phdDate = new Date(phdDate.getTime());
        this.fullLengthFastXDataStore = fullLengthFastXDataStore;
    }

    @Override
    protected void visitBeginContig(String contigId, String consensus) {
        contigBuilder = DefaultAceContig.createBuilder(contigId, consensus);
    }

    @Override
    protected void visitEndOfContig() {
        visitAceContig(contigBuilder.build());
        
    }

    @Override
    protected void visitRead(String readId, int offset, Range validRange,
            String basecalls, Direction dir) {
        
        PhdInfo info =new DefaultPhdInfo(readId, readId+".phd.1", phdDate);
        try {
            contigBuilder.addRead(readId, new NucleotideSequenceBuilder(basecalls).build() ,offset, dir, 
                    validRange ,info,
                    (int)(fullLengthFastXDataStore.get(readId).getSequence().getLength()));
        } catch (DataStoreException e) {
            throw new IllegalStateException("error getting full length trace for "+ readId);
        }
        
    }

    protected abstract void visitAceContig(AceContig aceContig);
    

    
}
