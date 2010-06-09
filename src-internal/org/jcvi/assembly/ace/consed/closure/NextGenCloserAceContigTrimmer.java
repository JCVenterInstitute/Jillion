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

package org.jcvi.assembly.ace.consed.closure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.assembly.ace.AceAssembly;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceContigDataStore;
import org.jcvi.assembly.ace.AceContigTrimmer;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.assembly.ace.AceFileVisitor;
import org.jcvi.assembly.ace.AceFileWriter;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.ace.ConsensusAceTag;
import org.jcvi.assembly.ace.DefaultAceAssembly;
import org.jcvi.assembly.ace.DefaultAceTagMap;
import org.jcvi.assembly.ace.DefaultWholeAssemblyAceTag;
import org.jcvi.assembly.ace.ReadAceTag;
import org.jcvi.assembly.ace.WholeAssemblyAceTag;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.trim.MinimumBidirectionalEndCoverageTrimmer;
import org.jcvi.assembly.trim.MinimumEndCoverageTrimmer;
import org.jcvi.assembly.trim.PlacedReadTrimmer;
import org.jcvi.assembly.trim.TrimmerException;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.MemoryMappedAceFileDataStore;
import org.jcvi.datastore.SimpleDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.trace.sanger.phd.LargePhdDataStore;
import org.jcvi.trace.sanger.phd.PhdDataStore;
import org.joda.time.DateTimeUtils;

/**
 * @author dkatzel
 *
 *
 */
public class NextGenCloserAceContigTrimmer extends AceContigTrimmer{

    /**
     * @param trimmers
     */
    public NextGenCloserAceContigTrimmer(int minimumEndCoverage, int minBiDirectionalEndCoverage, int ignoreThresholdEndCoverage){
        super( Arrays.<PlacedReadTrimmer<AcePlacedRead, AceContig>>asList(
                        new MinimumEndCoverageTrimmer<AcePlacedRead, AceContig>(minimumEndCoverage),
                        new MinimumBidirectionalEndCoverageTrimmer<AcePlacedRead, AceContig>(minBiDirectionalEndCoverage, ignoreThresholdEndCoverage)));
          
    }

    @Override
    protected String createNewContigId(String oldContigId, NucleotideEncodedGlyphs oldConsensus, Range newContigRange){
       String id= super.createNewContigId(oldContigId, oldConsensus, newContigRange);
       Pattern pattern = Pattern.compile("^(\\S+)_(\\d+)_(\\d+)$");
       Matcher trimmedSplitmatcher = pattern.matcher(id);
       if(!trimmedSplitmatcher.matches()){
           return id;
       }
       String untrimmedId = trimmedSplitmatcher.group(1);
       
       Matcher _0xMatcher = pattern.matcher(untrimmedId);
       if(!_0xMatcher.matches()){
           return id;
       }
       int trimmedLeft = Integer.parseInt(trimmedSplitmatcher.group(2));
       int trimmedRight = Integer.parseInt(trimmedSplitmatcher.group(3));
       String originalId = _0xMatcher.group(1);
       int _0xLeft = Integer.parseInt(_0xMatcher.group(2));
       return String.format("%s_%d_%d",originalId,_0xLeft +trimmedLeft, _0xLeft+trimmedRight);
    }
    
    public static void main(String[] args) throws TrimmerException, IOException, DataStoreException{
        File aceFile = new File("/usr/local/projects/VHTNGS/sample_data/giv3/MCE/30209/mapping/consed_with_sanger/edit_dir/cas2consed.ace.1");
        File phdFile = new File("/usr/local/projects/VHTNGS/sample_data/giv3/MCE/30209/mapping/consed_with_sanger/edit_dir/phd.ball");
        
        AceContigDataStore datastore = new MemoryMappedAceFileDataStore(aceFile);
        AceFileParser.parseAceFile(aceFile, (AceFileVisitor)datastore);
        NextGenCloserAceContigTrimmer trimmer = new NextGenCloserAceContigTrimmer(5, 5, 10);
        Map<String, AceContig> aceContigs = new HashMap<String, AceContig>();
        for(AceContig aceContig : datastore){
            AceContig trimmedAceContig =trimmer.trimContig(aceContig, DefaultCoverageMap.buildCoverageMap(aceContig.getPlacedReads()));
            if(trimmedAceContig ==null){
                System.out.printf("%s was completely trimmed... skipping%n", aceContig.getId());
                continue;
            }
            final String id = trimmedAceContig.getId();
            System.out.println(id);
            aceContigs.put(id, trimmedAceContig);
        }
        
        PhdDataStore phdDataStore = new LargePhdDataStore(phdFile);
        WholeAssemblyAceTag pathToPhd = new DefaultWholeAssemblyAceTag("phdball", "cas2consed", 
                new Date(DateTimeUtils.currentTimeMillis()), "../phd_dir/cas2consed.phd.ball");
       
        AceAssembly aceAssembly = new DefaultAceAssembly<AceContig>(new SimpleDataStore<AceContig>(aceContigs), 
                phdDataStore, 
                Collections.<File>emptyList(),
                new DefaultAceTagMap(Collections.<ConsensusAceTag>emptyList(), Collections.<ReadAceTag>emptyList(), 
                        Arrays.asList(pathToPhd)));
        OutputStream out = new FileOutputStream("/usr/local/scratch/dkatzel/trimmedAce_30209.ace");
        AceFileWriter.writeAceFile(aceAssembly, out);
    }
}
