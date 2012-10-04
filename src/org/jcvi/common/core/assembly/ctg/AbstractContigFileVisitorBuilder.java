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
package org.jcvi.common.core.assembly.ctg;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.DefaultContig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreAdapter;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaRecord;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;

public abstract class AbstractContigFileVisitorBuilder extends AbstractContigFileVisitor{

	private final DataStore<Long> fullRangeLengthDataStore;
	
    private DefaultContig.Builder currentContigBuilder;
    
    protected abstract void  addContig(Contig<AssembledRead> contig);
    
    public AbstractContigFileVisitorBuilder(){
    	this((DataStore<Long>)null);
    }
    public AbstractContigFileVisitorBuilder(DataStore<Long> fullRangeLengthDataStore){
    	this.fullRangeLengthDataStore = fullRangeLengthDataStore;
    }
    
    @SuppressWarnings("unchecked")
	public AbstractContigFileVisitorBuilder(NucleotideSequenceDataStore fullLengthSequenceDataStore){
    	this.fullRangeLengthDataStore = (DataStore<Long>)DataStoreAdapter.adapt(DataStore.class, fullLengthSequenceDataStore, 
    			new DataStoreAdapter.AdapterCallback<NucleotideSequence, Long>() {

					@Override
					public Long get(NucleotideSequence from) {
						return from.getUngappedLength();
					}
    		
		});
    }
    
    @SuppressWarnings("unchecked")
	public AbstractContigFileVisitorBuilder(QualitySequenceDataStore fullLengthQualityDataStore){
    	this.fullRangeLengthDataStore = (DataStore<Long>)DataStoreAdapter.adapt(DataStore.class, fullLengthQualityDataStore, 
    			new DataStoreAdapter.AdapterCallback<QualitySequence, Long>() {

					@Override
					public Long get(QualitySequence from) {
						return from.getLength();
					}
    		
		});
    }
    @SuppressWarnings("unchecked")
	public AbstractContigFileVisitorBuilder(NucleotideSequenceFastaDataStore fullLengthSequenceDataStore){
    	this.fullRangeLengthDataStore = (DataStore<Long>)DataStoreAdapter.adapt(DataStore.class, fullLengthSequenceDataStore, 
    			new DataStoreAdapter.AdapterCallback<NucleotideSequenceFastaRecord, Long>() {

					@Override
					public Long get(NucleotideSequenceFastaRecord from) {
						return from.getSequence().getUngappedLength();
					}
    		
		});
    }
    
    @SuppressWarnings("unchecked")
	public AbstractContigFileVisitorBuilder(QualitySequenceFastaDataStore fullLengthQualityDataStore){
    	this.fullRangeLengthDataStore = (DataStore<Long>)DataStoreAdapter.adapt(DataStore.class, fullLengthQualityDataStore, 
    			new DataStoreAdapter.AdapterCallback<QualitySequenceFastaRecord, Long>() {

					@Override
					public Long get(QualitySequenceFastaRecord from) {
						return from.getSequence().getLength();
					}
    		
		});
    }
    @Override
    protected void visitRead(String readId, int offset, Range validRange,
    		NucleotideSequence basecalls, Direction dir) {
    	final int fullLength;
    	if(fullRangeLengthDataStore ==null){
    		//fake validRangelength
    		fullLength =(int)validRange.getEnd();
    	}else{
    		try {
				Long length =fullRangeLengthDataStore.get(readId);
				if(length==null){
					throw new IllegalStateException("could not find full length value from datastore for read id "+readId);
				}
				fullLength=length.intValue();
			} catch (DataStoreException e) {
				throw new IllegalStateException("error getting full length value from datastore for read id "+ readId,e);
			}
    		
    	}
        currentContigBuilder.addRead(readId, offset, validRange,basecalls.toString(),dir,fullLength); 
    }

    @Override
    protected void visitEndOfContig() {
        addContig(currentContigBuilder.build());
    }

    @Override
    protected void visitBeginContig(String contigId, NucleotideSequence consensus) {
        currentContigBuilder = new DefaultContig.Builder(contigId, consensus);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {        
        super.visitEndOfFile();
        currentContigBuilder=null;
    }
    
    
}
