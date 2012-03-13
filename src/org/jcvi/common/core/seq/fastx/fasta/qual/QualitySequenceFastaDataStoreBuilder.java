package org.jcvi.common.core.seq.fastx.fasta.qual;

import org.jcvi.common.core.seq.fastx.fasta.FastaDataStoreBuilder;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
/**
 * {@code QualityFastaDataStoreBuilder} is a {@link FastaDataStoreBuilder}
 * that builds a {@link QualitySequenceFastaDataStore}.
 * @author dkatzel
 *
 */
public interface QualitySequenceFastaDataStoreBuilder extends FastaDataStoreBuilder<PhredQuality, QualitySequence, QualitySequenceFastaRecord, QualitySequenceFastaDataStore>{
	/**
	 * Adds the given {@link QualitySequenceFastaRecord} to this builder.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	QualitySequenceFastaDataStoreBuilder addFastaRecord(QualitySequenceFastaRecord fastaRecord);
}
