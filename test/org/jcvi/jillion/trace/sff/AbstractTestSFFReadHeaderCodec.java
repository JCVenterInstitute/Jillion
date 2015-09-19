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
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.trace.sff.DefaultSffReadHeader;
import org.jcvi.jillion.trace.sff.DefaultSffReadHeaderDecoder;
import org.jcvi.jillion.trace.sff.SffUtil;

public class AbstractTestSFFReadHeaderCodec {
    protected int numberOfBases=100;
    protected int qual_left = 5;
    protected int qual_right= 100;
    protected  int adapter_left = 10;
    protected  int adapter_right= 100;
    protected  Range qualityClip = Range.of(CoordinateSystem.RESIDUE_BASED, qual_left, qual_right);
    protected Range adapterClip= Range.of(CoordinateSystem.RESIDUE_BASED, adapter_left, adapter_right);
    protected String name = "sequence name";
    protected short headerLength= (short)(16+name.length()+SffUtil.caclulatePaddedBytes(16+name.length()));

    protected DefaultSffReadHeader expectedReadHeader = new DefaultSffReadHeader(numberOfBases,
            qualityClip, adapterClip, name);
    protected DefaultSffReadHeaderDecoder sut = DefaultSffReadHeaderDecoder.INSTANCE;
}
