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
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import org.junit.Assert;

import org.jcvi.trace.sanger.chromatogram.scf.section.NullSectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionDecoderException;
import org.junit.Test;

public class TestNullSectionDecoder {

    @Test
    public void parseDoesNothing() throws SectionDecoderException{
        long currentOffset = 123456L;
        Assert.assertEquals("current offset should not change",
                currentOffset,
                new NullSectionCodec().decode(null, currentOffset, null, null));
    }
}
