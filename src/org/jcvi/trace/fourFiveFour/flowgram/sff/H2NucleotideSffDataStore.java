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
