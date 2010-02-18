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
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestCommaSeparatedIdReader {

    CommaSeparatedIdReader<String> sut;
    IdParser<String> mockIdReader;
    
    String commaSepList = "first,second,third,fourth";
    @Before
    public void setup(){
        mockIdReader = createMock(IdParser.class);
        sut = new CommaSeparatedIdReader<String>(commaSepList,mockIdReader);
    }
    
    @Test
    public void getIds(){
        expectId("first");
        expectId("second");
        expectId("third");
        expectId("fourth");
        replay(mockIdReader);
        int count=0;
        String[] splitString =commaSepList.split(",");
        for(Iterator<String> ids = sut.getIds(); ids.hasNext();count++){
            assertEquals(splitString[count], ids.next());
        }
        verify(mockIdReader);
    }

    private void expectId(final String string) {
        expect(mockIdReader.parseIdFrom(string)).andReturn(string);
    }
}
