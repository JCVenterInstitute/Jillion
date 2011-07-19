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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.ace;

import java.io.IOException;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.ace.ConsensusAceTag;
import org.jcvi.common.core.assembly.contig.ace.DefaultAceFileTagMap;
import org.jcvi.common.core.assembly.contig.ace.DefaultConsensusAceTag;
import org.jcvi.common.core.assembly.contig.ace.DefaultWholeAssemblyAceTag;
import org.jcvi.common.core.assembly.contig.ace.WholeAssemblyAceTag;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestDefaultAceFileTagMap {

    ResourceFileServer RESOURCES = new ResourceFileServer(TestDefaultAceFileTagMap.class);
    String fileName = "files/sample.ace";
    
    WholeAssemblyAceTag expectedWholeAssemblyTag = new DefaultWholeAssemblyAceTag(
            "phrap_params", "phrap", 
            new DateTime(1999, 6, 21,16, 19, 47, 0).toDate(), 
            "/usr/local/genome/bin/phrap standard.fasta.screen -new_ace -view \nphrap version 0.990319\n");
    
    ConsensusAceTag consensusTag0 = new DefaultConsensusAceTag.Builder(
                                            "Contig1", "repeat", "consed",
                                            new DateTime(1997, 12, 18, 18, 6, 23, 0).toDate(), 
                                            Range.buildRange(976,986), 
                                            false)
                                            .build();
    ConsensusAceTag consensusTag1 = new DefaultConsensusAceTag.Builder(
            "Contig1", "comment", "consed",
            new DateTime(1997, 12, 18, 18, 6, 23, 0).toDate(), 
            Range.buildRange(996,1007), 
            false)
            .appendData("This is line 1 of a comment\nThere may be any number of lines\n")
            .build();
    
    ConsensusAceTag consensusTag2 = new DefaultConsensusAceTag.Builder(
            "Contig1", "oligo", "consed",
            new DateTime(1997, 12, 18, 18, 6, 23, 0).toDate(), 
            Range.buildRange(963,987), 
            false)
            .appendData("standard.1 acataagacattctaaatttttact 50 U\nseq from clone\n")
            .build();
    
    ConsensusAceTag consensusTag3 = new DefaultConsensusAceTag.Builder(
            "Contig853", "join", "consed",
            new DateTime(2009, 12, 28, 11, 38, 57, 0).toDate(), 
            Range.buildRange(437,437), 
            false)
            .addComment("old contigs:\nContig844 pinned pos: 511 length: 1324 reads: 1\nContig850 pinned pos: 23 length: 208,876 reads: 29,325\nace file: /local/closure10/HMP/HMP084/Newbler_091709_consed/hmp084/assembly/cons\ned/edit_dir/454Contigs.ace.176\nnew contig Contig853  length: 208,876 reads: 29,326\n")
            .build();
    
    ConsensusAceTag consensusTag4 = new DefaultConsensusAceTag.Builder(
            "Contig853", "contigEndPair", "consed",
            new DateTime(2009, 12, 28, 12, 10, 44, 0).toDate(), 
            Range.buildRange(10,10), 
            false)
            .appendData("3\n<-gap\nggcctcgggg\n")
            .build();
    
    DefaultAceFileTagMap sut;
    @Before
    public void setup() throws IOException{
        sut = new DefaultAceFileTagMap(RESOURCES.getFile(fileName));
    }
    @Test
    public void wholeAssemblyTag(){
        List<WholeAssemblyAceTag> tags = sut.getWholeAssemblyTags();
        assertEquals(1,tags.size());
        final WholeAssemblyAceTag wholeAssemblyAceTag = tags.get(0);
        assertEquals(expectedWholeAssemblyTag, wholeAssemblyAceTag);
    }
    
    @Test
    public void consensusTags(){
        List<ConsensusAceTag> actualTags = sut.getConsensusTags();
        assertEquals(5, actualTags.size());
        assertEquals(consensusTag0, actualTags.get(0));
        assertEquals(consensusTag1, actualTags.get(1));
        assertEquals(consensusTag2, actualTags.get(2));
        assertEquals(consensusTag3, actualTags.get(3));
        assertEquals(consensusTag4, actualTags.get(4));
    }
    
}
