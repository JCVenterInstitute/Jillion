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
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq.illumina;

import org.jcvi.fasta.fastq.AbstractFastQQualityCodec;
import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
/**
 * {@code SolexaFastQQualityCodec} supports Illumina (Solexa 1.3+)
 * FastQ format.
 * @author dkatzel
 *
 *
 */
public class IlluminaFastQQualityCodec extends AbstractFastQQualityCodec{

    public IlluminaFastQQualityCodec(GlyphCodec<PhredQuality> qualityCodec) {
        super(qualityCodec);
    }

    @Override
    protected PhredQuality decode(char encodedQuality) {
        return PhredQuality.valueOf(encodedQuality -64);
    }

    @Override
    protected char encode(PhredQuality quality) {
        return (char)(quality.getNumber().intValue()+64);
    }
    

}
