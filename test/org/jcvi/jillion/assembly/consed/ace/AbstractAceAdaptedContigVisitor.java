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
package org.jcvi.jillion.assembly.consed.ace;

import java.util.Date;

import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigReadVisitor;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigVisitor;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code AbstractAceAdaptedContigVisitor} is a {@link TigrContigVisitor}
 * that will build an {@link AceContig} from the visitXXX calls.
 * @author dkatzel
 *
 */
abstract class AbstractAceAdaptedContigVisitor implements TigrContigVisitor{

	 private AceContigBuilder contigBuilder;
	 private final Date phdDate;
	 private final QualitySequenceDataStore fullLengthQualityDataStore;
	 private final String contigId;
	 
	 
	 
    /**
     * Create a new AceAdapted Contig File DataStore using the given phdDate.
     * @param phdDate the date all faked phd files should be timestamped with.
     */
    public AbstractAceAdaptedContigVisitor(String contigId, QualitySequenceDataStore fullLengthFastXDataStore,Date phdDate) {
        this.phdDate = new Date(phdDate.getTime());
        this.fullLengthQualityDataStore = fullLengthFastXDataStore;
        this.contigId = contigId;
    }


	@Override
	public void visitConsensus(NucleotideSequence consensus) {
		contigBuilder = new AceContigBuilder(contigId, consensus);
		
	}

	@Override
	public TigrContigReadVisitor visitRead(final String readId,
			final long gappedStartOffset, final Direction dir,final Range validRange) {
		
		return new TigrContigReadVisitor(){

			@Override
			public void visitBasecalls(NucleotideSequence gappedBasecalls) {
				 PhdInfo phdInfo =new PhdInfo(readId, readId+".phd.1", phdDate);
				 int ungappedFullLength;
				try {
					ungappedFullLength = (int)fullLengthQualityDataStore.get(readId).getLength();
				} catch (DataStoreException e) {
					 throw new IllegalStateException("error getting full length trace for "+ readId);
				}
				contigBuilder.addRead(readId, gappedBasecalls, (int) gappedStartOffset, dir,
						validRange, phdInfo, ungappedFullLength);
				
			}

			@Override
			public void visitEnd() {
				//no-op				
			}
			
		};
	}

	@Override
	public void halted() {
		//no-op		
	}

	@Override
	public void visitEnd() {
		visitContig(contigBuilder);
		
	}

	protected abstract void visitContig(AceContigBuilder contigBuilder);
}
