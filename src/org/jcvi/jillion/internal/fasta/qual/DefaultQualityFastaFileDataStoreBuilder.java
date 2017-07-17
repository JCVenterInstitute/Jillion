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
package org.jcvi.jillion.internal.fasta.qual;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.qual.AbstractQualityFastaRecordVisitor;
import org.jcvi.jillion.fasta.qual.QualityFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualityFastaRecord;
import org.jcvi.jillion.internal.fasta.AdaptedFastaDataStore;

public class DefaultQualityFastaFileDataStoreBuilder implements FastaVisitor, Builder<QualityFastaDataStore>{

	private final Map<String, QualityFastaRecord> fastaRecords = new LinkedHashMap<String, QualityFastaRecord>();
	
	private final Predicate<String> filter;
	private final Predicate<QualityFastaRecord> recordFilter;
	
	public DefaultQualityFastaFileDataStoreBuilder(Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter){
		this.filter = filter;
		this.recordFilter = recordFilter;
	}
	@Override
	public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
			final String id, String optionalComment) {
		if(!filter.test(id)){
			return null;
		}
		return new AbstractQualityFastaRecordVisitor(id,optionalComment){

			@Override
			protected void visitRecord(
					QualityFastaRecord fastaRecord) {
			    if(recordFilter==null || recordFilter.test(fastaRecord)){
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
	public QualityFastaDataStore build() {
		return new AdaptedQualityDataStore(fastaRecords);
	}
	
	
	private static final class AdaptedQualityDataStore extends AdaptedFastaDataStore<PhredQuality , QualitySequence, QualityFastaRecord, QualitySequenceDataStore> implements QualityFastaDataStore{

	    
        public AdaptedQualityDataStore(Map<String, QualityFastaRecord> map) {
            super(map);
        }

        @Override
        public QualitySequenceDataStore asSequenceDataStore(){
            return DataStore.adapt(QualitySequenceDataStore.class, this, QualityFastaRecord::getSequence);
        }
	    
	}
}
