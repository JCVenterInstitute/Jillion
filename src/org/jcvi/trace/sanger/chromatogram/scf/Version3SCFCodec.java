/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

public class Version3SCFCodec  extends AbstractSCFCodec {

    /**
     * 
     */
    public Version3SCFCodec() {
        super();
    }

    public Version3SCFCodec(SCFHeaderCodec headerCodec,
            SectionCodecFactory sectionCodecFactory) {
        super(headerCodec, sectionCodecFactory);
    }

    @Override
    public void encode(SangerTrace c, OutputStream out) throws IOException {
        this.encode(out, (SCFChromatogram)c, 3);
    }

}
