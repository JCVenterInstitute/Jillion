package org.jcvi.common.core.seq.fasta.qual;

import org.jcvi.common.core.seq.fasta.FastaRecordWriter;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;

public interface QualitySequenceFastaRecordWriter extends FastaRecordWriter<PhredQuality, QualitySequence, QualitySequenceFastaRecord>{

}
