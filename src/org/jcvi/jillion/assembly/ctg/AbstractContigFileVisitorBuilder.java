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
package org.jcvi.jillion.assembly.ctg;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.DefaultContig;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaRecord;

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
    	this.fullRangeLengthDataStore = (DataStore<Long>)DataStoreUtil.adapt(DataStore.class, fullLengthSequenceDataStore, 
    			new DataStoreUtil.AdapterCallback<NucleotideSequence, Long>() {

					@Override
					public Long get(NucleotideSequence from) {
						return from.getUngappedLength();
					}
    		
		});
    }
    
    @SuppressWarnings("unchecked")
	public AbstractContigFileVisitorBuilder(QualitySequenceDataStore fullLengthQualityDataStore){
    	this.fullRangeLengthDataStore = (DataStore<Long>)DataStoreUtil.adapt(DataStore.class, fullLengthQualityDataStore, 
    			new DataStoreUtil.AdapterCallback<QualitySequence, Long>() {

					@Override
					public Long get(QualitySequence from) {
						return from.getLength();
					}
    		
		});
    }
    @SuppressWarnings("unchecked")
	public AbstractContigFileVisitorBuilder(NucleotideSequenceFastaDataStore fullLengthSequenceDataStore){
    	this.fullRangeLengthDataStore = (DataStore<Long>)DataStoreUtil.adapt(DataStore.class, fullLengthSequenceDataStore, 
    			new DataStoreUtil.AdapterCallback<NucleotideSequenceFastaRecord, Long>() {

					@Override
					public Long get(NucleotideSequenceFastaRecord from) {
						return from.getSequence().getUngappedLength();
					}
    		
		});
    }
    
    @SuppressWarnings("unchecked")
	public AbstractContigFileVisitorBuilder(QualitySequenceFastaDataStore fullLengthQualityDataStore){
    	this.fullRangeLengthDataStore = (DataStore<Long>)DataStoreUtil.adapt(DataStore.class, fullLengthQualityDataStore, 
    			new DataStoreUtil.AdapterCallback<QualitySequenceFastaRecord, Long>() {

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
