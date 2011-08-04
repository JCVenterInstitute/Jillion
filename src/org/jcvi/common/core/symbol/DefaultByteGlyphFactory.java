package org.jcvi.common.core.symbol;

public class DefaultByteGlyphFactory extends  ByteGlyphFactory<ByteSymbol>{

	private static final DefaultByteGlyphFactory INSTANCE = new DefaultByteGlyphFactory();
	
	public static final DefaultByteGlyphFactory getInstance(){
		return INSTANCE;
	}
	private DefaultByteGlyphFactory(){}
	
    @Override
    protected ByteSymbol createNewGlyph(Byte b) {
        return new ByteSymbol(b);
    }
    
}
