/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import org.junit.Assert;

import org.jcvi.jillion.internal.trace.chromat.scf.section.NullSectionCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionDecoderException;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramFileVisitor;
import org.junit.Test;

public class TestNullSectionDecoder {

    @Test
    public void parseDoesNothing() throws SectionDecoderException{
        long currentOffset = 123456L;
        Assert.assertEquals("current offset should not change",
                currentOffset,
                new NullSectionCodec().decode(null, currentOffset, null, (ScfChromatogramFileVisitor)null));
    }
}
