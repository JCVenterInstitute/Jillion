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
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.shared.fasta.AbstractFastaFileDataStoreBuilder;
/**
 * {@code NucleotideFastaFileDataStoreBuilder}
 * is a factory class that can create new instances
 * of {@link NucleotideFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class NucleotideFastaFileDataStoreBuilder extends AbstractFastaFileDataStoreBuilder<Nucleotide, NucleotideSequence, NucleotideFastaRecord, NucleotideSequenceDataStore, NucleotideFastaFileDataStore>{
	/**
	 * File for fai encoded file
	 * which may be null or point to non-existent file.
	 * Will only be used by {@link #build()} if the file exists.
	 */
	private File faiFile;
	/**
	 * The input Fasta File object only used if fai file
	 * is used.
	 */
	private File fastaFile;
	/**
	 * Handler for what to do when we get an invalid character
	 * @since 6.0
	 */
	private NucleotideSequenceBuilder.DecodingOptions decodingOptions = NucleotideSequenceBuilder.DecodingOptions.DEFAULT;
	
	/**
	 * Create a new Builder instance of 
	 * which will build a {@link NucleotideFastaDataStore} for the given
	 * fasta file.
	 * @param fastaFile the fasta file make a {@link NucleotideFastaDataStore} with;
	 * can not be null and must exist.
	 * 
	 * @apiNote As of Jillion 5.1, this will also look for a FastaIndex ({@code .fai})
	 * file using the standard naming conventions and is equivalent to
	 * <pre>
	 * new NucleotideFastaFileDataStoreBuilder(fastaFile, new File(fastaFile.getParentFile(), fastaFile.getName() + ".fai"));
	 * </pre>
	 * 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 */
	public NucleotideFastaFileDataStoreBuilder(File fastaFile)
			throws IOException {
		this(fastaFile, new File(fastaFile.getParentFile(), fastaFile.getName() + ".fai"));
	}
	
	/**
	 * Create a new Builder instance of 
	 * which will build a {@link NucleotideFastaDataStore} for the given
	 * fasta file that uses the provided Fasta Index File ({@code .fai} file).
	 * 
	 * @param fastaFile the fasta file make a {@link NucleotideFastaDataStore} with;
	 * can not be null and must exist.
	 * 
	 * @param faiFile the Fasta Index File to use, if the file
	 * is null or does not exist, then it won't be used.
	 * 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 * 
	 * @since 5.1
	 */
	public NucleotideFastaFileDataStoreBuilder(File fastaFile, File faiFile)
			throws IOException {
		super(fastaFile);
		this.fastaFile = fastaFile;
		this.faiFile = faiFile;
	}
	/**
         * Create a new Builder instance
         * that will build a {@link NucleotideFastaDataStore} using
         * the {@link FastaParser} object that will be parsing 
         * nucleotide fasta encoded data.
         * 
         * @param parser the {@link FastaParser} to use
         * to visit the fasta encoded data.
         * @throws NullPointerException if the inputStreamSupplier is null.
         */
	public NucleotideFastaFileDataStoreBuilder(FastaParser parser) {
		super(parser);
	}
	
	/**
         * Create a new Builder instance
         * that will build a {@link NucleotideFastaDataStore} from the
         * nucleotide fasta encoded data from the given {@link InputStreamSupplier}.
         * 
         * @param supplier the {@link InputStreamSupplier} to use
         * to get the inputStreams of fasta encoded data.
         * @throws NullPointerException if the inputStreamSupplier is null.
         * 
         * @since 5.0
         */
	public NucleotideFastaFileDataStoreBuilder(InputStreamSupplier supplier) throws IOException {
            super(supplier);
        }

	/**
	 * Create a new Builder instance of 
	 * which will build a {@link NucleotideFastaDataStore} for the given
	 * fasta file.
	 * @param fastaFileStream the {@link InputStream} of the fasta file to make a {@link NucleotideFastaDataStore} with. 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 */
	public NucleotideFastaFileDataStoreBuilder(InputStream fastaFileStream)
			throws IOException {
		super(fastaFileStream);
	}
	@Override
	protected NucleotideFastaFileDataStore createNewInstance(
			FastaParser parser, DataStoreProviderHint providerHint, Predicate<String> filter,
			Predicate<NucleotideFastaRecord> recordFilter, OptionalLong maxNumberOfRecords)
			throws IOException {
		if(parser.isReadOnceOnly()){
			return DefaultNucleotideFastaFileDataStore.create(parser,filter, recordFilter, decodingOptions);	
		}else{
		    NucleotideFastaFileDataStore delegate;
			switch(providerHint){
				case RANDOM_ACCESS_OPTIMIZE_SPEED: 
							delegate= DefaultNucleotideFastaFileDataStore.create(parser,filter, recordFilter, decodingOptions);
							break;
				case RANDOM_ACCESS_OPTIMIZE_MEMORY: 
							delegate = parser.canCreateMemento()?
										IndexedNucleotideSequenceFastaFileDataStore.create(parser,filter, recordFilter, decodingOptions)
										:
										DefaultNucleotideFastaFileDataStore.create(parser,filter, recordFilter, decodingOptions);
							break;
				case ITERATION_ONLY: delegate= LargeNucleotideSequenceFastaFileDataStore.create(parser,filter, recordFilter, maxNumberOfRecords, decodingOptions);
								break;
				default:
					throw new IllegalArgumentException("unknown provider hint : "+ providerHint);
			}
			
			if(faiFile !=null && faiFile.exists()){
				return FaiNucleotideFastaFileDataStore.create(fastaFile, faiFile, delegate);
			}
			return delegate;
			
		}
	}
	

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideFastaFileDataStoreBuilder filter( Predicate<String> filter) {
		super.filter(filter);
		return this;
	}
	/**
	 * Set the {@link org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler} to use
	 * when parsing sequences for this Datastore.  If set to {@code null}
	 * then the default handler is used.
	 * @param invalidCharacterHandler the handler to use; if set to {@code null}
	 * then the default handler is used.
	 * 
	 * @return this
	 * 
	 * @since 6.0
	 */
	public NucleotideFastaFileDataStoreBuilder invalidCharacterHandler(Nucleotide.InvalidCharacterHandler invalidCharacterHandler) {
		return decoderOptions(this.decodingOptions.toBuilder().invalidCharacterHandler(invalidCharacterHandler).build());
	}
	/**
	 * Set the {@link org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder.DecodingOptions} to use
	 * when parsing sequences for this Datastore.  If set to {@code null}
	 * then the default decoder is used.
	 * @param decodingOptions the options to use; if set to {@code null}
	 * then the default is used.
	 * 
	 * @return this
	 * 
	 * @since 6.0
	 */
	public NucleotideFastaFileDataStoreBuilder decoderOptions(NucleotideSequenceBuilder.DecodingOptions decodingOptions) {
		this.decodingOptions = decodingOptions==null? NucleotideSequenceBuilder.DecodingOptions.DEFAULT: decodingOptions;
		return this;
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideFastaFileDataStoreBuilder hint(DataStoreProviderHint hint) {
		super.hint(hint);
		return this;
	}

	@Override
    public NucleotideFastaFileDataStoreBuilder filterRecords(Predicate<NucleotideFastaRecord> filter) {
        super.filterRecords(filter);
        return this;
    }

    /**
	 * Builds a new {@link NucleotideFastaDataStore} for the given
	 * fasta input and using the provided filtering criteria.
	 * 
	 * <p>
	 * As of Jillion 5.1 if a Fasta Index file is also specified
	 * or is found using the standard naming conventions, then that file
	 * will also be used if it exists.
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideFastaFileDataStore build() throws IOException {
		return super.build();
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideFastaFileDataStoreBuilder onlyIncludeIds(
			Set<String> ids) {
		super.onlyIncludeIds(ids);
		return this;
	}
	
	
}
