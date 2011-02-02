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
package org.jcvi.fastX.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class H2QualityFastQDataStore extends AbstractH2FastQDataStore<PhredQuality, EncodedGlyphs<PhredQuality>> implements QualityDataStore{
    
    /**
     * @param fastQFile
     * @param qualityCodec
     * @param datastore
     * @throws FileNotFoundException
     */
    public H2QualityFastQDataStore(
            File fastQFile,
            FastQQualityCodec qualityCodec,
            AbstractH2EncodedGlyphDataStore<PhredQuality, EncodedGlyphs<PhredQuality>> datastore)
            throws IOException {
        super(fastQFile, qualityCodec, datastore);

    }

    /**
     * @param fastQFile
     * @param qualityCodec
     * @param datastore
     * @param filter
     * @throws FileNotFoundException
     */
    public H2QualityFastQDataStore(
            File fastQFile,
            FastQQualityCodec qualityCodec,
            AbstractH2EncodedGlyphDataStore<PhredQuality, EncodedGlyphs<PhredQuality>> datastore,
            DataStoreFilter filter) throws IOException {
        super(fastQFile, qualityCodec, datastore, filter);
    }

    @Override
    public void visitEncodedQualities(String encodedQualities) {
        StringBuilder builder = new StringBuilder();
        for(PhredQuality quality :this.getQualityCodec().decode(encodedQualities).decode()){
            builder.append(Integer.valueOf(quality.getNumber()))
                            .append(" ");
        }
                
               
        try {
            this.getDatastore().insertRecord(this.getCurrentId(),
                    builder.toString());
        } catch (DataStoreException e) {
          throw new IllegalStateException("could not insert qualities for into datastore",e);
        }
    }


}
