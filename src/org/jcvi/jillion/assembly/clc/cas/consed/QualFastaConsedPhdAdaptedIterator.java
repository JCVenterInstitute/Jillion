package org.jcvi.jillion.assembly.clc.cas.consed;

import java.io.File;
import java.util.Date;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaRecord;
/**
 * {@code QualFastaConsedPhdAdaptedIterator} is a {@link FastaConsedPhdAdaptedIterator}
 * that will try to find a corresponding qual file
 * and look up the quality scores.
 * @author dkatzel
 *
 */
public class QualFastaConsedPhdAdaptedIterator extends FastaConsedPhdAdaptedIterator{

	private final QualitySequenceFastaDataStore qualIter;
	
	public QualFastaConsedPhdAdaptedIterator(
			StreamingIterator<NucleotideSequenceFastaRecord> fastaIterator,
			File fastaFile, Date phdDate, PhredQuality defaultQualityValue) {
		super(fastaIterator, fastaFile, phdDate, defaultQualityValue);
		File qualFile = new File(fastaFile.getParentFile(), FileUtil.getBaseName(fastaFile)+".qual");
		if(qualFile.exists()){
			try {
				qualIter = new QualitySequenceFastaFileDataStoreBuilder(qualFile)
									.build();
			} catch (Exception e) {
				throw new IllegalStateException("error parsing corresponding qual file : " + qualFile.getAbsolutePath(), e);
			}
		}else{
			qualIter = null;
		}
	}

	@Override
	protected QualitySequence getQualitiesFor(
			NucleotideSequenceFastaRecord nextFasta) {
		QualitySequenceFastaRecord qualRecord=null;
		
		if(qualIter !=null){
			try {
				qualRecord =qualIter.get(nextFasta.getId());
			} catch (DataStoreException e) {
				throw new IllegalStateException("error getting quality fasta record "+ nextFasta.getId(), e);
			}
		}
		
		if(qualRecord ==null){
			return super.getQualitiesFor(nextFasta);
		}
		return qualRecord.getSequence();
	}

	
}
