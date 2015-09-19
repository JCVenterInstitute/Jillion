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
 * Created on Sep 15, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.header;

import java.nio.ByteBuffer;
/**
 * <code>SCFHeaderEncoder</code> is used to encode
 * a {@link SCFHeader} into the format specified by the SCF File
 * Format.
 * @author dkatzel
 *
 *
 */
public interface SCFHeaderEncoder {
    /**
     * Encodes the given {@link SCFHeader} into
     * the format specified by the SCF File Format.
     * @param header the header to encode.
     * @return a {@link ByteBuffer} containing
     * the SCF Header data encoded to the SCF
     * File Format Specification.
     */
    ByteBuffer encode(SCFHeader header);
}
