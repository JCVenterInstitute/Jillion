package org.jcvi.common.core.seq.fastx.fasta.pos;

import org.jcvi.common.core.seq.fastx.fasta.FastaDataStoreBuilder;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;

/**
 * {@code PositionFastaDataStoreBuilder} is a {@link FastaDataStoreBuilder}
 * that builds a {@link NucleotideSequenceFastaDataStore}.
 * @author dkatzel
 *
 */
public interface PositionFastaDataStoreBuilder extends FastaDataStoreBuilder<ShortSymbol, Sequence<ShortSymbol>, PositionFastaRecord<Sequence<ShortSymbol>>, PositionFastaDataStore>{
	/**
	 * Adds the given {@link PositionFastaRecord} to this builder.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	public PositionFastaDataStoreBuilder addFastaRecord(PositionFastaRecord<Sequence<ShortSymbol>> fastaRecord);
}
