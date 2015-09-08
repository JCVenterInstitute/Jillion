/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import java.io.IOException;

import org.jcvi.jillion.assembly.consed.ace.AceFileParser;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitor;
import org.jcvi.jillion.internal.ResourceHelper;
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
        ResourceHelper resources = new ResourceHelper(TestAceFileParserWithInvalidGapChar.class);
        AceFileVisitor mockVisitor = createNiceMock(AceFileVisitor.class);
        replay(mockVisitor);
        try{
            AceFileParser.create(resources.getFile("files/invalidAceFileWithDash.ace")).parse(mockVisitor);
            fail("should error out");
        }catch(IllegalStateException e){
            assertEquals(
                    String.format("invalid ace file: found '-' used as a gap instead of '*' : %s", problemLine)
                    ,e.getMessage());
        }
        
    }
}
