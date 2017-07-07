/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.util.streams.ThrowingConsumer;
import org.jcvi.jillion.sam.header.SamHeader;
/**
 * DataStore wrapper around a single SAM or BAM file.
 * 
 * @author dkatzel
 *
 * @since 5.2
 */
public interface SamFileDataStore extends DataStore<SamRecord>{
    
    /**
     * Create a new SamFileDataStore instance that will parse the given
     * sam or bam encoded file.  
     * If there is an accompanying BAI file in the same directory named
     * {@code samFile.getName() + ".bai"}, then the index will be automatically
     * detected and used by the Datastore to improve parsing runtime.
     * All records in the file will be included in this datastore.
     * If any filtering or non-standard indexes are required, please use
     * {@link SamFileDataStoreBuilder}.
     * 
     * @apiNote this is the same as {@code new SamFileDataStoreBuilder(samOrBamFile).build();}
     * 
     * @param samFile the sam or bam file to use; can not be null.
     * 
     * @throws IOException if the file does not exist or is not readable.
     * @throws NullPointerException if samFile is null.
     * 
     * @see SamFileDataStoreBuilder
     * @since 5.3
     */
    public static SamFileDataStore fromFile(File samOrBamFile) throws IOException{
        return new SamFileDataStoreBuilder(samOrBamFile).build();
                
    }
    /**
     * Get the <strong>first</strong> record with this query id that is
     * in this datastore.
     * This method should probably not be used since SAM and BAM
     * files often have multiple {@link SamRecord}s with the same
     * queryName if there are multiple alignments or paired end data
     * then the forward and reverse reads often have the same queryname.
     * 
     * 
     * @see #getAllRecordsFor(String)
     */
    @Override
    SamRecord get(String queryName) throws DataStoreException;
    /**
     * Get all the {@link SamRecord}s in this datastore that have the
     * given queryName.
     * 
     * @param queryName the {@link SamRecord#getQueryName()} to look for;
     * can not be null.
     * 
     * @return a {@link List} of all the {@link SamRecord}s with that queryname;
     * will never be null, but may be empty if there are no records in the datastore
     * that match.
     * 
     * @throws DataStoreException if there is a problem parsing the file
     * or if the datastore is closed.
     */
    List<SamRecord> getAllRecordsFor(String queryName) throws DataStoreException;
    /**
     * Get the {@link SamHeader} of this SAM or BAM file.
     * 
     * @return the {@link SamHeader} will never be null.
     * 
     * @throws DataStoreException if there is a problem parsing the file
     * or if the datastore is closed.
     */
    SamHeader getHeader() throws DataStoreException;
    /**
     * Get all the {@link SamRecord}s that aligned to the
     * given reference.
     * 
     * @param referenceName the name of the reference to look for.
     * This name should be in the {@link SamHeader#getReferenceSequences()}.
     * 
     * @return a new {@link StreamingIterator} of all the Records that align to the given reference.
     * 
     * @throws DataStoreException if there is a problem parsing the file
     * or if the datastore is closed.
     */
    StreamingIterator<SamRecord> getAlignedRecords(String referenceName) throws DataStoreException;
    
    /**
     * Get all the {@link SamRecord}s that aligned to the
     * given reference and for each one, call the given consumer's call back method.
     * 
     * @param referenceName the name of the reference to look for.
     * This name should be in the {@link SamHeader#getReferenceSequences()}; can not be null.
     * @param consuemr a {@link ThrowingConsumer} instance that will be called for each aligned record. Can not be null.
     * 
     * @implNote this will probably be faster than using {@link #getAlignedRecords(String)}
     * since most iterator implementations use multiple blocking threads.
     * 
     * @throws DataStoreException if there is a problem parsing the file
     * or if the datastore is closed.
     * 
     * @throws NullPointerException if either parameter is null.
     * @since 5.3
     */
    <E extends Throwable> void forEachAlignedRecord(String referenceName, ThrowingConsumer<SamRecord, E> consumer) throws DataStoreException, E;
    /**
     * Get all the {@link SamRecord}s that aligned to the
     * given reference and for each one, call the given consumer's call back method.
     * 
     * @param referenceName the name of the reference to look for.
     * This name should be in the {@link SamHeader#getReferenceSequences()}; can not be null.
     * @param alignmentRange the {@link Range} along this reference to find alignments for.; can not be null.
     * 
     * @param consuemr a {@link ThrowingConsumer} instance that will be called for each aligned record. Can not be null.
     * 
     * @implNote this will probably be faster than using {@link #getAlignedRecords(String, Range)}
     * since most iterator implementations use multiple blocking threads.
     * 
     * @throws DataStoreException if there is a problem parsing the file
     * or if the datastore is closed.
     * 
     * @throws NullPointerException if either parameter is null.
     * @since 5.3
     */
    <E extends Throwable> void forEachAlignedRecord(String referenceName, Range alignmentRange, ThrowingConsumer<SamRecord, E> consumer) throws DataStoreException, E;
    /**
     * Get all the {@link SamRecord}s that aligned to the
     * given reference within the alignment Ranges given. 
     * Only {@link SamRecord}s that align to the reference AND the alignment INTERSECTS with at least one of
     * these ranges will passed to the callback Consumer. Please note
     * to match the output of {@code samtools view} if a record intersects multiple ranges
     * given, then it will be returned multiple times.
     * 
     * @param referenceName the name of the reference to look for.
     * This name should be in the {@link SamHeader#getReferenceSequences()}; can not be null.
     * 
     * @param alignmentRanges the {@link Range}s along this reference to find alignments for. 
     * Please Note: to keep compatibility with samtools, the same record could be returned
     * multiple times if it intersects multiple ranges given; can not be null.
     * 
     * @param consuemr a {@link ThrowingConsumer} instance that will be called for each aligned record. Can not be null.
     * 
     * @implNote this will probably be faster than using {@link #getAlignedRecords(String, Range)}
     * since most iterator implementations use multiple blocking threads.
     * 
     * 
     * @throws DataStoreException if there is a problem parsing the file
     * or if the datastore is closed.
     * 
     * @throws NullPointerException if any parameter is null.
     * 
     * @since 5.3
     */
    default <E extends Throwable> void forEachAlignedRecord(String referenceName, Collection<Range> alignmentRanges, ThrowingConsumer<SamRecord, E> consumer) throws DataStoreException, E{
        for(Range r : alignmentRanges){
            forEachAlignedRecord(referenceName, r, consumer);
        }
    }
    
    /**
     * Get all the {@link SamRecord}s that aligned to the
     * given reference within the alignment Range given. 
     * Only {@link SamRecord}s that align to the reference AND the alignment INTERSECTS with this range
     * will be included in the returned Stream. 
     * 
     * @param referenceName the name of the reference to look for.
     * This name should be in the {@link SamHeader#getReferenceSequences()}.
     * 
     * @param alignmentRange the {@link Range} along this reference to find alignments for.
      The Range can not be null.
     * 
     * @return a new {@link StreamingIterator} of all the Records that align to the given reference
     * inside the alignment range.
     * 
     * @throws DataStoreException if there is a problem parsing the file
     * or if the datastore is closed.
     */
    StreamingIterator<SamRecord> getAlignedRecords(String referenceName, Range alignmentRange) throws DataStoreException;
    
    /**
     * Get all the {@link SamRecord}s that aligned to the
     * given reference within the alignment Ranges given. 
     * Only {@link SamRecord}s that align to the reference AND the alignment INTERSECTS with at least one of
     * these ranges will be included in the returned Stream. Please note
     * to match the output of {@code samtools view} if a record intersects multiple ranges
     * given, then it will be returned multiple times.
     * 
     * @param referenceName the name of the reference to look for.
     * This name should be in the {@link SamHeader#getReferenceSequences()}.
     * 
     * @param alignmentRanges the {@link Range}s along this reference to find alignments for. 
     * Please Note: to keep compatibility with samtools, the same record could be returned
     * multiple times if it intersects multiple ranges given.
     * 
     * @return a new {@link StreamingIterator} of all the Records that align to the given reference
     * inside the alignment range.
     * 
     * @throws DataStoreException if there is a problem parsing the file
     * or if the datastore is closed.
     * 
     * @since 5.3
     */
    default StreamingIterator<SamRecord> getAlignedRecords(String referenceName, Range... alignmentRanges) throws DataStoreException{
        //to keep with samtools just call each range one after another which causes duplicate records to be returned
        List<Supplier<StreamingIterator<SamRecord>>> suppliers = new ArrayList<>(alignmentRanges.length);
        for(Range r : alignmentRanges){
            Objects.requireNonNull(r, "range can not be null");
            suppliers.add( () -> {
                try {
                    return getAlignedRecords(referenceName, r);
                } catch (DataStoreException e) {
                   throw new UncheckedIOException(e);
                }
            });
        }
        return IteratorUtil.chainStreamingSuppliers(suppliers);
    }
}
