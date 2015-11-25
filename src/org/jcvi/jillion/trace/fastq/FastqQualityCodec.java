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
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
/**
 * {@code FastqQualityCodec} is a can encode and decode
 * The different ways a FASTQ file can be encode
 * quality data.
 * @author dkatzel
 *
 */
public enum FastqQualityCodec {
	/**
	 * {@code ILLUMINA} supports Illumina 1.3 through
	 *  Illumina 1.7 encoded qualities.
	 *  Illumina 1.8+ switched to sanger encoding.
	 *  @see #SANGER
	 */
	ILLUMINA(64),
	/**
	 * {@code SANGER} supports Sanger encoded qualities.
	 * .
	 */
	SANGER(33),
	/**
	 * {@code SOLEXA} is a {@link FastqQualityCodec}
	 * that supports qualities not only encoded in
	 * FASTq solexa/Illumina format,
	 * but <strong>also</strong> the quality scores are
	 * in Solexa scale and not the Phred scale.
	 */
	SOLEXA(64){
		    @Override
		    protected PhredQuality decode(char encodedQuality) {
		        int solexaQuality =encodedQuality -64;
		        return SolexaUtil.convertSolexaQualityToPhredQuality(solexaQuality);
		    }

		    @Override
		    protected char encode(PhredQuality quality) {
		        int solexaQuality = SolexaUtil.convertPhredQualityToSolexaQuality(quality);
		        return (char)(solexaQuality +64);
		    }
		    
		    public QualitySequence decode(String fastqQualities) {
		    	QualitySequenceBuilder builder = new QualitySequenceBuilder(fastqQualities.length());
		      
		        for(int i=0; i<fastqQualities.length(); i++){        	;
		            builder.append(SolexaUtil.convertSolexaQualityToPhredQuality(fastqQualities.charAt(i) - 64));
		        }
		        return builder.build();
		    }
	}
	;

	/**
	 * Decode the given FASTQ quality encoded String
	 * into the equivalent {@link QualitySequence}.
	 * @param fastqQualities the encoded qualities.
	 * 
	 * @return a {@link QualitySequence}
	 * the decoded FASTQ quality values.
	 */
    public QualitySequence decode(String fastqQualities) {
    	return decode(fastqQualities, false);
    }
    /**
     * Decode the given FASTQ quality encoded String
     * into the equivalent {@link QualitySequence}.
     * @param fastqQualities the encoded qualities.
     * 
     * @param turnOffCompression {@code true} if the quality sequence to be built
     * should disable additional processing to compress or compact the sequence in as little memory 
     * as possible.  Turning off compression may have improve speed but take up more memory.
     * 
     * @return a {@link QualitySequence}
     * the decoded FASTQ quality values.
     */
    public QualitySequence decode(String fastqQualities, boolean turnOffCompression) {
    	byte[] buffer = new byte[fastqQualities.length()];
      
        for(int i=0; i<fastqQualities.length(); i++){
            buffer[i] =(byte)(fastqQualities.charAt(i) - offset);
        }
        return new QualitySequenceBuilder(buffer)
        		.turnOffDataCompression(turnOffCompression)
        		.build();
    }

    private final int offset;
    
    FastqQualityCodec(int offset){
    	this.offset = offset;
    }
    
    /**
     * Get the encoding offset used to encode
     * the quality values. For example, for {@link #SANGER},
     * this will return 33, for {@link #ILLUMINA}, it will
     * return 64.
     * @return the encoding offset as an int.
     */
    public int getOffset() {
		return offset;
	}
    
    protected PhredQuality decode(char encodedQuality) {
        return PhredQuality.valueOf(encodedQuality -offset);
    }

   
    protected char encode(PhredQuality quality) {
        return (char)(quality.getQualityScore()+offset);
    }


	/**
     * Encode the given qualities into 
     * a FASTQ quality encoded String.
     * @param qualities the qualities to encode
     * @return a String representing these
     * quality values in the desired String encoding.
     */
    public String encode(Sequence<PhredQuality> qualities) {
        StringBuilder builder= new StringBuilder();
        for(PhredQuality quality : qualities){
            builder.append(encode(quality));
        }
        return builder.toString();
    }
    
    
    
}
