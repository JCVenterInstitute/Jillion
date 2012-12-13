/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.assembly.clc.cas.consed;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.jcvi.common.core.assembly.ace.PhdInfo;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.read.trace.sanger.phd.ArtificialPhd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdUtil;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;

public class FastaConsedPhdAdaptedIterator implements PhdReadRecordIterator{

	private final StreamingIterator<NucleotideSequenceFastaRecord> fastaIterator;
	private final Properties requiredComments;
	private final byte defaultQualityValue;
	private final Date phdDate;
	private final File fastaFile;
	public FastaConsedPhdAdaptedIterator(
			StreamingIterator<NucleotideSequenceFastaRecord> fastaIterator,
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
		NucleotideSequenceFastaRecord nextFasta = fastaIterator.next();
		String id = nextFasta.getId();
		Properties comments = createAdditionalCommentsFor(id,requiredComments);
		Phd phd =createPhdRecordFor(nextFasta, comments);
		
		PhdInfo info = ConsedUtil.generateDefaultPhdInfoFor(fastaFile, id, phdDate);
		return new DefaultPhdReadRecord(phd, info);
	}
	
	/**
     * Add any additional comments if needed.
     * By default this method does not add
     * any more comments.  Subclasses
     * may override this method to add new values.
     * @param id the id of this sequence
     * @param preExistingComments comments that already exist
     * @return a {@link Properties} object which contains
     * any pre-existing comments and any new ones;
     * can not be null.
     */
    protected Properties createAdditionalCommentsFor(String id,
            Properties preExistingComments) {
        return preExistingComments;
    }
    protected Phd createPhdRecordFor(NucleotideSequenceFastaRecord nextFasta, Properties requiredComments ){
	    String id = nextFasta.getId();
        QualitySequence qualities = getQualitiesFor(nextFasta);
        return ArtificialPhd.createNewbler454Phd(
                id, 
                nextFasta.getSequence(), 
                qualities,
                requiredComments);
	}
	
    protected QualitySequence getQualitiesFor(
    		NucleotideSequenceFastaRecord nextFasta) {
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
	public void close() throws IOException {
		fastaIterator.close();
		
	}

}
