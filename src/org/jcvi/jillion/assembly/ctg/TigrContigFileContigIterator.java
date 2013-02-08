package org.jcvi.jillion.assembly.ctg;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

final class TigrContigFileContigIterator extends AbstractBlockingStreamingIterator<TigrContig>{

	private final File contigFile;
	private final DataStoreFilter filter;
	private final DataStore<Long> fullLengthSequences;
	
	public static StreamingIterator<TigrContig> create(File contigFile, DataStore<Long> fullLengthSequences, DataStoreFilter filter){
		TigrContigFileContigIterator iter = new TigrContigFileContigIterator(contigFile,fullLengthSequences,filter);
		iter.start();
		return iter;
	}
	
	private TigrContigFileContigIterator(File contigFile, DataStore<Long> fullLengthSequences, DataStoreFilter filter) {
		this.contigFile = contigFile;
		this.filter = filter;
		this.fullLengthSequences = fullLengthSequences;
	}



	@Override
	protected void backgroundThreadRunMethod() throws RuntimeException {
		TigrContigFileVisitor visitor = new TigrContigFileVisitor() {
			
			@Override
			public void halted() {
				//no-op				
			}
			
			@Override
			public void visitEnd() {
				//no-op				
			}
			
			@Override
			public TigrContigVisitor visitContig(TigrContigVisitorCallback callback,
					String contigId) {
				if(filter.accept(contigId)){
					return new AbstractTigrContigBuilderVisitor(contigId, fullLengthSequences) {
						
						@Override
						protected void visitContig(TigrContigBuilder builder) {
							blockingPut(builder.build());
							
						}
					};
				}
				return null;
			}
		};
		
		try {
			TigrContigFileParser.create(contigFile).accept(visitor);
		} catch (IOException e) {
			throw new RuntimeException("error parsing contig file",e);
		}
	}

	
}
