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
package org.jcvi.jillion.assembly.tigr.contig;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
/**
 * {@code DefaultTigrContigFileDataStore} is
 * a {@link TigrContigDataStore} implementation
 * that stores all {@link TigrContig}s in memory.
 * @author dkatzel
 *
 */
final class DefaultTigrContigFileDataStore {

	public static TigrContigDataStore create(File contigFile, DataStore<Long> fullLengthSequenceDataStore, DataStoreFilter filter) throws IOException{
		DataStoreBuilder visitor = new DataStoreBuilder(fullLengthSequenceDataStore, filter);
		TigrContigFileParser.create(contigFile).parse(visitor);
		return DataStoreUtil.adapt(TigrContigDataStore.class, visitor.contigs);
	}
	
	private DefaultTigrContigFileDataStore(){
		//can not instantiate
	}
	
	private static final class DataStoreBuilder implements TigrContigFileVisitor{

		private final Map<String, TigrContig> contigs = new LinkedHashMap<String, TigrContig>();
		private final DataStoreFilter filter;
		private final DataStore<Long> fullLengthSequenceDataStore;
		
		public DataStoreBuilder(DataStore<Long> fullLengthSequenceDataStore,DataStoreFilter filter) {
			this.filter = filter;
			this.fullLengthSequenceDataStore = fullLengthSequenceDataStore;
		}

		@Override
		public TigrContigVisitor visitContig(TigrContigVisitorCallback callback,final String contigId) {
			if(!filter.accept(contigId)){
				return null;
			}
			return new AbstractTigrContigBuilderVisitor(contigId,fullLengthSequenceDataStore) {
				
				@Override
				protected void visitContig(TigrContigBuilder builder) {
					contigs.put(contigId, builder.build());
					
				}
			};
		}

		@Override
		public void halted() {
			//no-op
			
		}

		@Override
		public void visitEnd() {
			//no-op
			
		}
		
	}
}
