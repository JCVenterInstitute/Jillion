package org.jcvi.common.core.seq.fasta.qual;

import org.jcvi.common.core.seq.fasta.FastaRecordWriter;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;

public interface QualitySequenceFastaRecordWriter extends FastaRecordWriter<PhredQuality, QualitySequence, QualitySequenceFastaRecord>{

}
