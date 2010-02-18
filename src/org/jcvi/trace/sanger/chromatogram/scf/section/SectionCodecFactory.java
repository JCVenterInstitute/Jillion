/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;

public interface SectionCodecFactory {

    SectionDecoder getSectionParserFor(Section s, SCFHeader header);

    SectionEncoder getSectionEncoderFor(Section s, float version);
}
