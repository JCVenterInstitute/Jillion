/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.io.DataInputStream;
import java.io.IOException;

import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;

public class NullSectionCodec implements SectionCodec{

    @Override
    public long decode(DataInputStream in, long currentOffset, SCFHeader header,
            SCFChromatogramBuilder c) throws SectionDecoderException {
        return currentOffset;
    }

    @Override
    public EncodedSection encode(SCFChromatogram c, SCFHeader header)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
