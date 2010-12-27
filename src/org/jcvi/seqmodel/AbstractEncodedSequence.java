package org.jcvi.seqmodel;

import java.util.List;

import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;
import org.jcvi.glyph.GlyphCodec;

/*
 * {@code AbstractEncodedSequence} is an abstract class often used to
 * define any type of encode sequence such as {@link AminoAcidSequence} or
 * {@link NucleotideSequence}.
 * 
 * @author naxelrod
 */
 
public abstract class AbstractEncodedSequence<T extends Glyph> extends Sequence implements SequenceI {

	protected GlyphCodec<T> codec;
	protected EncodedGlyphs<T> glyphs;
	
	public AbstractEncodedSequence(GlyphCodec<T> codec) {
		super();
		this.codec = codec;
	}
	
	public EncodedGlyphs<T> getGlyphs() {
		return glyphs;
	}
	public void setGlyphs(EncodedGlyphs<T> glyphs) {
		this.glyphs = glyphs;
	}

	@Override
	public String getSequence() {
		StringBuffer buff = new StringBuffer();
		if (glyphs != null) {
			for (Glyph g : glyphs.decode()) {
				buff.append(g.toString());
			}
			return buff.toString();
		}
		return null;
	}
	
	@Override
	public void setSequence(String glyphs) {
		this.glyphs = new DefaultEncodedGlyphs<T>(codec, 
				convertStringToGlyph(glyphs));
		setSequence(getSequence());
	}
	
	public abstract List<T> convertStringToGlyph(String glyphs);

}
