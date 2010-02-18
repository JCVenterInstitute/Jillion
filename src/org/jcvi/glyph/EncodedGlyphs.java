/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph;

import java.util.List;

import org.jcvi.Range;

public interface EncodedGlyphs<T extends Glyph> {

    List<T> decode();
    T get(int index);
    long getLength();

    int hashCode();

    boolean equals(Object obj);
    
    List<T> decode(Range range);

}