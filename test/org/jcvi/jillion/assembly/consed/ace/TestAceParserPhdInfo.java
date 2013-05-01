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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.consed.ace.AceAssembledRead;
import org.jcvi.jillion.assembly.consed.ace.DefaultAceFileDataStore;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestAceParserPhdInfo {
    private static final String ACE_FILE = "files/sample.ace";
    private static final DateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("EEE MMM dd kk:mm:ss yyyy");
    private static final ResourceHelper RESOURCES = new ResourceHelper(TestAceParserPhdInfo.class);
    private static Contig<AceAssembledRead> actualContig;
    
    Map<String, PhdInfo> phdInfoMap;
    @BeforeClass
    public static void parseContig() throws DataStoreException, IOException{
        actualContig =DefaultAceFileDataStore.create(RESOURCES.getFile(ACE_FILE))
                            .get("Contig1");
    }
    @Before
    public void setupMap() throws ParseException{
        phdInfoMap = new HashMap<String, PhdInfo>();
        phdInfoMap.put("K26-217c", 
                    new PhdInfo("K26-217c", "K26-217c.phd.1",
                                DATE_TIME_FORMATTER.parse(
                                        "Thu Sep 12 15:42:38 1996")));
        phdInfoMap.put("K26-526t", 
                new PhdInfo("K26-526t", "K26-526t.phd.1",
                            DATE_TIME_FORMATTER.parse(
                                    "Thu Sep 12 15:42:33 1996")));
        phdInfoMap.put("K26-961c", 
                new PhdInfo("K26-961c", "K26-961c.phd.1",
                            DATE_TIME_FORMATTER.parse(
                                    "Thu Sep 12 15:42:37 1996")));
        phdInfoMap.put("K26-394c", 
                new PhdInfo("K26-394c", "K26-394c.phd.1",
                            DATE_TIME_FORMATTER.parse(
                                    "Thu Sep 12 15:42:32 1996")));
        phdInfoMap.put("K26-291s", 
                new PhdInfo("K26-291s", "K26-291s.phd.1",
                            DATE_TIME_FORMATTER.parse(
                                    "Thu Sep 12 15:42:31 1996")));
        phdInfoMap.put("K26-822c", 
                new PhdInfo("K26-822c", "K26-822c.phd.1",
                            DATE_TIME_FORMATTER.parse(
                                    "Thu Sep 12 15:42:36 1996")));
        phdInfoMap.put("K26-572c", 
                new PhdInfo("K26-572c", "K26-572c.phd.1",
                            DATE_TIME_FORMATTER.parse(
                                    "Thu Sep 12 15:42:34 1996")));
        phdInfoMap.put("K26-766c", 
                new PhdInfo("K26-766c", "K26-766c.phd.1",
                            DATE_TIME_FORMATTER.parse(
                                    "Thu Sep 12 15:42:35 1996")));       
    }
    
    @Test
    public void assertDefaultPhdInfosCorrect(){
    	StreamingIterator<AceAssembledRead> iter=null;
    	try{
    		iter = actualContig.getReadIterator();
    		while(iter.hasNext()){
    			AceAssembledRead read = iter.next();
    			 assertEquals(phdInfoMap.get(read.getId()), read.getPhdInfo());
    		}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter);
    	}
    }
}
