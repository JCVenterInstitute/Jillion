/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.trace.sanger.SangerTrace;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeaderCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionCodecFactory;

public class Version2SCFCodec extends AbstractSCFCodec {

    /**
     * 
     */
    public Version2SCFCodec() {
        super();
    }

    public Version2SCFCodec(SCFHeaderCodec headerCodec,
            SectionCodecFactory sectionCodecFactory) {
        super(headerCodec, sectionCodecFactory);
    }

    @Override
    public void encode(SangerTrace c, OutputStream out) throws IOException {
        this.encode(out, (SCFChromatogram)c, 2);

    }

}
