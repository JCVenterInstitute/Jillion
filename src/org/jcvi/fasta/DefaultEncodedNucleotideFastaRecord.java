/*
 * Created on Jan 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideGlyphFactory;

public class DefaultEncodedNucleotideFastaRecord extends AbstractNucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>{

    private static NucleotideGlyphFactory factory = NucleotideGlyphFactory.getInstance();
    
    public DefaultEncodedNucleotideFastaRecord(String identifier, EncodedGlyphs<NucleotideGlyph> sequence){
        super(identifier, NucleotideGlyph.convertToString(sequence.decode()));
    }
    public DefaultEncodedNucleotideFastaRecord(String identifier, String comments, EncodedGlyphs<NucleotideGlyph> sequence){
        super(identifier, comments,NucleotideGlyph.convertToString(sequence.decode()));
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
