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
package org.jcvi.jillion.internal.fasta.aa;


import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.AbstractProteinFastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.ProteinFastaDataStore;
import org.jcvi.jillion.fasta.aa.ProteinFastaFileDataStore;
import org.jcvi.jillion.fasta.aa.ProteinFastaRecord;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.fasta.AbstractLargeFastaFileDataStore;

/**
 * {@code LargeProteinFastaFileDataStore} is an implementation
 * of {@link ProteinFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fasta file
 * which can take some time. It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.jillion.core.datastore.DataStore, int)}.
 * @author dkatzel
 */
public final class LargeProteinFastaFileDataStore extends AbstractLargeFastaFileDataStore<AminoAcid, ProteinSequence, ProteinFastaRecord, ProteinSequenceDataStore> implements ProteinFastaFileDataStore{
	
	
	
    /**
     * Construct a {@link LargeProteinFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static ProteinFastaDataStore create(File fastaFile) throws IOException{
		return create(fastaFile, DataStoreFilters.alwaysAccept(),null);
	}
	/**
     * Construct a {@link LargeProteinFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static ProteinFastaDataStore create(File fastaFile, Predicate<String> filter,  Predicate<ProteinFastaRecord> recordFilter) throws IOException{
		FastaParser parser = FastaFileParser.create(fastaFile);
		return new LargeProteinFastaFileDataStore(parser,filter, recordFilter);
	}
	/**
     * Construct a {@link LargeProteinFastaFileDataStore}
     * for the given Fasta file.
     * @param parser the {@link FastaParser} instance to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static ProteinFastaFileDataStore create(FastaParser parser){
		return create(parser, DataStoreFilters.alwaysAccept(),null);
	}
	/**
     * Construct a {@link LargeProteinFastaFileDataStore}
     * for the given Fasta file.
     * @param parser the {@link FastaParser} instance to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static ProteinFastaFileDataStore create(FastaParser parser, Predicate<String> filter,  Predicate<ProteinFastaRecord> recordFilter){
		return new LargeProteinFastaFileDataStore(parser,filter, recordFilter);
	}
   
    protected LargeProteinFastaFileDataStore(FastaParser parser, Predicate<String> filter, Predicate<ProteinFastaRecord> recordFilter) {
		super(parser, filter, recordFilter);
	}


	@Override
	protected StreamingIterator<ProteinFastaRecord> createNewIterator(
			FastaParser parser, Predicate<String> filter,  Predicate<ProteinFastaRecord> recordFilter) {
		return DataStoreStreamingIterator.create(this,LargeProteinFastaIterator.createNewIteratorFor(parser, filter, recordFilter));
	       
	}
    @Override
    protected FastaRecordVisitor createRecordVisitor(String id, String comment,
            Consumer<ProteinFastaRecord> callback) {
        return new AbstractProteinFastaRecordVisitor(id, comment) {
            
            @Override
            protected void visitRecord(ProteinFastaRecord fastaRecord) {
                callback.accept(fastaRecord);
                
            }
        };
    }
   
   
}

