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

package org.jcvi.common.core.assembly.ace;

import java.io.IOException;

import org.jcvi.common.core.assembly.ace.AceFileParser;
import org.jcvi.common.core.assembly.ace.AceFileVisitor;
import org.jcvi.common.core.assembly.ace.AceFileVisitor.BeginContigReturnCode;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAceFileParserWithInvalidGapChar {

    @Test
    public void basecallInReadContainDashInsteadOfStar() throws IOException{
        String problemLine = "agccgaaggagg*ttttggaaacaccaaggg-g*ggtcagaccccaacgc\n";
        ResourceFileServer resources = new ResourceFileServer(TestAceFileParserWithInvalidGapChar.class);
        AceFileVisitor mockVisitor = createNiceMock(AceFileVisitor.class);
        expect(mockVisitor.visitBeginContig(isA(String.class), anyInt(), anyInt(), anyInt(), anyBoolean()))
        			.andReturn(BeginContigReturnCode.SKIP_CURRENT_CONTIG);
        replay(mockVisitor);
        try{
            AceFileParser.parse(resources.getFile("files/invalidAceFileWithDash.ace"), mockVisitor);
            fail("should error out");
        }catch(IllegalStateException e){
            assertEquals(
                    String.format("invalid ace file: found '-' used as a gap instead of '*' : %s", problemLine)
                    ,e.getMessage());
        }
        
    }
}
