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
