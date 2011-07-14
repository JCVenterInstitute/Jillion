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
 * Created on Dec 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.Arrays;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.EncodedQualitySequence;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.QualitySequence;


public class ArtificalQualityDataStoreFromContig extends AbstractArtificialDataStoreFromContig<QualitySequence> implements QualityDataStore{
    private final byte qualitytoUse;
    
    public ArtificalQualityDataStoreFromContig(
            DataStore<? extends Contig> contigDataStore, PhredQuality qualityToUse) {
        super(contigDataStore);
        this.qualitytoUse = qualityToUse.getNumber().byteValue();
    }

   
    
    @Override
    protected QualitySequence createArtificalTypefor(PlacedRead read) {
        long length =read.getValidRange().getEnd()+1;
       byte[] buf = new byte[(int)length];
       Arrays.fill(buf, qualitytoUse);
        return new EncodedQualitySequence(
                RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE, PhredQuality.valueOf(buf));

    }

}
