/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.io.DataInputStream;

import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramImpl;
import org.jcvi.trace.sanger.chromatogram.scf.header.DefaultSCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
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
     * data in the given {@link DefaultSCFHeader} and {@link SCFChromatogramImpl}.
     * @param in SCF Data Stream.
     * @param currentOffset the current offset into the SCF DataStream.
     * @param header the {@link DefaultSCFHeader} that explains where the Section data is for this
     * stream.  May be modified during method call to set new fields.
     * @param c SCFChromatogramStruct where newly decoded data will be set.  Will be modified.
     * @return the new currentOffset after decoding of this {@link Section} is complete.
     * @throws SectionDecoderException if there are any problems decoding this {@link Section}.
     */
    long decode(DataInputStream in, long currentOffset,SCFHeader header, SCFChromatogramBuilder c) throws SectionDecoderException;
}
