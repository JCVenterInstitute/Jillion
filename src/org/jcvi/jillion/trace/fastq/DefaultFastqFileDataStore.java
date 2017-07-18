/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.util.Builder;
/**
 * {@code DefaultFastqFileDataStore} is the default implementation
 * of {@link FastqDataStore} which stores
 * all {@link FastqRecord}s from a file in memory.  This is only recommended for small fastq
 * files that won't take up too much memory.
 * @author dkatzel
 *
 */
final class DefaultFastqFileDataStore{
	
	private DefaultFastqFileDataStore(){
		//can not instantiate
	}


   /**
	 * Create a new {@link FastqDataStore} instance for the {@link FastqRecord}s
	 * that are accepted by the given {@link DataStoreFilter} that are contained in
	 * the given fastq file. Any records that are not accepted by the filter
	 * will not be included in the returned {@link FastqDataStore}. All of those
	 * records must have their qualities encoded a manner that can be parsed by
	 * the given {@link FastqQualityCodec} (if provided).
	 * 
	 * @param parser
	 *            the {@link FastqParser} instance to parse, must exist and can not be null.
	 * @param filter
	 *            an instance of {@link DataStoreFilter} that can be used to filter
	 *            out some {@link FastqRecord}s from the datastore.
	 * @param qualityCodec
	 *            the {@link FastqQualityCodec} needed to parse the encoded
	 *            quality values in each record. If this value is null, then the
	 *            datastore implementation will try to guess the codec used
	 *            which might have a performance penalty associated with it.
	 * @return a new {@link FastqFileDataStore} instance containing only those
	 *         records that pass the filter.
	 * @throws IOException
	 *             if thre is a problem parsing the fastq file.
	 * @throws NullPointerException
	 *             if either fastqFile or filter is null.
	 */
	public static FastqFileDataStore create(FastqParser parser,
			FastqQualityCodec qualityCodec, Predicate<String> filter, Predicate<FastqRecord> recordFilter)
			throws IOException {
		DefaultFastqFileDataStoreBuilderVisitor2 visitor = new DefaultFastqFileDataStoreBuilderVisitor2(
		        qualityCodec,filter, recordFilter, parser.getFile().orElse(null));
		   
		   parser.parse(visitor);

		   return visitor.build();
	}
    
	private static final class DefaultFastqFileDataStoreBuilderVisitor2 implements FastqVisitor, Builder<FastqFileDataStore> {
		private final Predicate<String> filter;
		private final Predicate<FastqRecord> recordFilter;
		private final FastqQualityCodec qualityCodec;
		private final Map<String, FastqRecord> map = new LinkedHashMap<>();

		private final File fastqFile;
		
		public DefaultFastqFileDataStoreBuilderVisitor2(
				FastqQualityCodec qualityCodec, Predicate<String> filter, Predicate<FastqRecord> recordFilter,
				File fastqFile) {
			if(qualityCodec==null){
				throw new NullPointerException("quality codec can not be null");
			}
			if(filter==null){
				throw new NullPointerException("filter can not be null");
			}
			
			this.qualityCodec = qualityCodec;
			this.filter = filter;
			this.recordFilter = recordFilter;
			this.fastqFile = fastqFile;
		}

		@Override
		public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
				String id, String optionalComment) {
			if(!filter.test(id)){
				return null;
			}
			return new AbstractFastqRecordVisitor(id,optionalComment, qualityCodec) {
				
				@Override
				protected void visitRecord(FastqRecord record) {
				    if(recordFilter==null || recordFilter.test(record)){
					map.put(record.getId(), record);	
				    }
				}
			};
		}

		@Override
		public void visitEnd() {
			//no-op
		}
		@Override
		public void halted() {
			//no-op			
		}
		@Override
		public FastqFileDataStore build() {
			return new FastqFileDataStoreImpl(DataStore.of(map, FastqDataStore.class),
			                                    qualityCodec, fastqFile);
		}

	}
}
