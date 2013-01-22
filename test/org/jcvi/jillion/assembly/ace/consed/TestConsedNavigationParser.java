/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
package org.jcvi.jillion.assembly.ace.consed;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsedNavigationParser {

    ResourceHelper resources = new ResourceHelper(TestConsedNavigationParser.class);
    
    @Test
    public void parseNavFile() throws IOException{
        File navFile = resources.getFile("files/example.nav");
        ConsedNavigationVisitor mockVisitor = createMock(ConsedNavigationVisitor.class);
        
        mockVisitor.visitFile();
        mockVisitor.visitLine(anyObject(String.class));
        expectLastCall().anyTimes();
        mockVisitor.visitElement(new ReadNavigationElement(
                "B11_hs1-60153193_GGor_050426.f", 
                Range.of(33),
                "a comment"));
        mockVisitor.visitElement(new ConsensusNavigationElement(
                "hs21-15002178_HSap-Contig", 
                Range.of(CoordinateSystem.RESIDUE_BASED, 1774, 1784),
                "another comment"));
        
        mockVisitor.visitEndOfFile();
        replay(mockVisitor);
        ConsedNavigationParser.parse(navFile, mockVisitor);
        verify(mockVisitor);
    }
}
