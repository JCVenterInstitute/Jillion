package org.jcvi.common.core.seq.trace.sanger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.jcvi.common.core.seq.fasta.FastaDataStoreBuilder;
import org.jcvi.common.core.seq.fasta.FastaFileParser;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.internal.seq.fasta.AbstractFastaFileDataStoreBuilderVisitor;

public final class DefaultPositionFastaFileDataStore {
	
	private DefaultPositionFastaFileDataStore(){
		//can not instantiate
	}
	public static PositionSequenceFastaDataStore create(File positionFastaFile, DataStoreFilter filter) throws FileNotFoundException{
		PositionFastaFileVisitor builder = new PositionFastaFileVisitor(filter);
		FastaFileParser.parse(positionFastaFile, builder);
		return builder.build();
	}
	public static PositionSequenceFastaDataStore create(InputStream positionFastaInputStream, DataStoreFilter filter) throws FileNotFoundException{
		PositionFastaFileVisitor builder = new PositionFastaFileVisitor(filter);
		FastaFileParser.parse(positionFastaInputStream, builder);
		return builder.build();
	}
	public static PositionSequenceFastaDataStore create(File positionFastaFile) throws FileNotFoundException{
		return create(positionFastaFile, DataStoreFilters.alwaysAccept());
	}
	public static PositionSequenceFastaDataStore create(InputStream positionFastaInputStream) throws FileNotFoundException{
		return create(positionFastaInputStream, DataStoreFilters.alwaysAccept());
	}
	

	private static class PositionFastaFileVisitor extends AbstractFastaFileDataStoreBuilderVisitor<Position, PositionSequence, PositionSequenceFastaRecord, PositionSequenceFastaDataStore>{
		/**
		 * Default capacity for position builder {@value}
		 * should be large enough to handle
		 * most sanger reads, and the builder
		 * will grow to accommodate larger reads.
		 */
		private static final int DEFAULT_INITIAL_CAPACITY = 900;


		public PositionFastaFileVisitor(DataStoreFilter filter) {
			super(new DefaultPositionFastaDataStoreBuilder(), filter);
		}


		@Override
		protected PositionSequenceFastaRecord createFastaRecord(String id,
				String comment, String entireBody) {
			PositionSequenceBuilder builder = new PositionSequenceBuilder(DEFAULT_INITIAL_CAPACITY);
			Scanner scanner = new Scanner(entireBody);
	        while(scanner.hasNextShort()){
	        	builder.append(scanner.nextShort());
	        }
			scanner.close();
			return new PositionSequenceFastaRecord(id, comment, builder.build());
		}
		
	}
	
	private static class DefaultPositionFastaDataStoreBuilder implements FastaDataStoreBuilder<Position,PositionSequence,PositionSequenceFastaRecord,PositionSequenceFastaDataStore>{

		private final Map<String, PositionSequenceFastaRecord> map = new LinkedHashMap<String, PositionSequenceFastaRecord>();
		@Override
		public PositionSequenceFastaDataStore build() {
			return DataStoreUtil.adapt(PositionSequenceFastaDataStore.class,map);
		}

		@Override
		public DefaultPositionFastaDataStoreBuilder addFastaRecord(
				PositionSequenceFastaRecord fastaRecord) {
			if(fastaRecord ==null){
				throw new NullPointerException("fasta record can not be null");
			}
			map.put(fastaRecord.getId(), fastaRecord);
			return this;
		}
	}
}
