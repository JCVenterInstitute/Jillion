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

package org.jcvi.common.core.seq.fastx.fastq;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

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
        FastQFileVisitor visitor = createNiceMock(FastQFileVisitor.class);
        expect(visitor.visitBeginBlock("EAS139:136:FC706VJ:2:5:1000:12850", "1:Y:18:ATCACG"))
            .andReturn(true);
        
        replay(visitor);
        ResourceFileServer resources = new ResourceFileServer(TestCasava18.class);
        FastQFileParser.parse(resources.getFile("files/casava1.8.fastq"), visitor);
        verify(visitor);
    }
}
