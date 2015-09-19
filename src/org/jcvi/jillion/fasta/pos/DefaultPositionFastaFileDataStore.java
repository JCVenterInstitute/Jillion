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
package org.jcvi.jillion.fasta.pos;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;

final class DefaultPositionFastaFileDataStore {
	
	private DefaultPositionFastaFileDataStore(){
		//can not instantiate
	}
	public static PositionFastaDataStore create(FastaParser parser, Predicate<String> filter, Predicate<PositionFastaRecord> recordFilter) throws IOException{
		DefaultPositionFastaFileDataStoreBuilder builder = new DefaultPositionFastaFileDataStoreBuilder(filter, recordFilter);
		parser.parse(builder);
    	return builder.build();
	}
	

	
	private static class DefaultPositionFastaFileDataStoreBuilder implements FastaVisitor, Builder<PositionFastaDataStore>{

		private final Map<String, PositionFastaRecord> fastaRecords = new LinkedHashMap<String, PositionFastaRecord>();
		
		private final Predicate<String> filter;
		private final Predicate<PositionFastaRecord> recordFilter;
		
		public DefaultPositionFastaFileDataStoreBuilder(Predicate<String> filter, Predicate<PositionFastaRecord> recordFilter){
			this.filter = filter;
			this.recordFilter = recordFilter;
		}
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				final String id, String optionalComment) {
			if(!filter.test(id)){
				return null;
			}
			return new AbstractPositionSequenceFastaRecordVisitor(id,optionalComment){

				@Override
				protected void visitRecord(
						PositionFastaRecord fastaRecord) {
					if(recordFilter ==null || recordFilter.test(fastaRecord)){
						fastaRecords.put(id, fastaRecord);
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
		public PositionFastaDataStore build() {
			return DataStoreUtil.adapt(PositionFastaDataStore.class,fastaRecords);
		}
		
	}
}
