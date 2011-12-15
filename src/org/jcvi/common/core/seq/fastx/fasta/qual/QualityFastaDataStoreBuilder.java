package org.jcvi.common.core.seq.fastx.fasta.qual;

import org.jcvi.common.core.seq.fastx.fasta.FastaDataStoreBuilder;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;

public interface QualityFastaDataStoreBuilder extends FastaDataStoreBuilder<PhredQuality, QualitySequence, QualityFastaRecord, QualityFastaDataStore>{
	/**
	 * Adds the given {@link QualityFastaRecord} to this builder.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	public QualityFastaDataStoreBuilder addFastaRecord(QualityFastaRecord fastaRecord);
}
