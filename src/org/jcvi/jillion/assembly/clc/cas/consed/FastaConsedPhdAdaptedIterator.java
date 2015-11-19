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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdBuilder;
import org.jcvi.jillion.assembly.consed.phd.PhdUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;
/**
 * {@code FastaConsedPhdAdaptedIterator} is a PhdReadRecord generator
 * for chromatograms.  Since CLC's reference mappers don't handle chromatograms,
 * they have to be passed in as fasta files.  This class will find the chromatogram in the 
 * chromat_dir folder with the same name as the current read id in the fasta and create a PhdReadRecord
 * with the correct sequence, qualities and postions from the chromatogram as well
 * as a correctly formatted {@link PhdInfo} so consed can correctly display the chromatogram
 * wave forms.
 * @author dkatzel
 *
 */
class FastaConsedPhdAdaptedIterator implements StreamingIterator<PhdReadRecord>{
	
	private final StreamingIterator<NucleotideFastaRecord> fastaIterator;
	private final Map<String,String> requiredComments;
	private final byte defaultQualityValue;
	private final Date phdDate;
	private final File fastaFile;
	
	public FastaConsedPhdAdaptedIterator(
			StreamingIterator<NucleotideFastaRecord> fastaIterator,
			File fastaFile,
			Date phdDate,
			PhredQuality defaultQualityValue){
		this.requiredComments = PhdUtil.createPhdTimeStampCommentFor(phdDate);
		this.fastaIterator = fastaIterator;	
		this.defaultQualityValue = defaultQualityValue.getQualityScore();
		this.fastaFile = fastaFile;
		this.phdDate = new Date(phdDate.getTime());
	}
	@Override
	public boolean hasNext() {
		return fastaIterator.hasNext();
	}

	@Override
	public PhdReadRecord next() {
		NucleotideFastaRecord nextFasta = fastaIterator.next();
		String id = nextFasta.getId();
		//Properties constructor "new Properties(Properties)"
		//doesn't actually put those values in the map,
		//they are only used for "defaults" 
		//so we have to manually add them using put methods.
		Properties comments = new Properties();
		comments.putAll(requiredComments);
		requiredComments.putAll( createAdditionalCommentsFor(id));

		Phd phd =createPhdRecordFor(nextFasta, requiredComments);
		
		PhdInfo info = ConsedUtil.generateDefaultPhdInfoFor(fastaFile, id, phdDate);
		return new PhdReadRecord(phd, info);
	}
	
	/**
     * Add any additional comments if needed.
     * By default this method does not add
     * any more comments.  Subclasses
     * may override this method to add new values.
     * @param id the id of this sequence
     * @return a {@link Properties} object which contains
     * any new comments to be included for the current read with the given id;
     * can not be null but may be empty.
     */
    protected  Map<String,String> createAdditionalCommentsFor(String id) {
        return Collections.emptyMap();
    }
    protected Phd createPhdRecordFor(NucleotideFastaRecord nextFasta,  Map<String,String> requiredComments ){
	    String id = nextFasta.getId();
        QualitySequence qualities = getQualitiesFor(nextFasta);
        return new PhdBuilder(id, nextFasta.getSequence(), qualities)
        				.comments(requiredComments)
        				.fakePeaks()
        				.build();
	}
	
    protected QualitySequence getQualitiesFor(
    		NucleotideFastaRecord nextFasta) {
        int numberOfQualities =(int) nextFasta.getSequence().getLength();
		byte[] qualities = new byte[numberOfQualities];
		Arrays.fill(qualities, defaultQualityValue);
        return new QualitySequenceBuilder(qualities).build();
    }

	@Override
	public void remove() {
		fastaIterator.remove();
		
	}
	@Override
	public void close() {
		fastaIterator.close();
		
	}

}
