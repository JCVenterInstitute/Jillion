package org.jcvi.fastX.fasta.aa;

import org.jcvi.fastX.fasta.FastaRecord;
import org.jcvi.glyph.aa.AminoAcid;
import org.jcvi.glyph.aa.AminoAcidSequence;
import org.jcvi.glyph.aa.DefaultAminoAcidEncodedGlyphs;

public class DefaultAminoAcidSequenceFastaRecord extends AbstractAminoAcidSequenceFastaRecord {

/**
 * Default implementation of Amino Acid encoding for {@link FastaRecord} objects
 * @author naxelrod
 *
 */

    public DefaultAminoAcidSequenceFastaRecord(String identifier, AminoAcidSequence glyphs){
    	super(identifier, AminoAcid.convertToString(glyphs.decode()));
    }
    
	/**
     * @param identifier
     * @param sequence
     */
    public DefaultAminoAcidSequenceFastaRecord(int identifier, CharSequence sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultAminoAcidSequenceFastaRecord(int identifier, String comments,
            CharSequence sequence) {
        super(identifier, comments, sequence);
    }

    /**
     * @param identifier
     * @param sequence
     */
    public DefaultAminoAcidSequenceFastaRecord(String identifier, CharSequence sequence) {
        super(identifier, sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultAminoAcidSequenceFastaRecord(String identifier, String comments,
            CharSequence sequence) {
        super(identifier, comments, sequence);
    }


	@Override
	protected CharSequence decodeAminoAcids() {
		StringBuilder result = new StringBuilder();
		for(AminoAcid aa : getValue().decode()){
			result.append(aa.getAbbreviation());
		}
		return result;
	}

	@Override
	protected AminoAcidSequence encodeAminoAcids(String sequence) {
		return new DefaultAminoAcidEncodedGlyphs( AminoAcid.getGlyphsFor(sequence) );
	}

	
	
}
