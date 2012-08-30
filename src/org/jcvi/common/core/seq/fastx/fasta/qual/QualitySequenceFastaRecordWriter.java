package org.jcvi.common.core.seq.fastx.fasta.qual;

import org.jcvi.common.core.seq.fastx.fasta.FastaRecordWriter;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;

public interface QualitySequenceFastaRecordWriter extends FastaRecordWriter<PhredQuality, QualitySequence, QualitySequenceFastaRecord>{

}
