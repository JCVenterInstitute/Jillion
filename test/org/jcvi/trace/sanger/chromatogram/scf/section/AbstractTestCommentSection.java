/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import static org.easymock.classextension.EasyMock.createMock;

import java.util.Properties;
import java.util.Map.Entry;

import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramImpl;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.section.CommentSectionCodec;
import org.junit.Before;

public class AbstractTestCommentSection {
    CommentSectionCodec sut = new CommentSectionCodec();
    SCFHeader mockHeader;
    SCFChromatogramBuilder chromaStruct;
    SCFChromatogramImpl mockChroma;
    int currentOffset = 0;
    Properties expectedComments;
    @Before
    public void setup(){
        mockHeader = createMock(SCFHeader.class);
        chromaStruct = new SCFChromatogramBuilder();
        mockChroma = createMock(SCFChromatogramImpl.class);
        expectedComments = new Properties();
        expectedComments.put("key","value");
        expectedComments.put("test","testing");
    }

    protected String convertPropertiesToSCFComment(Properties props){
        StringBuilder result =new StringBuilder();
        for(Entry<Object,Object> entry : props.entrySet()){
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
            result.append("\n");
        }
        result.append("\0");
        return result.toString();
    }
}
