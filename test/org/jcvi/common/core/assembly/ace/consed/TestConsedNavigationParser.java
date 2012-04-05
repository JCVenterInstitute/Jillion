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

package org.jcvi.common.core.assembly.ace.consed;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.ace.consed.ConsedNavigationParser;
import org.jcvi.common.core.assembly.ace.consed.ConsedNavigationVisitor;
import org.jcvi.common.core.assembly.ace.consed.ConsensusNavigationElement;
import org.jcvi.common.core.assembly.ace.consed.ReadNavigationElement;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsedNavigationParser {

    ResourceFileServer resources = new ResourceFileServer(TestConsedNavigationParser.class);
    
    @Test
    public void parseNavFile() throws IOException{
        File navFile = resources.getFile("files/example.nav");
        ConsedNavigationVisitor mockVisitor = createMock(ConsedNavigationVisitor.class);
        
        mockVisitor.visitFile();
        mockVisitor.visitLine(anyObject(String.class));
        expectLastCall().anyTimes();
        mockVisitor.visitElement(new ReadNavigationElement(
                "B11_hs1-60153193_GGor_050426.f", 
                Range.create(CoordinateSystem.RESIDUE_BASED, 34),
                "a comment"));
        mockVisitor.visitElement(new ConsensusNavigationElement(
                "hs21-15002178_HSap-Contig", 
                Range.create(CoordinateSystem.RESIDUE_BASED, 1774, 1784),
                "another comment"));
        
        mockVisitor.visitEndOfFile();
        replay(mockVisitor);
        ConsedNavigationParser.parse(navFile, mockVisitor);
        verify(mockVisitor);
    }
}
