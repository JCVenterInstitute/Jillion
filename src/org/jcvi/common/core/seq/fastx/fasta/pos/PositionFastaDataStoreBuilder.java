package org.jcvi.common.core.seq.fastx.fasta.pos;

import org.jcvi.common.core.seq.fastx.fasta.FastaDataStoreBuilder;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;

/**
 * {@code PositionFastaDataStoreBuilder} is a {@link FastaDataStoreBuilder}
 * that builds a {@link NucleotideSequenceFastaDataStore}.
 * @author dkatzel
 *
 */
public interface PositionFastaDataStoreBuilder extends FastaDataStoreBuilder<ShortSymbol, Sequence<ShortSymbol>, PositionSequenceFastaRecord<Sequence<ShortSymbol>>, PositionFastaDataStore>{
	/**
	 * Adds the given {@link PositionSequenceFastaRecord} to this builder.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	<F extends PositionSequenceFastaRecord<Sequence<ShortSymbol>>> PositionFastaDataStoreBuilder addFastaRecord(F fastaRecord);
}
