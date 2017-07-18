/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.clc.cas.consed;

import java.io.File;
import java.util.Date;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
import org.jcvi.jillion.fasta.qual.QualityFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualityFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.qual.QualityFastaRecord;
/**
 * An {@link Iterator}
 * that will try to find a corresponding qual file
 * and look up the quality scores.
 * @author dkatzel
 *
 */
class QualFastaConsedPhdAdaptedIterator extends FastaConsedPhdAdaptedIterator{

	private final QualityFastaDataStore qualIter;
	
	public QualFastaConsedPhdAdaptedIterator(
			StreamingIterator<NucleotideFastaRecord> fastaIterator,
			File fastaFile, Date phdDate, PhredQuality defaultQualityValue) {
		super(fastaIterator, fastaFile, phdDate, defaultQualityValue);
		File qualFile = new File(fastaFile.getParentFile(), FileUtil.getBaseName(fastaFile)+".qual");
		if(qualFile.exists()){
			try {
				qualIter = new QualityFastaFileDataStoreBuilder(qualFile)
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
			NucleotideFastaRecord nextFasta) {
		QualityFastaRecord qualRecord=null;
		
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
