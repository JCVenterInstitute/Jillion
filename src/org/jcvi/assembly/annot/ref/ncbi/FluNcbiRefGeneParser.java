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
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref.ncbi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jcvi.Range;
import org.jcvi.assembly.annot.DefaultExon;
import org.jcvi.assembly.annot.Exon;
import org.jcvi.assembly.annot.Frame;
import org.jcvi.assembly.annot.Strand;
import org.jcvi.assembly.annot.ref.CodingRegion;
import org.jcvi.assembly.annot.ref.CodingRegionState;
import org.jcvi.assembly.annot.ref.DefaultCodingRegion;
import org.jcvi.assembly.annot.ref.DefaultRefGene;
import org.jcvi.assembly.annot.ref.RefGene;
import org.jcvi.assembly.annot.ref.RefUtil;
import org.jcvi.io.IOUtil;


public class FluNcbiRefGeneParser {
    private static Pattern SOURCE_PARSER = Pattern.compile("VERSION\\s+(\\S+)\\s+GI:(\\d+)");
    private static Pattern  GENE_REGION_PARSER = Pattern.compile(">gene</a>\\s+(\\S+)\\s+/gene=\"(\\w+)\"(.*?translation=\".*?\")");   
    private static Pattern  CODING_REGION_PARSER = Pattern.compile(">CDS</a>\\s+(\\S+)\\s+/gene=\"\\w+\"\\s+.*/codon_start=(\\d)");
    private static Pattern EXON_PARSER = Pattern.compile("(<?\\d+..>?\\d+)");
    private static int ID_COUNTER = 1;
    
    private static synchronized int createNewId(){
        return ID_COUNTER++;
    }
    public List<RefGene> parse(InputStream ncbiXml) throws IOException{
        List<RefGene> refGenes = new ArrayList<RefGene>();
        String response =IOUtil.readStream(ncbiXml).replaceAll("\n", "");
        Strand strand=Strand.FORWARD;
        String referenceName = null;
        Matcher sourceMatcher = SOURCE_PARSER.matcher(response);
        if(sourceMatcher.find()){
            referenceName = "gi|"+sourceMatcher.group(2)+"|gb|"+sourceMatcher.group(1)+"|";
        }
       
        Matcher geneMatcher = GENE_REGION_PARSER.matcher(response);
        while(geneMatcher.find()){
            final String geneName = geneMatcher.group(2);
            Range transcriptionRange =parseTranscriptionRange(geneMatcher.group(1));
            final String codingRegionResponse = geneMatcher.group(3);
            Matcher codingMatcher =CODING_REGION_PARSER.matcher(codingRegionResponse);
            CodingRegion codingRegion = parseCodingRegion(codingMatcher, transcriptionRange.getStart());
            refGenes.add(new DefaultRefGene(createNewId(), geneName,referenceName,strand,transcriptionRange,codingRegion));
        }

        return refGenes;
        
    }
    private CodingRegion parseCodingRegion(Matcher codingMatcher, long transcriptionStartCoordinate) {
        if(codingMatcher.find()){
            
            Matcher exonMatcher =EXON_PARSER.matcher(codingMatcher.group(1));
            List<Exon> exons = new ArrayList<Exon>();
            List<Range> exonRanges = new ArrayList<Range>();
            boolean leftIncomplete = false;
            boolean rightIncomplete=false;
            while(exonMatcher.find()){
                leftIncomplete = leftIncomplete || exonMatcher.group(1).contains("<");
                rightIncomplete = rightIncomplete || exonMatcher.group(1).contains(">");
                
                final Range rangeAsOnesbased = Range.parseRange(exonMatcher.group(1));
                //ncbi is 1's based? lets make it space based                
                Range spacedBasedRange = RefUtil.convertOnesBasedToSpacedBased(rangeAsOnesbased);
                Frame frame = Frame.parseFrame((int)(spacedBasedRange.getStart()-transcriptionStartCoordinate)%3);
                
                exons.add(new DefaultExon(frame, spacedBasedRange));
                exonRanges.add(spacedBasedRange);
            }
            
            Range codingRange = Range.buildInclusiveRange(exonRanges);
            return new DefaultCodingRegion(codingRange,
                    getCodingRegionState(leftIncomplete),
                    getCodingRegionState(rightIncomplete), 
                    exons);
            
        }
        return null;
    }
    private Range parseTranscriptionRange(
            final String transcriptionRangeInput) {
        Matcher exonMatcher =EXON_PARSER.matcher(transcriptionRangeInput);
        List<Range> exonRanges = new ArrayList<Range>();
        boolean leftIncomplete = false;
        boolean rightIncomplete=false;
        while(exonMatcher.find()){
            leftIncomplete = leftIncomplete || exonMatcher.group(1).contains("<");
            rightIncomplete = rightIncomplete || exonMatcher.group(1).contains(">");
            
            final Range range_1sbased = Range.parseRange(exonMatcher.group(1));
            //ncbi is one's based? lets make it 0's based
            Range spacedBasedRange = RefUtil.convertOnesBasedToSpacedBased(range_1sbased);
            exonRanges.add(spacedBasedRange);
        }
        return Range.buildInclusiveRange(exonRanges);
    }
    
    private CodingRegionState getCodingRegionState(boolean incomplete){
        if(incomplete){
            return CodingRegionState.INCOMPLETE;
        }
        return CodingRegionState.COMPLETE;
    }
    
}
