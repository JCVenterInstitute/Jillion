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
package org.jcvi.common.io.idReader;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.jcvi.common.io.idReader.DefaultFileIdReader;
import org.jcvi.common.io.idReader.IdParser;
import org.jcvi.common.io.idReader.IdReaderException;
import org.junit.Before;
import org.junit.Test;

public class TestDefaultFileIdParser {
    DefaultFileIdReader<String> sut;
    IdParser<String> mockIdReader;
    
    String commaSepList = "first,second,third,fourth";
    ResourceFileServer RESOURCES = new ResourceFileServer(TestDefaultFileIdParser.class);
    
    File file;
    
    @Before
    public void setup() throws IOException{
        mockIdReader = createMock(IdParser.class);
        file = RESOURCES.getFile("files/ids.txt");
        sut = new DefaultFileIdReader<String>(file,mockIdReader);
       
    }
    
    @Test
    public void getIds() throws IdReaderException{
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
        expect(mockIdReader.isValidId(string)).andReturn(true);
        expect(mockIdReader.parseIdFrom(string)).andReturn(string);
    }
}
