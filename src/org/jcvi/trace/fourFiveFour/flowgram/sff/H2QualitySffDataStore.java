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
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.Range;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class H2QualitySffDataStore extends AbstractH2SffDataStore<PhredQuality, EncodedGlyphs<PhredQuality>> implements QualityDataStore{


    /**
     * @param sffFile
     * @param datastore
     * @param trim
     * @throws SFFDecoderException
     * @throws FileNotFoundException
     */
    public H2QualitySffDataStore(
            File sffFile,
            AbstractH2EncodedGlyphDataStore<PhredQuality, EncodedGlyphs<PhredQuality>> datastore,
            boolean trim) throws SFFDecoderException, FileNotFoundException {
        super(sffFile, datastore, trim);
    }

    /**
     * @param sffFile
     * @param datastore
     * @throws SFFDecoderException
     * @throws FileNotFoundException
     */
    public H2QualitySffDataStore(
            File sffFile,
            AbstractH2EncodedGlyphDataStore<PhredQuality, EncodedGlyphs<PhredQuality>> datastore)
            throws SFFDecoderException, FileNotFoundException {
        super(sffFile, datastore);
    }

    @Override
    protected String getDataRecord(SFFReadHeader readHeader,
            SFFReadData readData, boolean shouldTrim) {
        
        byte[] qualities = readData.getQualities();
        StringBuilder builder = new StringBuilder();
        final Range trimRange;
        if(shouldTrim){
            trimRange = SFFUtil.getTrimRangeFor(readHeader);            
             
        }else{
            trimRange = Range.buildRangeOfLength(0, qualities.length);
        }
        //convert array of byte values into a 
        //space separated string representation
        for(long i : trimRange){
            builder.append(Integer.valueOf(qualities[(int)i]))
                    .append(" ");
        }
        return builder.toString();
    }

}
