/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 12, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.trace.chromat.BasicChromatogram;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;

public class ZTRChromatogramImpl extends BasicChromatogram implements ZtrChromatogram{

    /**
     * Hints for valid range of this sequence.
     */
    private Range clip;
    /**
     * Constructor that takes a {@link Chromatogram} without
     * ztr specific parameters.
     * @param c the {@link Chromatogram} instance to wrap;
     * can not be null.
     * @throws NullPointerException if c is null.
     */
    public ZTRChromatogramImpl(Chromatogram c){
        this(c, null);
    }
    /**
     * Constructor that takes a {@link Chromatogram} and a ZTR clip
     * point as a Range.
     * @param c the {@link Chromatogram} instance to wrap;
     * can not be null.
     * @param clip the ZTR clip points as  {@link Range};
     * may be null (which means no clip points have been set).
     * @throws NullPointerException if c is null.
     */
    public ZTRChromatogramImpl(Chromatogram c, Range clip) {
        super(c);
        this.clip = clip;
    }
    /**
     * Gets the ZTR Specific clip.
     * @return a clip, may be null.
     */
    public Range getClip() {
        return clip;
    }

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        //keep superclass'es equals
        return super.equals(obj);
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        //keep superclass hascode
        return super.hashCode();
    }
    
    
}
