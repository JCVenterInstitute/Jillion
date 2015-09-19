/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.nav;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.consed.nav.ConsedNavigationParser;
import org.jcvi.jillion.assembly.consed.nav.ConsedNavigationVisitor;
import org.jcvi.jillion.assembly.consed.nav.ConsensusNavigationElement;
import org.jcvi.jillion.assembly.consed.nav.ReadNavigationElement;
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
