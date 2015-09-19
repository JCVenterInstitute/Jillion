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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
/**
 * {@code DefaultTasmFileContigDataStore}
 * is a {@link TasmContigDataStore}
 * implementation that stores all {@link TasmContig}s
 * from a tasm file
 * in a {@link Map}.
 * @author dkatzel
 *
 */
final class DefaultTasmFileContigDataStore {

	public static TasmContigDataStore create(File tasmFile, DataStore<Long> fullLengthSequenceDataStore, DataStoreFilter filter) throws IOException{
		Visitor visitor = new Visitor(filter,fullLengthSequenceDataStore);
		TasmFileParser.create(tasmFile).parse(visitor);
		return DataStoreUtil.adapt(TasmContigDataStore.class, visitor.contigs);
	}
	
	private DefaultTasmFileContigDataStore(){
		//can not instantiate.
	}
	
	private static final class Visitor implements TasmVisitor{
		private final DataStoreFilter filter;
		private final DataStore<Long> fullLengthSequenceDataStore;
		private final Map<String, TasmContig> contigs = new LinkedHashMap<String, TasmContig>();
		
		public Visitor(DataStoreFilter filter, DataStore<Long> fullLengthSequenceDataStore) {
			this.filter = filter;
			this.fullLengthSequenceDataStore = fullLengthSequenceDataStore;
		}

		@Override
		public TasmContigVisitor visitContig(
				TasmVisitorCallback callback, final String contigId) {
			if(!filter.accept(contigId)){
				return null;
			}
			return new AbstractTasmContigBuilderVisitor(contigId, fullLengthSequenceDataStore) {
				
				@Override
				protected void visitRecord(TasmContigBuilder builder) {
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
