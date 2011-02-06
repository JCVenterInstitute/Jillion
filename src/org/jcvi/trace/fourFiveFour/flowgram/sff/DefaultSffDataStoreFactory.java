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
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.IOException;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.QualityGlyphCodec;

public class DefaultSffDataStoreFactory implements SffDataStoreFactory{

    private final QualityGlyphCodec qualityCodec;
    
    /**
     * @param shouldTrim
     * @param qualityCodec
     */
    public DefaultSffDataStoreFactory(QualityGlyphCodec qualityCodec) {
        this.qualityCodec = qualityCodec;
    }
    
    /**
     * @param shouldTrim
     * @param qualityCodec
     */
    public DefaultSffDataStoreFactory() {
        this(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
    }

    @Override
    public SffDataStore createDataStoreFor(File sffFile) throws IOException {
        try {
            return new DefaultSffFileDataStore(sffFile, qualityCodec);
        } catch (SFFDecoderException e) {
            throw new IOException("could not parse SFF file",e);
        }
    }

}
