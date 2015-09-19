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
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.core.qual.QualitySequence;

/**
 * {@code FastqUtil} is a utility class for working with 
 * FASTQ data.
 * @author dkatzel
 *
 *
 */
public final class FastqUtil {


    /**
     * This is the {@link Pattern} to parse
     * the sequence record defline of a FASTQ record.
     * Group 1 will be the read id
     * Group 3 will be the optional comment if there is one,
     * or null if there isn't a comment.
     */
    public static final Pattern SEQ_DEFLINE_PATTERN = Pattern.compile("^@(\\S+)(\\s+)?(.+$)?");
    

	private FastqUtil(){
		//can not instantiate
	}
    
    /**
     * Attempts to guess the {@link FastqQualityCodec} used to encode
     * the qualities in the given fastq file. 
     * This method works by analyzing the encoded quality value ranges for each
     * read getting inspected.  Since all the different quality encodings have
     * overlapping values the {@link FastqQualityCodec} used for the entire file
     * can not always be determined from just analyzing a few reads.
     * Therefore, this method might have to parse
     * ALL records in the fastq file to guarantee that the
     * correct {@link FastqQualityCodec} is returned.
     * @param fastqFile a fastq File, must exist and can not be null.
     * @return an instance of {@link FastqQualityCodec} which is the one
     * that is most likely able to decode the quality values for all the reads
     * in the file (will never be null).
     * @throws IOException if there is a problem reading the file.
     * @throws NullPointerException if fastqFile is null.
     * @throws IllegalArgumentException if the given quality string is empty
     * or if it contains any characters out of range of any known
     * quality encoding formats.
     * @throws IllegalStateException if the fastq file does not contain 
     * any records.
     */
    public static FastqQualityCodec guessQualityCodecUsed(File fastqFile) throws IOException{
    	return guessQualityCodecUsed(fastqFile, Integer.MAX_VALUE);
    }
    
    /**
     * Attempts to guess the {@link FastqQualityCodec} used to encode
     * the qualities in the given fastq file. 
     * This method works by analyzing the encoded quality value ranges for each
     * read getting inspected.  Since all the different quality encodings have
     * overlapping values the {@link FastqQualityCodec} used for the entire file
     * can not always be determined from just analyzing a few reads.
     * Therefore, this method must
     * parse many (probably hundreds) of reads in the fastq file in order to get
     * a large enough sampling size to confidently return a {@link FastqQualityCodec}.
     * This method will keep looking at reads in the fastq file until
     * either the file ends, or it has looked at {@code numberOfReadsToInspect}.
     * @param fastqFile a fastq File, must exist and can not be null.
     * @param numberOfReadsToInspect the number of reads in the file to analyze.
     * 
     * @return an instance of {@link FastqQualityCodec} which is the one
     * that is most likely able to decode the quality values for all the reads
     * in the file (will never be null).
     * @throws IOException if there is a problem reading the file.
     * @throws NullPointerException if fastqFile is null.
     * @throws IllegalArgumentException if the given quality string is empty
     * or if it contains any characters out of range of any known
     * quality encoding formats.
     * @throws IllegalArgumentException if numberOfReadsToInspect is < 1.
     * @throws IllegalStateException if the fastq file does not contain 
     * any records.
     */
    public static FastqQualityCodec guessQualityCodecUsed(File fastqFile, int numberOfReadsToInspect) throws IOException{
    	if(numberOfReadsToInspect <1){
    		throw new IllegalArgumentException("number of reads to inspect must be >=1");
    	}
    	FastqQualityCodecDetectorVisitor detectorVisitor =new FastqQualityCodecDetectorVisitor(numberOfReadsToInspect);
    	FastqFileParser.create(InputStreamSupplier.forFile(fastqFile), false,true).parse(detectorVisitor);
    	return detectorVisitor.getDetectedCodec();
    }
    /**
     * Attempts to guess the {@link FastqQualityCodec} used to encode
     * the qualities in the given fastq file. 
     * This method works by analyzing the encoded quality value ranges for each
     * read getting inspected.  Since all the different quality encodings have
     * overlapping values the {@link FastqQualityCodec} used for the entire file
     * can not always be determined from just analyzing a few reads.
     * Therefore, this method might have to parse
     * ALL records in the fastq file to guarantee that the
     * correct {@link FastqQualityCodec} is returned.
     * @param parser a {@link FastqParser} instance to parse; must exist and can not be null.
     * @return an instance of {@link FastqQualityCodec} which is the one
     * that is most likely able to decode the quality values for all the reads
     * in the file (will never be null).
     * @throws IOException if there is a problem reading the file.
     * @throws NullPointerException if fastqFile is null.
     * @throws IllegalArgumentException if the given quality string is empty
     * or if it contains any characters out of range of any known
     * quality encoding formats.
     * @throws IllegalStateException if the fastq file does not contain 
     * any records.
     */
    public static FastqQualityCodec guessQualityCodecUsed(FastqParser parser) throws IOException{
    	return guessQualityCodecUsed(parser, Integer.MAX_VALUE);
    }
    
    /**
     * Attempts to guess the {@link FastqQualityCodec} used to encode
     * the qualities in the given fastq file. 
     * This method works by analyzing the encoded quality value ranges for each
     * read getting inspected.  Since all the different quality encodings have
     * overlapping values the {@link FastqQualityCodec} used for the entire file
     * can not always be determined from just analyzing a few reads.
     * Therefore, this method must
     * parse many (probably hundreds) of reads in the fastq file in order to get
     * a large enough sampling size to confidently return a {@link FastqQualityCodec}.
     * This method will keep looking at reads in the fastq file until
     * either the file ends, or it has looked at {@code numberOfReadsToInspect}.
     * @param parser a {@link FastqParser} instance to parse; must exist and can not be null.
     * @param numberOfReadsToInspect the number of reads in the file to analyze.
     * 
     * @return an instance of {@link FastqQualityCodec} which is the one
     * that is most likely able to decode the quality values for all the reads
     * in the file (will never be null).
     * @throws IOException if there is a problem reading the file.
     * @throws NullPointerException if fastqFile is null.
     * @throws IllegalArgumentException if the given quality string is empty
     * or if it contains any characters out of range of any known
     * quality encoding formats.
     * @throws IllegalArgumentException if numberOfReadsToInspect is < 1.
     * @throws IllegalStateException if the fastq file does not contain 
     * any records.
     */
    public static FastqQualityCodec guessQualityCodecUsed(FastqParser parser, int numberOfReadsToInspect) throws IOException{
    	if(numberOfReadsToInspect <1){
    		throw new IllegalArgumentException("number of reads to inspect must be >=1");
    	}
    	FastqQualityCodecDetectorVisitor detectorVisitor =new FastqQualityCodecDetectorVisitor(numberOfReadsToInspect);
    	parser.parse(detectorVisitor);
    	return detectorVisitor.getDetectedCodec();
    }
    /**
     * Attempts to guess the {@link FastqQualityCodec} used to encode
     * the given qualities.
     * @param encodedQualities a String of fastq encoded qualities
     * @return an instance of {@link FastqQualityCodec} which could have been 
     * used to encode the given qualities; or {@code null} if encodedQualities
     * is empty.
     * @throws NullPointerException if the given qualities are null.
     * @throws IllegalArgumentException if the given quality string
     * contains any characters out of range of any known
     * quality encoding formats.
     */
    static FastqQualityCodec guessQualityCodecUsed(String encodedQualities){
    	if(encodedQualities.isEmpty()){
    		return null;
    	}
    	//sanger uses 33 as an offset so any ascii values around there will 
    	//automatically be sanger
    	int sangerOffset = FastqQualityCodec.SANGER.getOffset();
    	//solexa and illumina encoding have offsets of 64 so they look very similar
    	//except solexa uses a different log scale and can actually have scores as low
    	//as -5 (ascii value 59) so any values from ascii 59-63 mean solexa
    	boolean hasSolexaOnlyValues=false;
    	int maxQuality=Integer.MIN_VALUE;
    	int minQuality = Integer.MAX_VALUE;
    	
    	//convert to char[] as optimization
    	//so we don't have to do 2x the boundary checks
    	//in String.charAt(i) 
    	//which does extra boundary checking to make
    	//sure i is in range before accessing
    	//the internal array.
    	char[] chars = encodedQualities.toCharArray();
    	
    	for(int i=0; i<chars.length; i++){
    		int asciiValue = chars[i];
    		if(asciiValue <sangerOffset){
    			throw new IllegalArgumentException(
    					String.format(
    							"invalid encoded qualities has out of range ascii value %d : '%s'", 
    							asciiValue,
    							encodedQualities));
    		}
    		if(asciiValue <59){
    			return FastqQualityCodec.SANGER;
    		}
    		if(asciiValue < 64){
    			hasSolexaOnlyValues=true;
    		}
    		if(asciiValue > maxQuality){
    			maxQuality = asciiValue;
    		}
    		if(asciiValue < minQuality){
    			minQuality = asciiValue;
    		}
    	}
    	//if we get this far, we don't have 
    	//any QVs ascii values < 59
    	//we could still be sanger
    	//and only have high quality qvs
    	//(possible but unlikely outside of testing)
    	//or only low super low illumina/solexa qvs
    	//most qvs are between 1-40 so check to see if we have
    	//any values beyond that.
    	
		if(maxQuality <  sangerOffset + 41){
			//we have QVs up to sanger 40
			//or illumina qv 9
			//assume sanger
			return FastqQualityCodec.SANGER;
		}
    	
    	if(hasSolexaOnlyValues){
    		return FastqQualityCodec.SOLEXA;
    	}
    	//if we get here then we only saw encoded values from ascii 64 +
    	//assume illumina, solexa scaling is so close at good quality anyway
    	//that I don't think it matters.
    	
    	return FastqQualityCodec.ILLUMINA;
    }
    /**
     * Inner class that visits the first X reads of a fastq file
     * and notes the {@link FastqQualityCodec} that can be used.
     * After a sample size of X reads is analyzed, the most frequent
     * codec is returned.
     * @author dkatzel
     *
     */
    private static final class FastqQualityCodecDetectorVisitor implements FastqVisitor{
    	private int numberOfRecordsVisited=0;
    	private final int maxNumberOfRecordsToVisit;
		
    	FastqQualityCodecDetectorRecordVisitor recordVisitor = new FastqQualityCodecDetectorRecordVisitor();
    	
		public FastqQualityCodecDetectorVisitor(int maxNumberOfRecordsToVisit) {
			this.maxNumberOfRecordsToVisit = maxNumberOfRecordsToVisit;
		}
		public FastqQualityCodec getDetectedCodec() throws MixedFastqEncodings {
			return recordVisitor.getMostFrequentCodec();	
		}
		@Override
		public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
				String id, String optionalComment) {
			if(numberOfRecordsVisited<maxNumberOfRecordsToVisit){
				numberOfRecordsVisited++;
				return recordVisitor;
			}
			callback.haltParsing();
			return null;
		}
		/**
		 * This method will be the most frequently
		 * one called since most fastq files
		 * are huge so {@link org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback#haltParsing()} will get called
		 * 
		 */
		@Override
		public void halted() {
			//no-op
		}
		/**
		 * It is possible that the fastq
		 * file is so small that 
		 * {@link org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback#haltParsing()}
		 * will not get called.
		 * 
		 */
		@Override
		public void visitEnd() {			
			//no-op
		}
    	
    }
    
    private static final class FastqQualityCodecDetectorRecordVisitor implements FastqRecordVisitor{
    	private long numSanger=0;
    	private long numIllumina=0;
    	private long numPossiblySolexa=0;
    	private long numEither=0;
    	
    	private int overAllMin= Integer.MAX_VALUE;
    	private int overallMax = Integer.MIN_VALUE;
		@Override
		public void visitNucleotides(String nucleotides) {
			//no-op			
		}

		@Override
		public void visitEncodedQualities(String encodedQualities) {
		
			
	    	boolean hasSolexaOnlyValues=false;
	    	int maxQuality=Integer.MIN_VALUE;

	    	//convert to char[] as optimization
	    	//so we don't have to do 2x the boundary checks
	    	//in String.charAt(i) 
	    	//which does extra boundary checking to make
	    	//sure i is in range before accessing
	    	//the internal array.
	    	char[] chars = encodedQualities.toCharArray();
	    	for(int i=0; i<chars.length; i++){
	    		int asciiValue = chars[i];
	    		if(asciiValue <33){
	    			throw new IllegalArgumentException(
	    					String.format(
	    							"invalid encoded qualities has out of range ascii value %d : '%s'", 
	    							asciiValue,
	    							encodedQualities));
	    		}
	    		if(asciiValue > maxQuality){
	    		    maxQuality = asciiValue;
	    		}
    	    		if(asciiValue > overallMax){
    	    		    overallMax = asciiValue;
                        }
                        if(asciiValue < overAllMin){
                            overAllMin = asciiValue;
                        }
	    		//sanger uses 33 as an offset so any ascii values around there will 
		    	//automatically be sanger
	    		if(asciiValue <59){
	    			numSanger++;
	    			return;
	    		}
	    		//solexa and illumina encoding have offsets of 64 so they look very similar
		    	//except solexa uses a different log scale and can actually have scores as low
		    	//as -5 (ascii value 59) so any values from ascii 59-63 mean solexa
	    		if(asciiValue < 64){
	    			hasSolexaOnlyValues=true;
	    		}
	    		
	    	}
	    	//a quality more than this is highly improbable
	    	//for sanger encoding so assume illumina or solexa
	    	if(maxQuality > 80){
	    		if(hasSolexaOnlyValues){
	    			numPossiblySolexa++;
	    		}else{
	    			numIllumina ++;
	    		}
	    	}else{
	    		//could not tell which encoding
	    		//this could be a high quality sanger 
	    		//or low quality illumina read
	    		numEither++;
	    	}
	    	
		}

		
		
		@Override
		public void visitEnd() {
			//no-op			
		}
    	
		@Override
		public void halted() {
			//no-op			
		}
		
		public FastqQualityCodec getMostFrequentCodec() throws MixedFastqEncodings{
			if(numSanger==0 && numPossiblySolexa==0 && numIllumina==0 && numEither ==0){
				throw new IllegalStateException("fastq file must not be empty");
			}
			if(numSanger>0 && numIllumina>0){
				//mix could be invalid upstream process
				//that mixed the encodings together.
				//This has happened where many 
				//fastq files are processed differently 
				//but then combined into one large fastq file
				throw new MixedFastqEncodings(numSanger, numPossiblySolexa, numIllumina, numEither);
			}		
			//if our file has anything that
			//is definitely sanger encoded then we 
			//must be sanger encoded.
			if(numSanger >0){
				return FastqQualityCodec.SANGER;
			}
			//we could be either high quality sanger
			//or low quality illumina.
			//we have 0 def sanger and 0 def illumina
			//AND
			//we have QVs up to sanger 40
			//or illumina qv 9:
			//assume sanger
			if(numIllumina==0 && overallMax <  FastqQualityCodec.SANGER.getOffset() + 41){
				return FastqQualityCodec.SANGER;				
			}
			
			//if we get this far than we use a 64 offset
			//but don't yet know if we are solexa or illumina
			if(numPossiblySolexa>0){
				return FastqQualityCodec.SOLEXA;
			}
			return FastqQualityCodec.ILLUMINA;
		}

		@Override
		public void visitQualities(QualitySequence qualities) {
			//no-op since we there is no encoding.
			
		}
    }
    
    private static class MixedFastqEncodings extends IOException{
		private static final long serialVersionUID = 1L;

		MixedFastqEncodings(long numSanger, long numSolexa, long numIllumina, long numEither){
    		super(String.format("many reads are encoded differently #sanger =%d #illuminia = %d #either = %d" , numSanger,numIllumina, numSolexa+numEither));
    	}
    }
   
}
