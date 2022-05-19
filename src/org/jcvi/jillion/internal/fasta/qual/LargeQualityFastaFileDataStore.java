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
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.fasta.qual;

import java.io.File;
import java.io.IOException;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.qual.QualityFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualityFastaRecord;
import org.jcvi.jillion.fasta.qual.QualityFastaRecordBuilder;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.fasta.AbstractLargeFastaFileDataStore;
import org.jcvi.jillion.internal.fasta.AbstractResuseableFastaRecordVisitor;
/**
 * {@code LargeQualityFastaFileDataStore} is an implementation
 * of {@link QualityFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.jillion.core.datastore.DataStore, int)}.
 * @author dkatzel
 *
 *
 */
public final class LargeQualityFastaFileDataStore extends AbstractLargeFastaFileDataStore<PhredQuality, QualitySequence, QualityFastaRecord, QualitySequenceDataStore> implements QualityFastaDataStore{

    
    public static QualityFastaDataStore create(File fastaFile) throws IOException{
    	return create(fastaFile, DataStoreFilters.alwaysAccept(), null);
    }
    public static QualityFastaDataStore create(File fastaFile, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter) throws IOException{
    	FastaParser parser = FastaFileParser.create(fastaFile);
    	return new LargeQualityFastaFileDataStore(parser,filter, recordFilter, OptionalLong.empty());
    }
    
    public static QualityFastaDataStore create(FastaParser parser){
    	return create(parser, DataStoreFilters.alwaysAccept(), null, OptionalLong.empty());
    }
    public static QualityFastaDataStore create(FastaParser parser, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter,  OptionalLong maxNumberofRecords){
    	return new LargeQualityFastaFileDataStore(parser,filter, recordFilter, maxNumberofRecords);
    }
	protected LargeQualityFastaFileDataStore(FastaParser parser, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter, OptionalLong maxNumberofRecords) {
		super(parser, filter, recordFilter, maxNumberofRecords);
	}

	@Override
	protected StreamingIterator<QualityFastaRecord> createNewIterator(
			FastaParser parser, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter) {
		StreamingIterator<QualityFastaRecord> iter = QualitySequenceFastaDataStoreIteratorImpl.createIteratorFor(parser, filter, recordFilter);
        
        return DataStoreStreamingIterator.create(this,iter);
	}
	
    @Override
    protected FastaRecordVisitor createRecordVisitor(String id, String comment,
            Consumer<QualityFastaRecord> callback) {
        return new AbstractResuseableFastaRecordVisitor(){

            @Override
            public void visitRecord(String id, String optionalComment,
                    String fullBody) {
                QualityFastaRecord record = new QualityFastaRecordBuilder(id,fullBody)
                        .comment(optionalComment)                        
                        .build();
                callback.accept(record);
                
            }
            
        };
    }
   
	
}
