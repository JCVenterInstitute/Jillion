package org.jcvi.jillion.assembly.tasm;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

final class TasmContigFileContigIterator extends AbstractBlockingStreamingIterator<TasmContig>{

	private final File contigFile;
	private final DataStoreFilter filter;
	private final DataStore<Long> fullLengthSequences;
	
	public static StreamingIterator<TasmContig> create(File contigFile, DataStore<Long> fullLengthSequences, DataStoreFilter filter){
		TasmContigFileContigIterator iter = new TasmContigFileContigIterator(contigFile,fullLengthSequences,filter);
		iter.start();
		return iter;
	}
	
	private TasmContigFileContigIterator(File contigFile, DataStore<Long> fullLengthSequences, DataStoreFilter filter) {
		this.contigFile = contigFile;
		this.filter = filter;
		this.fullLengthSequences = fullLengthSequences;
	}



	@Override
	protected void backgroundThreadRunMethod() throws RuntimeException {
		TasmFileVisitor visitor = new TasmFileVisitor() {
			
			@Override
			public void visitIncompleteEnd() {
				//no-op				
			}
			
			@Override
			public void visitEnd() {
				//no-op				
			}
			
			@Override
			public TasmContigVisitor visitContig(TasmContigVisitorCallback callback,
					String contigId) {
				if(filter.accept(contigId)){
					return new AbstractTasmContigVisitor(contigId, fullLengthSequences) {
						
						@Override
						protected void visitRecord(DefaultTasmContig.Builder contig) {
							blockingPut(contig);
							
						}
					};
				}
				return null;
			}
		};
		
		try {
			TasmFileParser.create(contigFile).accept(visitor);
		} catch (IOException e) {
			throw new RuntimeException("error parsing contig file",e);
		}
	}

	
}
