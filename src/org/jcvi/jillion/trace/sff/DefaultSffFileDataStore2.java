package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;

public class DefaultSffFileDataStore2 {

	
	public static FlowgramDataStore create(File sffFile) throws IOException{
		return create(sffFile, DataStoreFilters.alwaysAccept());
	}
	public static FlowgramDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
		Visitor visitor = new Visitor(filter);
		SffFileParser2 parser = new SffFileParser2(sffFile);
		parser.accept(visitor);
		
		return visitor.builder.build();
	}
	
	
	
	private static final class Visitor implements SffFileVisitor2{
		private DefaultSffDataStoreBuilder builder;
		
		private final DataStoreFilter filter;
		
		
		public Visitor(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			builder = new DefaultSffDataStoreBuilder((int)header.getNumberOfReads());
			
		}

		@Override
		public SffFileReadVisitor visitRead(SffFileParserCallback callback,
				final SffReadHeader readHeader) {
			if(filter.accept(readHeader.getId())){
				return new SffFileReadVisitor(){

					@Override
					public void visitReadData(SffFileParserCallback callback,
							SffReadData readData) {
						 builder.addFlowgram(SffFlowgram.create(readHeader, readData));
						
					}

					@Override
					public void visitEndOfRead(SffFileParserCallback callback) {
						//no-op
						
					}
					
				};
			}
			return null;
		}

		@Override
		public void endSffFile() {
			//no-op
			
		}
		
	}
}
