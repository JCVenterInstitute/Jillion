package org.jcvi.jillion.sam;

import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
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
}
