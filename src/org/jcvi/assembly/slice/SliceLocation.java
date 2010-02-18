/*
 * Created on Apr 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.assembly.Location;
import org.jcvi.glyph.phredQuality.PhredQuality;

public interface SliceLocation<T> extends Location<T>{

    PhredQuality getQuality();
}
