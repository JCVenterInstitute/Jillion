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
