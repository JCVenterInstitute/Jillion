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
 * Created on Jan 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.util.List;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideGlyphFactory;

public class DefaultEncodedNucleotideFastaRecord extends AbstractNucleotideSequenceFastaRecord{

    private static NucleotideGlyphFactory factory = NucleotideGlyphFactory.getInstance();
    
    public DefaultEncodedNucleotideFastaRecord(String identifier, EncodedGlyphs<NucleotideGlyph> sequence){
        super(identifier, NucleotideGlyph.convertToString(sequence.decode()));
    }
    public DefaultEncodedNucleotideFastaRecord(String identifier, String comments, EncodedGlyphs<NucleotideGlyph> sequence){
        super(identifier, comments,NucleotideGlyph.convertToString(sequence.decode()));
    }
    public DefaultEncodedNucleotideFastaRecord(String identifier, String comments, List<NucleotideGlyph> sequence){
        super(identifier, comments,NucleotideGlyph.convertToString(sequence));
    }
    /**
     * @param identifier
     * @param sequence
     */
    public DefaultEncodedNucleotideFastaRecord(int identifier, CharSequence sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultEncodedNucleotideFastaRecord(int identifier, String comments,
            CharSequence sequence) {
        super(identifier, comments, sequence);
    }

    /**
     * @param identifier
     * @param sequence
     */
    public DefaultEncodedNucleotideFastaRecord(String identifier, CharSequence sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultEncodedNucleotideFastaRecord(String identifier, String comments,
            CharSequence sequence) {
        super(identifier, comments, sequence);
    }

    @Override
    protected CharSequence decodeNucleotides() {

        StringBuilder result = new StringBuilder();
        for(NucleotideGlyph glyph : getValues().decode()){
            result.append(glyph.getCharacter());
        }
        return result;
    }

    @Override
    protected NucleotideEncodedGlyphs encodeNucleotides(
            CharSequence sequence) {
        return new DefaultNucleotideEncodedGlyphs( factory.getGlyphsFor(sequence));
    }


   
}
