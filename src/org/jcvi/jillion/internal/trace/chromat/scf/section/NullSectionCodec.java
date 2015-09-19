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
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.section;

import java.io.DataInputStream;
import java.io.IOException;

import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;

public class NullSectionCodec implements SectionCodec{

    @Override
    public long decode(DataInputStream in, long currentOffset, SCFHeader header,
            ScfChromatogramBuilder c) throws SectionDecoderException {
        return currentOffset;
    }

    @Override
    public EncodedSection encode(Chromatogram c, SCFHeader header)
            throws IOException {
        // do nothing
        return null;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long decode(DataInputStream in, long currentOffset,
            SCFHeader header, ChromatogramFileVisitor c)
            throws SectionDecoderException {
        return currentOffset;
    }

}
