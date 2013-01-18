package org.jcvi.jillion.internal.fasta.qual;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser2;
import org.jcvi.jillion.fasta.FastaFileVisitor2;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.qual.AbstractQualityFastaRecordVisitor;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaRecord;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

public class QualitySequenceFastaDataStoreIteratorImpl extends AbstractBlockingStreamingIterator<QualitySequenceFastaRecord>{
	
	public static StreamingIterator<QualitySequenceFastaRecord> createIteratorFor(File fastaFile, DataStoreFilter filter){
		QualitySequenceFastaDataStoreIteratorImpl iter = new QualitySequenceFastaDataStoreIteratorImpl(fastaFile, filter);
		iter.start();
		return iter;
	}
	
	
	private final File fastaFile;
	private final DataStoreFilter filter;
	public QualitySequenceFastaDataStoreIteratorImpl(File fastaFile, DataStoreFilter filter) {
		if(!fastaFile.exists()){
			throw new IllegalArgumentException("fasta file must exist");
		}
		this.fastaFile = fastaFile;
		this.filter =filter;
	}
	/**
    * {@inheritDoc}
    */
    @Override
    protected void backgroundThreadRunMethod() {	    	
        FastaFileVisitor2 visitor = new FastaFileVisitor2() {
			
			@Override
			public void visitEnd() {
				//no-op
				
			}
			
			@Override
			public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
					String id, String optionalComment) {
				if(!filter.accept(id)){
					return null;
				}
				return new AbstractQualityFastaRecordVisitor(id,optionalComment) {
					
					@Override
					protected void visitRecord(QualitySequenceFastaRecord fastaRecord) {
						blockingPut(fastaRecord);							
					}
				};
			}
		};
        try {
            new FastaFileParser2(fastaFile).accept(visitor);
        } catch (IOException e) {
            throw new RuntimeException("fasta file does not exist",e);
        }
        


    }
    
    
}