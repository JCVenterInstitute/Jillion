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

package org.jcvi.assembly.ace.consed;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.jcvi.assembly.ace.PhdInfo;
import org.jcvi.fastX.fasta.seq.NucleotideSequenceFastaRecord;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.DefaultQualityEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.trace.sanger.phd.ArtificialPhd;
import org.jcvi.trace.sanger.phd.Phd;
import org.jcvi.trace.sanger.phd.PhdUtil;
import org.jcvi.util.CloseableIterator;
import org.joda.time.DateTime;

public class FastaConsedPhdAdaptedIterator implements PhdReadRecordIterator{

	private final CloseableIterator<NucleotideSequenceFastaRecord> fastaIterator;
	private final Properties requiredComments;
	private final PhredQuality defaultQualityValue;
	private final DateTime phdDate;
	private final File fastaFile;
	public FastaConsedPhdAdaptedIterator(
			CloseableIterator<NucleotideSequenceFastaRecord> fastaIterator,
			File fastaFile,
			DateTime phdDate,
			PhredQuality defaultQualityValue){
		this.requiredComments = PhdUtil.createPhdTimeStampCommentFor(phdDate);
		this.fastaIterator = fastaIterator;	
		this.defaultQualityValue = defaultQualityValue;
		this.fastaFile = fastaFile;
		this.phdDate = phdDate;
	}
	@Override
	public boolean hasNext() {
		return fastaIterator.hasNext();
	}

	@Override
	public PhdReadRecord next() {
		NucleotideSequenceFastaRecord nextFasta = fastaIterator.next();
		int numberOfQualities =(int) nextFasta.getValue().getLength();
		PhredQuality[] qualities = new PhredQuality[numberOfQualities];
		Arrays.fill(qualities, defaultQualityValue);
		String id = nextFasta.getId();
		DefaultQualityEncodedGlyphs qualities2 = new DefaultQualityEncodedGlyphs(
				RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE, 
				Arrays.asList(qualities));
		Phd phd = ArtificialPhd.createNewbler454Phd(
				id, 
				nextFasta.getValue(), 
				qualities2,
				requiredComments);
		
		PhdInfo info = ConsedUtil.generatePhdInfoFor(fastaFile, id, phdDate);
		return new DefaultPhdReadRecord(phd, info);
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
