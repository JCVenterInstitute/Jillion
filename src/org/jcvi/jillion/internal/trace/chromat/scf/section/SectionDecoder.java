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

import org.jcvi.jillion.internal.trace.chromat.scf.header.DefaultSCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.trace.chromat.ChromatogramFileVisitor;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
/**
 * <code>SectionDecoder</code> is an interface that parses
 * a section of the SCF Data stream.
 * @author dkatzel
 *
 *
 */
public interface SectionDecoder {
    /**
     * Decode a {@link Section} of the SCF Data Stream, will set any Section specific
     * data in the given {@link DefaultSCFHeader} and {@link ScfChromatogram}.
     * @param in SCF Data Stream.
     * @param currentOffset the current offset into the SCF DataStream.
     * @param header the {@link DefaultSCFHeader} that explains where the Section data is for this
     * stream.  May be modified during method call to set new fields.
     * @param c SCFChromatogramStruct where newly decoded data will be set.  Will be modified.
     * @return the new currentOffset after decoding of this {@link Section} is complete.
     * @throws SectionDecoderException if there are any problems decoding this {@link Section}.
     */
    long decode(DataInputStream in, long currentOffset,SCFHeader header, ScfChromatogramBuilder c) throws SectionDecoderException;

    /**
     * Decode a {@link Section} of the SCF Data Stream, will set any Section specific
     * data in the given {@link DefaultSCFHeader} and {@link ScfChromatogram}.
     * @param in SCF Data Stream.
     * @param currentOffset the current offset into the SCF DataStream.
     * @param header the {@link DefaultSCFHeader} that explains where the Section data is for this
     * stream.  May be modified during method call to set new fields.
     * @param c ChromatogramFileVisitor whose visitXXX methods will be called.
     * @return the new currentOffset after decoding of this {@link Section} is complete.
     * @throws SectionDecoderException if there are any problems decoding this {@link Section}.
     */
    long decode(DataInputStream in, long currentOffset,SCFHeader header, ChromatogramFileVisitor c) throws SectionDecoderException;
}
