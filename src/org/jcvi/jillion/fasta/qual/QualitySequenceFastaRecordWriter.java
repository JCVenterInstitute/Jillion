package org.jcvi.jillion.fasta.qual;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.fasta.FastaRecordWriter;

public interface QualitySequenceFastaRecordWriter extends FastaRecordWriter<PhredQuality, QualitySequence, QualitySequenceFastaRecord>{

}
