package org.jcvi.seqmodel;

import java.util.List;

import org.jcvi.glyph.aa.AminoAcid;
import org.jcvi.glyph.aa.AminoAcidEncodedGlyphs;
import org.jcvi.glyph.aa.DefaultAminoAcidEncodedGlyphs;
import org.jcvi.glyph.aa.DefaultAminoAcidGlyphCodec;

/**
 * {@code AminoAcidSequence} is the implementation of the SequenceI interface
 * using the DefaultAminoAcidGlyphCodec to encode the sequence as AminoAcids.  
 * 
 * @author naxelrod
 */

public class AminoAcidSequence extends AbstractEncodedSequence<AminoAcid> {

	public AminoAcidSequence() {
		super(DefaultAminoAcidGlyphCodec.getInstance());
	}
	public AminoAcidSequence(String sequence) {
		this();
		this.setSequence(sequence);
	}
	public AminoAcidSequence(String id, String sequence) {
		this(sequence);
		this.id = id;
	}
	
	public AminoAcidSequence(AminoAcidEncodedGlyphs glyphs) {
		this();
		this.glyphs = glyphs;
	}
	public AminoAcidSequence(String id, AminoAcidEncodedGlyphs glyphs) {
		this(glyphs);
		this.id = id;
	}
	public AminoAcidSequence(String id, String accession, Long giNumber, AminoAcidEncodedGlyphs glyphs) {
		this(id, glyphs);
		this.accession = accession;
		this.giNumber = giNumber;
	}	
	

	public AminoAcidSequence(String id, String accession, Long giNumber, String sequence) {
		this(id, sequence);
		this.accession = accession;
		this.giNumber = giNumber;
	}	
	
	public void setSequence(String sequence) {
		glyphs = new DefaultAminoAcidEncodedGlyphs(sequence);
	}
	
	public List<AminoAcid> convertStringToGlyph(String sequence) {
		return AminoAcid.getGlyphsFor(sequence);
	}

}
