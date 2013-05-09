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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.util.Date;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public abstract class  AbstractTasmContigBuilderVisitor implements TasmContigVisitor{

	private TasmContigBuilder builder;
	private final String contigId;
	private final DataStore<Long> fullRangeLengthDataStore;
	
	public AbstractTasmContigBuilderVisitor(String contigId, DataStore<Long> fullRangeLengthDataStore){
		this.contigId = contigId;
		this.fullRangeLengthDataStore = fullRangeLengthDataStore;
	}
	
	@Override
	public void visitConsensus(NucleotideSequence consensus) {
		builder = new TasmContigBuilder(contigId, consensus);
	}

	@Override
	public void visitCeleraId(long id) {
		builder.withCeleraAssemblerContigId(Long.valueOf(id));		
	}

	@Override
	public void visitComments(Integer bacId, String comment, String commonName,
			String assemblyMethod, boolean isCircular) {
		if(bacId !=null){
			builder.setSampleId(bacId.toString());
		}
		if(commonName !=null){
			builder.withCommonName(commonName);
		}
		builder.withMethod(assemblyMethod);
		builder.isCircular(isCircular);
	}

	@Override
	public void visitCoverageData(int numberOfReads, float avgCoverage) {
		//no-op these are computed by the builder automatically
		//incase we add/remove reads
	}

	@Override
	public void visitLastEdited(String username, Date editDate) {
		builder.withEditInfo(username, editDate);
	}

	@Override
	public TasmContigReadVisitor visitRead(final String readId,
			final long gappedStartOffset, final Direction dir, final Range validRange) {
		return new TasmContigReadVisitor() {
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
				String gappedSequence = gappedBasecalls.toString();
				builder.addRead(readId, (int)gappedStartOffset, validRange, 
						gappedSequence, dir, fullLength.intValue());
				
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

	@Override
	public void visitEnd() {
		visitRecord(builder);
		builder=null;
		
	}
	
	

	/**
	 * Visit a populated {@link TasmContigBuilder} instance
	 * that has been created using the data from a single 
	 * contig record in a tasm file.
	 * @param builder a {@link TasmContigBuilder} instance;
	 * never null.
	 */
	protected abstract void visitRecord(TasmContigBuilder builder);

}
