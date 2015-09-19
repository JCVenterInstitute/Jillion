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
package org.jcvi.jillion.internal.trace.chromat.scf.header;

import java.io.DataInputStream;

/**
 * <code>SCFHeaderDecoder</code> can parse the beginning of an SCF data stream
 * and create an {@link SCFHeader}.
 *
 * @author dkatzel
 *
 *
 */
public interface SCFHeaderDecoder {
    /**
     * Parse the beginning of the SCF data contain in the {@link DataInputStream}
     * and create a {@link SCFHeader}.
     * @param in {@link DataInputStream} of the SCF data.
     * @return a populated {@link SCFHeader}.
     * @throws SCFHeaderDecoderException if there are any
     * problems parsing the {@link SCFHeader}.
     */
    SCFHeader decode(DataInputStream in) throws SCFHeaderDecoderException;
}
