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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ace;

import java.util.Date;

import org.jcvi.jillion.assembly.ace.AceContig;
import org.jcvi.jillion.assembly.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.ace.PhdInfo;
import org.jcvi.jillion.assembly.ctg.AbstractContigFileVisitor;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaDataStore;


public abstract class AbstractAceAdaptedContigFileDataStore extends AbstractContigFileVisitor{

    private AceContigBuilder contigBuilder;
    private final Date phdDate;
    private final QualitySequenceFastaDataStore fullLengthFastXDataStore;
    /**
     * Create a new AceAdapted Contig File DataStore using the given phdDate.
     * @param phdDate the date all faked phd files should be timestamped with.
     */
    public AbstractAceAdaptedContigFileDataStore(QualitySequenceFastaDataStore fullLengthFastXDataStore,Date phdDate) {
        this.phdDate = new Date(phdDate.getTime());
        this.fullLengthFastXDataStore = fullLengthFastXDataStore;
    }

    @Override
    protected void visitBeginContig(String contigId, NucleotideSequence consensus) {
        contigBuilder = new AceContigBuilder(contigId, consensus);
    }

    @Override
    protected void visitEndOfContig() {
        visitAceContig(contigBuilder.build());
        
    }

    @Override
    protected void visitRead(String readId, int offset, Range validRange,
    		NucleotideSequence basecalls, Direction dir) {
        
        PhdInfo info =new PhdInfo(readId, readId+".phd.1", phdDate);
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
