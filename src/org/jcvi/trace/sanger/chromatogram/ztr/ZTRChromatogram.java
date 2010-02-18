/*
 * Created on Aug 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr;

import org.jcvi.Range;
import org.jcvi.trace.sanger.chromatogram.Chromatogram;

public interface ZTRChromatogram extends Chromatogram{
    /**
     * Gets the ZTR Specific clip.
     * @return a clip, may be null or empty.
     */
    Range getClip();
    /**
     * Gets the ZTR Specific comment.
     * @return a comment, may be null.
     */
    String getComment();
}
