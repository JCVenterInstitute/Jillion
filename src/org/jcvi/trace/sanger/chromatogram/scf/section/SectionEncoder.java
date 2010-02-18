/*
 * Created on Sep 15, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.io.IOException;

import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogram;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;

public interface SectionEncoder {

    EncodedSection encode(SCFChromatogram c, SCFHeader header) throws IOException;
}
