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
package org.jcvi.jillion.assembly.clc.cas;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

import org.jcvi.jillion.assembly.clc.cas.CasIdLookup;
import org.jcvi.jillion.assembly.clc.cas.DifferentFileCasIdLookupAdapter;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestDifferentFileCasIdLookupAdapter {

    private CasIdLookup mockLookup;
    private DifferentFileCasIdLookupAdapter sut;
    long casReadId = 123456789L;
    File file1 = new File("file1");
    File file2 = new File("file2");
    File file0 = new File("file0");
    String lookupId = "lookup_id";
    @Before
    public void setup(){
        mockLookup = createMock(CasIdLookup.class);
        
    }
    @Test
    public void getCasIdForFromDelegate(){
        Map<String, File> map = new HashMap<String, File>();
        sut = new DifferentFileCasIdLookupAdapter(mockLookup, map);
        expect(mockLookup.getCasIdFor(lookupId)).andReturn(casReadId);
        replay(mockLookup);
        assertEquals(casReadId,sut.getCasIdFor(lookupId));
        verify(mockLookup);
    }
    @Test
    public void getLookupIdFromDelegate(){
        Map<String, File> map = new HashMap<String, File>();
        sut = new DifferentFileCasIdLookupAdapter(mockLookup, map);
        expect(mockLookup.getLookupIdFor(casReadId)).andReturn(lookupId);
        replay(mockLookup);
        assertEquals(lookupId,sut.getLookupIdFor(casReadId));
        verify(mockLookup);
    }
    @Test
    public void getNumberOfIdsFromDelegate(){
        int numIds = 42;
        Map<String, File> map = new HashMap<String, File>();
        sut = new DifferentFileCasIdLookupAdapter(mockLookup, map);
        expect(mockLookup.getNumberOfIds()).andReturn(numIds);
        replay(mockLookup);
        assertEquals(numIds,sut.getNumberOfIds());
        verify(mockLookup);
    }
    @Test
    public void getFileForCasReadIdFromDelegate(){
        Map<String, File> map = new HashMap<String, File>();
        sut = new DifferentFileCasIdLookupAdapter(mockLookup, map);
        expect(mockLookup.getLookupIdFor(casReadId)).andReturn(lookupId);
        expect(mockLookup.getFileFor(lookupId)).andReturn(file1);
        
        replay(mockLookup);
        assertEquals(file1,sut.getFileFor(casReadId));
        verify(mockLookup);
    }
    
    @Test
    public void getDifferentFileForCasReadIdFromDelegate(){
        Map<String, File> map = new HashMap<String, File>();
        map.put(lookupId,file2);
        sut = new DifferentFileCasIdLookupAdapter(mockLookup, map);
        expect(mockLookup.getLookupIdFor(casReadId)).andReturn(lookupId);
        
        replay(mockLookup);
        assertEquals(file2,sut.getFileFor(casReadId));
        verify(mockLookup);
    }
    
    @Test
    public void close() throws IOException{
        Map<String, File> map = new HashMap<String, File>();
        map.put(lookupId,file2);
        sut = new DifferentFileCasIdLookupAdapter(mockLookup, map);
        
        mockLookup.close();
        replay(mockLookup);
        sut.close();
        verify(mockLookup);
    }
    
    @Test
    public void getFiles(){
        Map<String, File> map = new HashMap<String, File>();
        map.put("1",file1);
        map.put("2",file2);
        sut = new DifferentFileCasIdLookupAdapter(mockLookup, map);
        expect(mockLookup.getNumberOfIds()).andReturn(3);
        expect(mockLookup.getLookupIdFor(0L)).andReturn("0");
        expect(mockLookup.getLookupIdFor(1L)).andReturn("1");
        expect(mockLookup.getLookupIdFor(2L)).andReturn("2");
        expect(mockLookup.getFileFor("0")).andReturn(file0);
        replay(mockLookup);
        List<File> expected = Arrays.asList(file0,file1,file2);
        List<File> actual = sut.getFiles();
        assertEquals(expected.size(), actual.size());
        for(File expectedFile : expected){
            assertTrue(actual.contains(expectedFile));
        }
        verify(mockLookup);
    }
}
