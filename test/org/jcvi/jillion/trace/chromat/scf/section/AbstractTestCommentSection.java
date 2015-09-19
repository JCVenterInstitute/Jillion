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
/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import static org.easymock.EasyMock.createMock;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.internal.trace.chromat.scf.ScfChromatogramImpl;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.section.CommentSectionCodec;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.junit.Before;

public class AbstractTestCommentSection {
    CommentSectionCodec sut = new CommentSectionCodec();
    SCFHeader mockHeader;
    ScfChromatogramBuilder builder;
    ScfChromatogramImpl mockChroma;
    int currentOffset = 0;
    Map<String,String> expectedComments;
    String id = "id";
    @Before
    public void setup(){
        mockHeader = createMock(SCFHeader.class);
        builder = new ScfChromatogramBuilder(id);
        mockChroma = createMock(ScfChromatogramImpl.class);
        expectedComments = new HashMap<String,String>();
        expectedComments.put("key","value");
        expectedComments.put("test","testing");
    }

    protected String convertPropertiesToSCFComment(Map<String,String>  props){
        StringBuilder result =new StringBuilder();
        for(Entry<String,String> entry : props.entrySet()){
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
            result.append("\n");
        }
        result.append("\0");
        return result.toString();
    }
}
