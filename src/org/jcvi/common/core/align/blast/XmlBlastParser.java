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

package org.jcvi.common.core.align.blast;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author dkatzel
 *
 *
 */
public final class XmlBlastParser {

    private XmlBlastParser(){}
    
    public static void parse(InputStream xml, BlastVisitor visitor){
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser parser =spf.newSAXParser();
            parser.parse(xml, new SaxBlastParser(visitor));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    public static void parse(File xml, BlastVisitor visitor){
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser parser =spf.newSAXParser();
            visitor.visitFile();
            parser.parse(xml, new SaxBlastParser(visitor));
            visitor.visitEndOfFile();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    private static class SaxBlastParser extends DefaultHandler{
        private static final String START_HIT = "Hit_id";
        private static final String END_HIT = "Hsp";
        private static final String BIT_SCORE = "Hsp_bit-score";
        private static final String E_VALUE = "Hsp_evalue";
        private static final String ALIGN_LENGTH = "Hsp_align-len";
        private static final String QUERY_FROM = "Hsp_query-from";
        private static final String QUERY_TO = "Hsp_query-to";
        private static final String HIT_FROM = "Hsp_hit-from";
        private static final String HIT_TO = "Hsp_hit-to";
        private static final String MIDLINE = "Hsp_midline";
        private static final String QUERY_ID = "BlastOutput_query-def";
        private static final String QUERY_SEQUENCE = "Hsp_qseq";
        private static final String SUBJECT_SEQUENCE = "Hsp_hseq";
        
        private static final Pattern GAP_OPENING_PATTERN = Pattern.compile("[-]+");
        private HspBuilder hspBuilder;
        private final BlastVisitor visitor;
        private String tempVal=null;
        private StringBuilder tempBuilder =null;
        private Integer queryStart, queryEnd, subjectStart,subjectEnd;
        private String queryId;
        int misMatches=0,numberOfGapOpenings=0;
        NucleotideSequence querySequence, subjectSequence;
        SaxBlastParser(BlastVisitor visitor){
            this.visitor = visitor;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
        	tempBuilder = new StringBuilder();           
            
        }
        public void characters(char[] ch, int start, int length) throws SAXException {
        	tempBuilder.append( new String(ch,start,length));
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
        	tempVal = tempBuilder.toString();
            if(QUERY_ID.equals(qName)){                
               queryId = tempVal;
            }
            if(START_HIT.equals(qName)){
            	hspBuilder = HspBuilder.create(queryId);
                hspBuilder.subject(tempVal);
            }else if(END_HIT.equals(qName)){
                hspBuilder.queryRange(DirectedRange.parse(queryStart, queryEnd, CoordinateSystem.RESIDUE_BASED));
                hspBuilder.subjectRange(DirectedRange.parse(subjectStart, subjectEnd, CoordinateSystem.RESIDUE_BASED));
                
                double percentIdentity = (tempVal.length()-misMatches)/(double)tempVal.length();
                hspBuilder.percentIdentity(percentIdentity);
                hspBuilder.numMismatches(misMatches);
                hspBuilder.numGapOpenings(numberOfGapOpenings);
                hspBuilder.gappedAlignments(querySequence, subjectSequence);
                visitor.visitHsp(hspBuilder.build());
               
                queryStart=null;
                queryEnd=null;
                subjectStart=null;
                subjectStart=null;
                numberOfGapOpenings=0;
                misMatches=0;
                querySequence=null;
                subjectSequence=null;
            }else if(BIT_SCORE.equals(qName)){
                hspBuilder.bitScore(new BigDecimal(tempVal));
            }else if(E_VALUE.equals(qName)){
                hspBuilder.eValue(new BigDecimal(tempVal));
            }else if(ALIGN_LENGTH.equals(qName)){
                hspBuilder.alignmentLength(Integer.parseInt(tempVal));
            }else if(QUERY_FROM.equals(qName)){
               queryStart = Integer.parseInt(tempVal);
            }else if(QUERY_TO.equals(qName)){
               queryEnd = Integer.parseInt(tempVal);
            }else if(HIT_FROM.equals(qName)){
               subjectStart = Integer.parseInt(tempVal);
            }else if(HIT_TO.equals(qName)){
                subjectEnd = Integer.parseInt(tempVal);
            }else if(QUERY_SEQUENCE.equals(qName)){
                numberOfGapOpenings +=parseNumberOfGapOpenings(tempVal);
                querySequence = new NucleotideSequenceBuilder(tempVal).build();
            }else if(SUBJECT_SEQUENCE.endsWith(qName)){
                numberOfGapOpenings +=parseNumberOfGapOpenings(tempVal);
                subjectSequence = new NucleotideSequenceBuilder(tempVal).build();
            }else if(MIDLINE.equals(qName)){
                int totalMisMatches= parseNumberOfMismatches(tempVal);
                misMatches = totalMisMatches- numberOfGapOpenings;
                
            }
        }
        /**
         * @param tempVal2
         * @return
         */
        private int parseNumberOfMismatches(String midline) {
           
            int misMatches=0;
            for(int i=0; i<midline.length(); i++){
                if(midline.charAt(i) != '|'){
                    misMatches++;
                }
            }
            return misMatches;
        }
        private int parseNumberOfGapOpenings(String basecalls){
            Matcher matcher = GAP_OPENING_PATTERN.matcher(basecalls);
            int openings =0;
            while(matcher.find()){
                openings++;
            }
            return openings;
        }
    }
}
