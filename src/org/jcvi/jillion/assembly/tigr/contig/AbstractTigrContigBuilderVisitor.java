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
package org.jcvi.jillion.assembly.tigr.contig;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code AbstractTigrContigBuilderVisitor} is a {@link TigrContigVisitor}
 * that will create a {@link TigrContigBuilder} and populate it using the
 * visit calls. 
 * @author dkatzel
 *
 */
public abstract class AbstractTigrContigBuilderVisitor implements TigrContigVisitor{

	private final DataStore<Long> fullRangeLengthDataStore;
	
    private TigrContigBuilder currentContigBuilder;
    
    private final String contigId;
    
	public AbstractTigrContigBuilderVisitor(String contigId, DataStore<Long> fullLengthSequenceDataStore){
    	this.fullRangeLengthDataStore = fullLengthSequenceDataStore;
    	this.contigId = contigId;
    }

  

	@Override
	public void visitConsensus(NucleotideSequence consensus) {
		currentContigBuilder = new TigrContigBuilder(contigId, consensus);
		
	}

	@Override
	public TigrContigReadVisitor visitRead(final String readId,
			final long gappedStartOffset, final Direction dir, final Range validRange) {
		return new TigrContigReadVisitor() {
			private NucleotideSequence gappedBasecalls;
			
			
			@Override
			public void visitEnd() {
				final Long fullLength;
				try {
					fullLength = fullRangeLengthDataStore.get(readId);
				} catch (DataStoreException e) {
					throw new IllegalStateException("error reading from full length sequence datastore for read "+ readId, e);
				}
				if(fullLength ==null){
					throw new IllegalStateException("full length sequence datastore did not contain read "+ readId);
				}
				currentContigBuilder.addRead(readId, (int)gappedStartOffset, validRange, 
						gappedBasecalls.toString(), dir, fullLength.intValue());
				
				
			}
			
			@Override
			public void visitBasecalls(NucleotideSequence gappedBasecalls) {
				this.gappedBasecalls = gappedBasecalls;				
			}
		};
	}

	@Override
	public void halted() {
		//no-op
		
	}
	/**
	 * Calls {@link #visitContig(TigrContigBuilder)}
	 * with the populated internal {@link TigrContigBuilder}.
	 * @throws IllegalStateException if {@link #visitConsensus(NucleotideSequence)}
	 * has not yet been called.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	public final void visitEnd() {		
		if(currentContigBuilder==null){
			throw new IllegalStateException("contig does not contain any consensus or read data");
		}
		visitContig(currentContigBuilder);
		currentContigBuilder=null;
	}
    /**
     * The entire contig has been visited.  This method
     * will only be called from inside {@link #visitEnd()}.
     * Subclasses may modify the builder as they see fit.
     * @param builder a completely populated {@link TigrContigBuilder}
     * instance containing all the contig data gathered from
     * the visit methods; will never be null.
     */
	protected abstract void visitContig(TigrContigBuilder builder);
    
}
