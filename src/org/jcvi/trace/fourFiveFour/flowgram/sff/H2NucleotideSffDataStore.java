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
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class H2NucleotideSffDataStore extends AbstractH2SffDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> implements NucleotideDataStore{
    /**
     * @param sffFile
     * @param datastore
     * @param trim
     * @throws SFFDecoderException
     * @throws FileNotFoundException
     */
    public H2NucleotideSffDataStore(
            File sffFile,
            AbstractH2EncodedGlyphDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> datastore,
            boolean trim) throws SFFDecoderException, FileNotFoundException {
        super(sffFile, datastore, trim);
    }
    /**
     * @param sffFile
     * @param datastore
     * @throws SFFDecoderException
     * @throws FileNotFoundException
     */
    public H2NucleotideSffDataStore(
            File sffFile,
            AbstractH2EncodedGlyphDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> datastore)
            throws SFFDecoderException, FileNotFoundException {
        super(sffFile, datastore);
    }
    
    
    /**
     * @param sffFile
     * @param datastore
     * @param filter
     * @param trim
     * @throws SFFDecoderException
     * @throws FileNotFoundException
     */
    public H2NucleotideSffDataStore(
            File sffFile,
            AbstractH2EncodedGlyphDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> datastore,
            DataStoreFilter filter, boolean trim) throws SFFDecoderException,
            FileNotFoundException {
        super(sffFile, datastore, filter, trim);
    }
    /**
     * @param sffFile
     * @param datastore
     * @param filter
     * @param trim
     * @throws SFFDecoderException
     * @throws FileNotFoundException
     */
    public H2NucleotideSffDataStore(
            File sffFile,
            AbstractH2EncodedGlyphDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> datastore,
            DataStoreFilter filter) throws SFFDecoderException,
            FileNotFoundException {
        super(sffFile, datastore, filter, false);
    }
    @Override
    protected String getDataRecord(SFFReadHeader readHeader,
            SFFReadData readData, boolean shouldTrim) {
        String basecalls =readData.getBasecalls();
        if(shouldTrim){
            Range trimRange =SFFUtil.getTrimRangeFor(readHeader);
            basecalls =basecalls.substring((int)trimRange.getStart(), (int)trimRange.getEnd()+1);
        }
        return basecalls;
    }
   
 
    
    
    
}
