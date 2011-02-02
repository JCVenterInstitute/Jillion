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
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class H2NucleotideFastQDataStore extends AbstractH2FastQDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> implements NucleotideDataStore{
    
    /**
     * @param fastQFile
     * @param qualityCodec
     * @param datastore
     * @throws FileNotFoundException
     */
    public H2NucleotideFastQDataStore(
            File fastQFile,
            AbstractH2EncodedGlyphDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> datastore)
            throws IOException {
        super(fastQFile, null, datastore);

    }

    
    
    /**
     * @param fastQFile
     * @param qualityCodec
     * @param datastore
     * @param filter
     * @throws FileNotFoundException
     */
    public H2NucleotideFastQDataStore(
            File fastQFile,
            AbstractH2EncodedGlyphDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> datastore,
            DataStoreFilter filter) throws IOException {
        super(fastQFile,null, datastore, filter);
    }



    /**
     * @param fastQFile
     * @param qualityCodec
     * @param datastore
     * @throws FileNotFoundException
     */
    public H2NucleotideFastQDataStore(
            File fastQFile,
            FastQQualityCodec qualityCodec,
            AbstractH2EncodedGlyphDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> datastore)
            throws IOException {
        super(fastQFile, qualityCodec, datastore);
    }



    @Override
    public void visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
        try {
            this.getDatastore().insertRecord(this.getCurrentId(), 
                    NucleotideGlyph.convertToString(nucleotides.decode()));
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not insert qualities for into datastore",e);
        }
    }


}
