package org.jcvi.common.core.seq;

public class DefaultByteGlyphFactory extends  ByteGlyphFactory<ByteGlyph>{

	private static final DefaultByteGlyphFactory INSTANCE = new DefaultByteGlyphFactory();
	
	public static final DefaultByteGlyphFactory getInstance(){
		return INSTANCE;
	}
	private DefaultByteGlyphFactory(){}
	
    @Override
    protected ByteGlyph createNewGlyph(Byte b) {
        return new ByteGlyph(b);
    }
    
}
