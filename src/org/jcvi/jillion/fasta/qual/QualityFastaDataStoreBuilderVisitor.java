package org.jcvi.jillion.fasta.qual;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.fasta.FastaFileDataStoreBuilderVisitor;

public interface QualityFastaDataStoreBuilderVisitor extends FastaFileDataStoreBuilderVisitor<PhredQuality, QualitySequence, QualitySequenceFastaRecord, QualitySequenceFastaDataStore>{

}
