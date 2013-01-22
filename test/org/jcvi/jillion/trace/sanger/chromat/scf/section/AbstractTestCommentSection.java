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
/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.chromat.scf.section;

import static org.easymock.EasyMock.createMock;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.internal.trace.sanger.chromat.scf.SCFChromatogramImpl;
import org.jcvi.jillion.internal.trace.sanger.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.sanger.chromat.scf.section.CommentSectionCodec;
import org.jcvi.jillion.trace.sanger.chromat.scf.ScfChromatogramBuilder;
import org.junit.Before;

public class AbstractTestCommentSection {
    CommentSectionCodec sut = new CommentSectionCodec();
    SCFHeader mockHeader;
    ScfChromatogramBuilder chromaStruct;
    SCFChromatogramImpl mockChroma;
    int currentOffset = 0;
    Map<String,String> expectedComments;
    String id = "id";
    @Before
    public void setup(){
        mockHeader = createMock(SCFHeader.class);
        chromaStruct = new ScfChromatogramBuilder(id);
        mockChroma = createMock(SCFChromatogramImpl.class);
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
