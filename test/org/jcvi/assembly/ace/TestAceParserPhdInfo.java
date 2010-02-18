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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.assembly.Contig;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

public class TestAceParserPhdInfo {
    private static final String ACE_FILE = "files/sample.ace";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd kk:mm:ss yyyy");
    private static Contig<AcePlacedRead> actualContig = new AceParser(TestAceParserMatchesAce2ContigSingleContig.class.getResourceAsStream(ACE_FILE)).parseContigsFrom().get(0);
    
    Map<String, PhdInfo> phdInfoMap;
    @Before
    public void setupMap(){
        phdInfoMap = new HashMap<String, PhdInfo>();
        phdInfoMap.put("K26-217c", 
                    new DefaultPhdInfo("K26-217c", "K26-217c.phd.1",
                                DATE_TIME_FORMATTER.parseDateTime(
                                        "Thu Sep 12 15:42:38 1996").toDate()));
        phdInfoMap.put("K26-526t", 
                new DefaultPhdInfo("K26-526t", "K26-526t.phd.1",
                            DATE_TIME_FORMATTER.parseDateTime(
                                    "Thu Sep 12 15:42:33 1996").toDate()));
        phdInfoMap.put("K26-961c", 
                new DefaultPhdInfo("K26-961c", "K26-961c.phd.1",
                            DATE_TIME_FORMATTER.parseDateTime(
                                    "Thu Sep 12 15:42:37 1996").toDate()));
        phdInfoMap.put("K26-394c", 
                new DefaultPhdInfo("K26-394c", "K26-394c.phd.1",
                            DATE_TIME_FORMATTER.parseDateTime(
                                    "Thu Sep 12 15:42:32 1996").toDate()));
        phdInfoMap.put("K26-291s", 
                new DefaultPhdInfo("K26-291s", "K26-291s.phd.1",
                            DATE_TIME_FORMATTER.parseDateTime(
                                    "Thu Sep 12 15:42:31 1996").toDate()));
        phdInfoMap.put("K26-822c", 
                new DefaultPhdInfo("K26-822c", "K26-822c.phd.1",
                            DATE_TIME_FORMATTER.parseDateTime(
                                    "Thu Sep 12 15:42:36 1996").toDate()));
        phdInfoMap.put("K26-572c", 
                new DefaultPhdInfo("K26-572c", "K26-572c.phd.1",
                            DATE_TIME_FORMATTER.parseDateTime(
                                    "Thu Sep 12 15:42:34 1996").toDate()));
        phdInfoMap.put("K26-766c", 
                new DefaultPhdInfo("K26-766c", "K26-766c.phd.1",
                            DATE_TIME_FORMATTER.parseDateTime(
                                    "Thu Sep 12 15:42:35 1996").toDate()));       
    }
    
    @Test
    public void assertPhdInfosCorrect(){
        for(AcePlacedRead read : actualContig.getPlacedReads()){
            assertEquals(phdInfoMap.get(read.getId()), read.getPhdInfo());
        }
    }
}
