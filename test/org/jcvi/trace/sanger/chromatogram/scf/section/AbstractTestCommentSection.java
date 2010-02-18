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
