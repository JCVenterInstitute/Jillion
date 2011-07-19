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
package org.jcvi.common.core.assembly.contig.ace;

import java.util.Date;

import org.jcvi.Range;
import org.jcvi.common.core.assembly.contig.ctg.AbstractContigFileVisitor;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.Glyph;
import org.jcvi.common.core.seq.Sequence;
import org.jcvi.common.core.seq.fastx.FastXRecord;
import org.jcvi.common.core.seq.read.SequenceDirection;

public abstract class AbstractAceAdaptedContigFileDataStore extends AbstractContigFileVisitor{

    private DefaultAceContig.Builder contigBuilder;
    private final Date phdDate;
    private final DataStore<? extends FastXRecord<? extends Sequence<? extends Glyph>>> fullLengthFastXDataStore;
    /**
     * Create a new AceAdapted Contig File DataStore using the given phdDate.
     * @param phdDate the date all faked phd files should be timestamped with.
     */
    public AbstractAceAdaptedContigFileDataStore(DataStore<? extends FastXRecord<? extends Sequence<? extends Glyph>>> fullLengthFastXDataStore,Date phdDate) {
        this.phdDate = new Date(phdDate.getTime());
        this.fullLengthFastXDataStore = fullLengthFastXDataStore;
    }

    @Override
    protected void visitBeginContig(String contigId, String consensus) {
        contigBuilder = new DefaultAceContig.Builder(contigId, consensus);
    }

    @Override
    protected void visitEndOfContig() {
        visitAceContig(contigBuilder.build());
        
    }

    @Override
    protected void visitRead(String readId, int offset, Range validRange,
            String basecalls, SequenceDirection dir) {
        
        PhdInfo info =new DefaultPhdInfo(readId, readId+".phd.1", phdDate);
        try {
            contigBuilder.addRead(readId, basecalls ,offset, dir, 
                    validRange ,info,
                    (int)(fullLengthFastXDataStore.get(readId).getValue().getLength()));
        } catch (DataStoreException e) {
            throw new IllegalStateException("error getting full length trace for "+ readId);
        }
        
    }

    protected abstract void visitAceContig(DefaultAceContig aceContig);
    

    
}
