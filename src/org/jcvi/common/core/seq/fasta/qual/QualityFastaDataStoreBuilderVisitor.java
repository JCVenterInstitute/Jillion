package org.jcvi.common.core.seq.fasta.qual;

import org.jcvi.common.core.seq.fasta.FastaFileDataStoreBuilderVisitor;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;

public interface QualityFastaDataStoreBuilderVisitor extends FastaFileDataStoreBuilderVisitor<PhredQuality, QualitySequence, QualitySequenceFastaRecord, QualitySequenceFastaDataStore>{

}
