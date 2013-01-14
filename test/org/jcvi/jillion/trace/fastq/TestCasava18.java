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

package org.jcvi.jillion.trace.fastq;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.internal.ResourceHelper;
import org.jcvi.jillion.trace.fastq.FastqFileParser;
import org.jcvi.jillion.trace.fastq.FastqFileVisitor;
import org.junit.Test;

/**
 * Casava 1.8 changes the fastq mated read names
 * to have the mate pairs have the same read name
 * and the optional comment to be the mate info.
 * Since the mates are in different files this usually isn't 
 * a problem unless you are combining reads from many files (like an assembler).
 * 
 * @author dkatzel
 *
 *
 */
public class TestCasava18 {
    
    @Test
    public void parseMateInfoCorrectly() throws FileNotFoundException, IOException{
        FastqFileVisitor visitor = createNiceMock(FastqFileVisitor.class);
        expect(visitor.visitDefline("EAS139:136:FC706VJ:2:5:1000:12850 1:Y:18:ATCACG",null))
            .andReturn(FastqFileVisitor.DeflineReturnCode.VISIT_CURRENT_RECORD);
        expect(visitor.visitEndOfBody()).andReturn(FastqFileVisitor.EndOfBodyReturnCode.KEEP_PARSING);
        replay(visitor);
        ResourceHelper resources = new ResourceHelper(TestCasava18.class);
        FastqFileParser.parse(resources.getFile("files/casava1.8.fastq"), visitor);
        verify(visitor);
    }
}
