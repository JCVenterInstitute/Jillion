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
package org.jcvi.jillion_experimental.align.blast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * -m 7
 * 
 * @author dkatzel
 *
 *
 */
public final class XmlFileBlastParser implements BlastParser{

	private final SAXParser parser;
	private OpenAwareInputStream inputStream;
	private File file;
	
	
	public static BlastParser create(File xml) throws IOException{
		 SAXParser parser = createSaxParser();
		 return new XmlFileBlastParser(parser, xml);
	}
	
	public static BlastParser create(InputStream xml) throws IOException{
		 SAXParser parser = createSaxParser();
		 return new XmlFileBlastParser(parser, xml);
	}

	private static SAXParser createSaxParser() throws IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		 SAXParser parser;
		try {
			parser = spf.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new IOException("error creating SAX parser", e);
		} catch (SAXException e) {
			throw new IOException("error creating SAX parser", e);
		}
		return parser;
	}
	
    private XmlFileBlastParser(SAXParser parser, InputStream inputStream){
    	this.parser = parser;
    	this.inputStream = new OpenAwareInputStream(inputStream);
    }
    private XmlFileBlastParser(SAXParser parser, File file){
    	this.parser = parser;
    	this.file = file;
    }
    
    
    
    @Override
	public boolean canParse() {
		if(inputStream !=null && !inputStream.isOpen()){
			return false;
		}
		return true;
	}



	@Override
	public void parse(BlastVisitor visitor) throws IOException {
		if(canParse()){
			try {
				if (inputStream != null) {
					parser.parse(inputStream, new SaxBlastParser(visitor));
				}else{
					parser.parse(file, new SaxBlastParser(visitor));
				}
			} catch (SAXException e) {
				throw new IOException("error parsing xml blast output",e);
			}
		}
		
	}



    
   
    
    private static class SaxBlastParser extends DefaultHandler{
    	
    	//TODO currently only support nucleotide
    	//the XML has a tag BlastOutput_program with a value of blastn etc
    	//maybe use different subclasses to handle nucleotide or amino acid
    	//(or mix?)
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
        private static final String SUBJECT_DEF = "Hit_def";
        
        private static final Pattern GAP_OPENING_PATTERN = Pattern.compile("[-]+");
        
        private static final Pattern DEFLINE_PATTERN = Pattern.compile("^\\s*(\\S+)\\s*.*$");
        
        private static final String PROGRAM_NAME = "BlastOutput_program";
        
        
        private HspBuilder<?,?> hspBuilder;
        private final BlastVisitor visitor;
        private String tempVal=null;
        private StringBuilder tempBuilder =null;
        private Integer queryStart, queryEnd, subjectStart,subjectEnd;
        private String queryId;
        int misMatches=0,numberOfGapOpenings=0;
        Sequence<?> querySequence, subjectSequence;
        private boolean isNucleotide=false;
        
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

        @Override
		public void endDocument() throws SAXException {
			visitor.visitEnd();
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
            }else if(SUBJECT_DEF.equals(qName)){
            	if(isNucleotide){
            		hspBuilder = HspBuilder.forBlastN().query(queryId);
            	}else{
            		hspBuilder = HspBuilder.forBlastP().query(queryId);
            	}
            	
            	//defline could have comments
            	//only include up to first whitespace
            	Matcher matcher = DEFLINE_PATTERN.matcher(tempVal);
            	if(matcher.find()){            		
                    hspBuilder.subject(matcher.group(1));
            	}else{
            		//doesn't match defline pattern
            		//use whole string?
            		hspBuilder.subject(tempVal);
            	}
            	
            	
            }else if(END_HIT.equals(qName)){
                hspBuilder.queryRange(DirectedRange.parse(queryStart, queryEnd, CoordinateSystem.RESIDUE_BASED));
                hspBuilder.subjectRange(DirectedRange.parse(subjectStart, subjectEnd, CoordinateSystem.RESIDUE_BASED));
                
                double percentIdentity = (tempVal.length()-misMatches)/(double)tempVal.length();
                hspBuilder.percentIdentity(percentIdentity);
                hspBuilder.numMismatches(misMatches);
                hspBuilder.numGapOpenings(numberOfGapOpenings);
                if(isNucleotide){
                	 ((HspBuilder<Nucleotide,NucleotideSequence>)hspBuilder)
                	 				.gappedAlignments((NucleotideSequence)querySequence, (NucleotideSequence)subjectSequence);
                }else{
                	 ((HspBuilder<AminoAcid,ProteinSequence>)hspBuilder)
 	 								.gappedAlignments((ProteinSequence)querySequence, (ProteinSequence)subjectSequence);

                }
               
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
                if(isNucleotide){
                	querySequence = new NucleotideSequenceBuilder(tempVal).build();
                }else{
                	querySequence = new ProteinSequenceBuilder(tempVal).build();
                }
            }else if(SUBJECT_SEQUENCE.endsWith(qName)){
                numberOfGapOpenings +=parseNumberOfGapOpenings(tempVal);
                if(isNucleotide){
                	subjectSequence = new NucleotideSequenceBuilder(tempVal).build();
                }else{
                	subjectSequence = new ProteinSequenceBuilder(tempVal).build();
                }
            }else if(MIDLINE.equals(qName)){
                int totalMisMatches= parseNumberOfMismatches(tempVal);
                misMatches = totalMisMatches- numberOfGapOpenings;                
            }else if(PROGRAM_NAME.equals(qName)){
            	isNucleotide = "blastn".equalsIgnoreCase(tempVal);
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
