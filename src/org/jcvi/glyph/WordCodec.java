package org.jcvi.glyph;

import java.util.Collection;
import java.util.List;

/*
 * Implementing an {@code WordCodec} requires writing
 * methods to encode words to bytes, and decode bytes
 * to words
 *
 * @author naxelrod
 */
 
public interface WordCodec {
	
    byte[][] encode(Collection<String> words);
    
    List<String> decode(byte[][] encodedWords);
    
    String decode(byte[][] encodedGlyphs, int index);
   
}
