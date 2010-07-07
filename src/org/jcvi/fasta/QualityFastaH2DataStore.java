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

package org.jcvi.fasta;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

/**
 * {@code QualityFastaH2DataStore} is an {@link AbstractFastaH2DataStore}
 * implementation for storing Quality data from quality fasta records
 * in an H2 database.
 * @author dkatzel
 *
 *
 */
public class QualityFastaH2DataStore extends AbstractFastaH2DataStore<PhredQuality,EncodedGlyphs<PhredQuality>> implements QualityDataStore{

    /**
     * @param fastaFile
     * @param h2Datastore
     * @param filter
     * @throws FileNotFoundException
     */
    public QualityFastaH2DataStore(
            File fastaFile,
            AbstractH2EncodedGlyphDataStore<PhredQuality, EncodedGlyphs<PhredQuality>> h2Datastore,
            DataStoreFilter filter) throws FileNotFoundException {
        super(fastaFile, h2Datastore, filter);
    }

    /**
     * @param fastaFile
     * @param h2Datastore
     * @throws FileNotFoundException
     */
    public QualityFastaH2DataStore(
            File fastaFile,
            AbstractH2EncodedGlyphDataStore<PhredQuality, EncodedGlyphs<PhredQuality>> h2Datastore)
            throws FileNotFoundException {
        super(fastaFile, h2Datastore);
    }

 

}
