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
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler;
import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandlers;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.fasta.AbstractLargeFastaFileDataStore;
import org.jcvi.jillion.internal.fasta.MaxNumberOfRecordsFastaVisitor;
/**
 * {@code LargeNucleotideSequenceFastaFileDataStore} is an implementation
 * of {@link NucleotideSequenceFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fasta file
 * which can take some time.  It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.jillion.core.datastore.DataStore, int)}.
 * @author dkatzel
 */
final class LargeNucleotideSequenceFastaFileDataStore extends AbstractLargeFastaFileDataStore<Nucleotide, NucleotideSequence, NucleotideFastaRecord, NucleotideSequenceDataStore> implements NucleotideFastaFileDataStore{
    
	/**
     * Construct a {@link NucleotideFastaDataStore}
     * for the given Fasta file.
     * @param parser the FastaParser to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	static NucleotideFastaFileDataStore create(FastaParser parser){
		return create(parser, Nucleotide.defaultInvalidCharacterHandler());
	}
	/**
     * Construct a {@link NucleotideFastaDataStore}
     * for the given Fasta file.
     * @param parser the FastaParser to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	static NucleotideFastaFileDataStore create(FastaParser parser,InvalidCharacterHandler invalidCharacterHandler){
		return create(parser, DataStoreFilters.alwaysAccept(), null, OptionalLong.empty(), invalidCharacterHandler);
	}
	 /**
     * Construct a {@link NucleotideFastaDataStore}
     * for the given Fasta file.
     * @param parser the FastaParser to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	static NucleotideFastaFileDataStore create(FastaParser parser, Predicate<String> filter,
			Predicate<NucleotideFastaRecord> recordFilter, OptionalLong maxNumberOfRecords, InvalidCharacterHandler invalidCharacterHandler){
		return new LargeNucleotideSequenceFastaFileDataStore(parser, filter, recordFilter, maxNumberOfRecords, invalidCharacterHandler);
	}
   
	private final File fastaFile;
    private final OptionalLong maxNumberOfRecords;
    private final InvalidCharacterHandler invalidCharacterHandler;
    
    public LargeNucleotideSequenceFastaFileDataStore(FastaParser parser,
            Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter,
            OptionalLong maxNumberOfRecords,
            InvalidCharacterHandler invalidCharacterHandler) {
		super(parser, filter, recordFilter, maxNumberOfRecords);
		File tmpFile = null;
		this.maxNumberOfRecords = maxNumberOfRecords;
		this.invalidCharacterHandler = invalidCharacterHandler;
		if( parser instanceof FastaFileParser){
		    Optional<File> optFile =((FastaFileParser)parser).getFile();
		    
		    if(optFile.isPresent()){
		        tmpFile = optFile.get();
		    }
		}
		
		fastaFile = tmpFile;
	}
    
    
	
	@Override
    protected FastaRecordVisitor createRecordVisitor(String id,
            String comment, Consumer<NucleotideFastaRecord> consumer) {
        return new AbstractNucleotideFastaRecordVisitor(id,comment, invalidCharacterHandler, true) {
            
            @Override
            protected void visitRecord(NucleotideFastaRecord fastaRecord) {
                consumer.accept(fastaRecord);
            }
        };
    }
   
    @Override
    public Optional<File> getFile() {
        return Optional.ofNullable(fastaFile);
    }
    @Override
	protected StreamingIterator<NucleotideFastaRecord> createNewIterator(
			FastaParser parser, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordIterator) throws DataStoreException {
		 try {
			return DataStoreStreamingIterator.create(this,
			    		LargeNucleotideSequenceFastaIterator.createNewIteratorFor(parser,filter,recordIterator, invalidCharacterHandler));
		} catch (IOException e) {
			throw new DataStoreException("error iterating over fasta file", e);
		}
	}
    
    
	@Override
	public <E extends Throwable> void forEach(ThrowingBiConsumer<String, NucleotideFastaRecord, E> consumer)
			throws IOException, E {
		Objects.requireNonNull(consumer);
		FastaVisitor visitor = new LargeNucleotideFastaVisitor(getIdFilter(), getRecordFilter(), invalidCharacterHandler, consumer);
		if(maxNumberOfRecords.isPresent()) {
			visitor = new MaxNumberOfRecordsFastaVisitor(maxNumberOfRecords.getAsLong(), visitor);
		}
		getFastaParser().parse(visitor);
		
	}
	@Override
	public ThrowingStream<NucleotideFastaRecord> records() throws DataStoreException {
		if(getNumberOfRecords()< 10_000) {
				//do it all in memory
			Stream.Builder<NucleotideFastaRecord> stream = Stream.builder();
			try {
				forEach((i,r)-> stream.accept(r));
				return ThrowingStream.asThrowingStream(stream.build());
			} catch (IOException e) {
				throw new DataStoreException(e.getMessage(), e);
			}
	    	
		}
		return super.records();
		
	}
    
    
}
