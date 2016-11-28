/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.experimental.align.blast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * {@code XmlFileBlastParser} is a {@link BlastParser}
 * implementation that can parse XML formatted 
 * Blast output (-m 7).  This class should work
 * on both nucleotide and protein alignments.
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
		spf.setValidating(false);
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
				if (inputStream == null) {				
					parser.parse(file, new SaxBlastParser(visitor));
				}else{
					parser.parse(inputStream, new SaxBlastParser(visitor));
				}
			} catch (SAXException e) {
				throw new IOException("error parsing xml blast output",e);
			}
		}
		
	}



    
   
    
    private static class SaxBlastParser extends DefaultHandler{

    	private static final String HIT = "Hit";
        private static final String HSP = "Hsp";
        private static final String BIT_SCORE = "Hsp_bit-score";
        private static final String E_VALUE = "Hsp_evalue";
        private static final String HSP_SCORE = "Hsp_score";
        private static final String ALIGN_LENGTH = "Hsp_align-len";
        private static final String QUERY_FROM = "Hsp_query-from";
        private static final String QUERY_TO = "Hsp_query-to";
        private static final String HIT_FROM = "Hsp_hit-from";
        private static final String HIT_TO = "Hsp_hit-to";
        private static final String IDENTICAL_MATCHES = "Hsp_identity";
        private static final String POSITIVE_MATCHES = "Hsp_positive";
        private static final String NUM_GAPS = "Hsp_gaps";
        private static final String HIT_FRAME = "Hsp_hit-frame";
        
        
        
        private static final String SUBJECT_LENGTH = "Hit_len";
        private static final String MIDLINE = "Hsp_midline";
        
        
        private static final String QUERY_SEQUENCE = "Hsp_qseq";
        private static final String SUBJECT_SEQUENCE = "Hsp_hseq";
        private static final String SUBJECT_DEF = "Hit_def";
         
        private static final Pattern DEFLINE_PATTERN = Pattern.compile("^\\s*(\\S+)\\s*(.*)$");
        
        private static final String LEGACY_QUERY_ID = "BlastOutput_query-def";
        
        private static final String PROGRAM_NAME = "BlastOutput_program";
        private static final String PROGRAM_VERSION = "BlastOutput_version";
        private static final String QUERY_LENGTH = "BlastOutput_query-len";
        private static final String BLAST_DB = "BlastOutput_db";
        
        private static final String BLAST_ITERATIONS = "BlastOutput_iterations";
        
        private static final String ITERATION_QUERY_ID = "Iteration_query-def";
        private static final String ITERATION_QUERY_LENGTH = "Iteration_query-len";
        
        private static final String HIT_ACCESSION = "Hit_accession";
        
        private HspBuilder<?,?,?> hspBuilder;
        private final BlastVisitor visitor;
        private String tempVal=null;
        private StringBuilder tempBuilder =null;
        private Integer queryStart, queryEnd, subjectStart,subjectEnd, queryLength, subjectLength, numMatches;
        private String queryId, subjectId;
        private String programName, programVersion, blastDb, subjectDeflineComment;
        
        int misMatches=0,numberOfGapOpenings=0;
        Sequence<?> querySequence, subjectSequence;
        private boolean isNucleotide=false;
        
        private boolean inHspBlock=false;
        private BlastHitImpl.Builder hitBuilder;
        
        SaxBlastParser(BlastVisitor visitor){
            this.visitor = visitor;
        }
        
        
        
        @Override
		public InputSource resolveEntity(String publicId, String systemId)
				throws IOException, SAXException {
			//for some stupid reason, SAXParser#parse()
        	//ignores our dtd validation settings
        	//
        	//so we have to override this method
        	//to do a NullObject dtd resolution and check
			return new InputSource(new StringReader(""));
		}
        
        /**
        * {@inheritDoc}
        */
        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {
        	if(BLAST_ITERATIONS.equals(qName)){
        		reportBlastHeaderInfo();
        	}else if(HSP.equals(qName) && !inHspBlock){
        		
        		inHspBlock =true;
        		hitBuilder = new BlastHitImpl.Builder(queryId, subjectId);
        	
        		hitBuilder.setQueryLength(queryLength);
        		hitBuilder.setSubjectDefline(subjectDeflineComment);
        		
        		hitBuilder.setBlastDbName(blastDb);
        		hitBuilder.setBlastProgramName(programName);
        	}
        	tempBuilder = new StringBuilder();           
            
        }
        private void reportBlastHeaderInfo() {
			visitor.visitInfo(programName, programVersion, blastDb, queryId);
			
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
        @SuppressWarnings("unchecked")
		@Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
        	tempVal = tempBuilder.toString();
        	
        	if(inHspBlock){
        		if(HSP.equals(qName)){
                    DirectedRange queryRange = DirectedRange.parse(queryStart, queryEnd, CoordinateSystem.RESIDUE_BASED);
    				hspBuilder.queryRange(queryRange);
                    hspBuilder.subjectRange(DirectedRange.parse(subjectStart, subjectEnd, CoordinateSystem.RESIDUE_BASED));
                    if(subjectLength !=null){
                    	hspBuilder.subjectLength(subjectLength);
                    }
                    if(numMatches ==null){
    	                double percentIdentity = (tempVal.length()-misMatches)/(double)tempVal.length();
    	                hspBuilder.percentIdentity(percentIdentity);
    	                hspBuilder.numMismatches(misMatches);	                
    	               
                    }else{
                    	long length = queryRange.asRange().getLength();
                    	int numMismatches = (int)(length - numMatches);
                    	hspBuilder.percentIdentity(numMatches/(double)length);
                    	 hspBuilder.numMismatches(numMismatches);
                    }
                    hspBuilder.numGapOpenings(numberOfGapOpenings);
                    if(isNucleotide){
                    	 ((HspBuilder<Nucleotide,NucleotideSequence, NucleotideSequenceBuilder>)hspBuilder)
                    	 				.gappedAlignments((NucleotideSequence)querySequence, (NucleotideSequence)subjectSequence);
                    }else{
                    	 ((HspBuilder<AminoAcid,ProteinSequence, ProteinSequenceBuilder>)hspBuilder)
     	 								.gappedAlignments((ProteinSequence)querySequence, (ProteinSequence)subjectSequence);

                    }
                   
                    if(queryLength !=null){
                    	hspBuilder.queryLength(queryLength);
                    }
                    hitBuilder.addHsp(hspBuilder.build());
                    
                   
                    queryStart=null;
                    queryEnd=null;
                    subjectStart=null;
                    subjectEnd=null;
                    subjectLength =null;
                    
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
                }else if(IDENTICAL_MATCHES.equals(qName)){
    		        hspBuilder.numIdenticalMatches(Integer.parseInt(tempVal));
    		    }else if(POSITIVE_MATCHES.equals(qName)){
    		    	numMatches = Integer.parseInt(tempVal);
    				hspBuilder.numPositiveMatches(numMatches);
    		    }else if(HIT_FRAME.equals(qName)){
    		        hspBuilder.hitFrame(Integer.valueOf(tempVal));
    		    }else if(HIT_TO.equals(qName)){
                    subjectEnd = Integer.parseInt(tempVal);
                }else if(HSP_SCORE.equals(qName)){
                    hspBuilder.hspScore(Float.parseFloat(tempVal));
                }else if(QUERY_SEQUENCE.equals(qName)){
                    if(isNucleotide){
                    	querySequence = new NucleotideSequenceBuilder(tempVal).build();
                    }else{
                    	querySequence = new ProteinSequenceBuilder(tempVal).build();
                    }
                }else if(SUBJECT_SEQUENCE.endsWith(qName)){
                    if(isNucleotide){
                    	subjectSequence = new NucleotideSequenceBuilder(tempVal).build();
                    }else{
                    	subjectSequence = new ProteinSequenceBuilder(tempVal).build();
                    }
                }else if(NUM_GAPS.equals(qName)){
                	numberOfGapOpenings = Integer.parseInt(tempVal);
                }else if(MIDLINE.equals(qName)){
                    int totalMisMatches= parseNumberOfMismatches(tempVal);
                    misMatches = totalMisMatches- numberOfGapOpenings;                
                }else if (HIT.equals(qName)){
                	inHspBlock=false;
                	visitor.visitHit(hitBuilder.build());
                }
        	}else{
        		if(LEGACY_QUERY_ID.equals(qName)){                
                    queryId = tempVal;
                 }else if(ITERATION_QUERY_ID.equals(qName)){
                 	queryId = tempVal;
                 }else if(ITERATION_QUERY_LENGTH.equals(qName)){
                 	queryLength = Integer.parseInt(tempVal);
                 }else if(SUBJECT_LENGTH.equals(qName)){
                 	subjectLength = Integer.parseInt(tempVal);
                 }
                 else if(SUBJECT_DEF.equals(qName)){
                 	if(isNucleotide){
                 		hspBuilder = HspBuilder.forBlastN().query(queryId);
                 	}else{
                 		hspBuilder = HspBuilder.forBlastP().query(queryId);
                 	}
                 	
                 	//defline could have comments
                 	//only include up to first whitespace
                 	Matcher matcher = DEFLINE_PATTERN.matcher(tempVal);
                 	if(matcher.find()){            		
                 		if("No definition line found".equals(tempVal)){
                 				hspBuilder.subject(tempVal);
	                         
	                         subjectId = tempVal; 
                 		}else{
	                         hspBuilder.subject(matcher.group(1));
	                         
	                         subjectId = matcher.group(1); 
                 		}
                         subjectDeflineComment = tempVal.trim();
                         hspBuilder.subjectDef(subjectDeflineComment);
                 	}else{
                 		//doesn't match defline pattern
                 		//use whole string?
                 		hspBuilder.subject(tempVal);
                 		subjectId = tempVal.trim();
                 		hspBuilder.subject(subjectId);
                 	}
             }else if(HIT_ACCESSION.equals(qName) && "No definition line found".equals(subjectId)){
            	 //VHTNGS-1131 use accession hit as subject if no defline found
            	 subjectId = tempVal.trim();
            	 hspBuilder.subject(subjectId);
        	}else if(PROGRAM_NAME.equals(qName)){
            	isNucleotide = "blastn".equalsIgnoreCase(tempVal);
            	programName = tempVal;
            }else if(PROGRAM_VERSION.equals(qName)){
            	programVersion = tempVal;
            }else if (QUERY_LENGTH.equals(qName)){
            	this.queryLength = Integer.parseInt(tempVal);
            }else if (BLAST_DB.equals(qName)){
            	this.blastDb = tempVal;
            }
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
    }
}
