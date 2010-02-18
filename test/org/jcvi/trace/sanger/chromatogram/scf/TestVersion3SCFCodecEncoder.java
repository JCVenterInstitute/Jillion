/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import org.jcvi.trace.sanger.chromatogram.scf.AbstractSCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.Version3SCFCodec;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeaderCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionCodecFactory;

public class TestVersion3SCFCodecEncoder extends
        AbstractTestVersionSCFCodecEncoder {

    @Override
    protected int getVersion() {
        return 3;
    }

    protected AbstractSCFCodec createSCFCodec(SCFHeaderCodec headerCodec,SectionCodecFactory sectionCodecFactory) {
        return new Version3SCFCodec(headerCodec,sectionCodecFactory);
    }


}
