/*
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class TestDefaultFileIdParser {
    DefaultFileIdReader<String> sut;
    IdParser<String> mockIdReader;
    
    String commaSepList = "first,second,third,fourth";
    File file = new File(TestDefaultFileIdParser.class.getResource("files/ids.txt").getFile());
    
    @Before
    public void setup(){
        mockIdReader = createMock(IdParser.class);
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
