package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileVisitor;
import org.jcvi.common.core.seq.fastx.fasta.qual.LargeQualityFastaFileDataStore.DataStoreClosedException;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;

class QualitySequenceFastaDataStoreIteratorImpl extends AbstractBlockingCloseableIterator<QualitySequenceFastaRecord>{
		private final QualitySequenceFastaDataStore parentDatastore;
		private final File fastaFile;
		public QualitySequenceFastaDataStoreIteratorImpl(QualitySequenceFastaDataStore parentDatastore, File fastaFile) {
			if(parentDatastore ==null){
				throw new NullPointerException("parent datastore can not be null");
			}
			if(!fastaFile.exists()){
				throw new IllegalArgumentException("fasta file must exist");
			}
			this.parentDatastore = parentDatastore;
			this.fastaFile = fastaFile;
		}
		/**
	    * {@inheritDoc}
	    */
	    @Override
	    protected void backgroundThreadRunMethod() {
	        FastaFileVisitor visitor = new AbstractFastaVisitor(){

				@Override
				protected boolean visitRecord(String id, String comment,
						String entireBody) {
					//if our datastore is closed then 
					//we should throw an exception
					//when we try to keep iterating
					if(parentDatastore.isClosed()){
						throw new DataStoreClosedException("backing datastore has been closed");
					}
					QualitySequenceFastaRecord record = QualitySequenceFastaRecordFactory.create(id, 
							QualityFastaRecordUtil.parseQualitySequence(entireBody), 
							comment);
					blockingPut(record);
					return !parentDatastore.isClosed();
				}
	        	
	        };
	        try {
	            FastaFileParser.parse(fastaFile, visitor);
	        } catch (FileNotFoundException e) {
	            throw new RuntimeException("fasta file does not exist",e);
	        }
	        
	

	    }

	@Override
	protected void hasNextCallback() {
		//if our datastore is closed then 
		//we should throw an exception
		//when we try to keep iterating
		if(parentDatastore.isClosed()){
			IOUtil.closeAndIgnoreErrors(this);
			throw new DataStoreClosedException("backing datastore has been closed");
		}
	}
	    
	    
}