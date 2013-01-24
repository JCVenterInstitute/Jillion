package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
/**
* {@code DefaultSffFileDataStore} creates {@link FlowgramDataStore}
* instances that store all the {@link Flowgram}s
* in a Map.  This implementation is not very 
* memory efficient and therefore should not be used
* for large sff files.
* @author dkatzel
*/
class DefaultSffFileDataStore {

	private DefaultSffFileDataStore(){
		//can not instantiate
	}
	/**
	 * Create a new {@link FlowgramDataStore} by parsing
	 * the entire given sff file and include all
	 * the reads in the DataStore.
	 * @param sffFile the sff encoded file to parse.
	 * @return a new {@link FlowgramDataStore} containing
	 * all the reads in the sff file; never null.
	 * @throws IOException if there is a problem
	 * parsing the file.
	 * @throws NullPointerException if either the sffFile is null.
	 */
	public static FlowgramDataStore create(File sffFile) throws IOException{
		return create(sffFile, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Create a new {@link FlowgramDataStore} by parsing
	 * the entire given sff file but include only
	 * the reads that are accepted by the given {@link DataStoreFilter}.
	 * @param sffFile the sff encoded file to parse.
	 * @param filter the {@link DataStoreFilter} to use
	 * to filter out any reads in the sff file; can not be null.
	 * @return a new {@link FlowgramDataStore} containing
	 * only the reads accepted by the given filter; never null.
	 * @throws IOException if there is a problem
	 * parsing the file.
	 * @throws NullPointerException if either the sffFile or filter are null.
	 */
	public static FlowgramDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
		Visitor visitor = new Visitor(filter);
		SffFileParser parser = new SffFileParser(sffFile);
		parser.accept(visitor);
		
		return visitor.builder.build();
	}
	
	
	/**
	 * {@link SffFileVisitor} implementation 
	 * that puts flowgrams into a datastore
	 * as each record is visited.
	 * @author dkatzel
	 *
	 */
	private static final class Visitor implements SffFileVisitor{
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
					public void visitReadData(SffReadData readData) {
						 builder.addFlowgram(SffFlowgram.create(readHeader, readData));
						
					}

					@Override
					public void visitEnd() {
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
