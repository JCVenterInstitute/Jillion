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
package org.jcvi.jillion.internal.trace.fastq;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;

public class ParsedFastqRecord implements FastqRecord {

    private final String id;
    private final String encodedQualities;
    private final FastqQualityCodec qualityCodec;
    
    private final String nucleotideSequenceString;
    private NucleotideSequence nucleotideSequence;
    private QualitySequence qualitySequence;
    
    private final boolean turnOffCompression;
    
    
    public ParsedFastqRecord(String id, String nucleotideSequence,
            String encodedQualities, FastqQualityCodec qualityCodec,
            boolean turnOffCompression) {
        this.id = id;
        this.nucleotideSequenceString = nucleotideSequence;
        this.encodedQualities = encodedQualities;
        this.qualityCodec = qualityCodec;
        this.turnOffCompression = turnOffCompression;
    }

    @Override
	public long getLength() {
    	//get the string length which will be faster
    	//than parsing the length from most NucleotideSequence implementations.
		return nucleotideSequenceString.length();
	}

	@Override
    public String getId() {
        return id;
    }

    @Override
    public NucleotideSequence getNucleotideSequence() {
    	if(nucleotideSequence==null){
    		nucleotideSequence = new NucleotideSequenceBuilder(nucleotideSequenceString)
    									.turnOffDataCompression(turnOffCompression)
    									.build();
    	}
        return nucleotideSequence;
    }

    @Override
    public QualitySequence getQualitySequence() {
       if(qualitySequence ==null){
           qualitySequence = qualityCodec.decode(encodedQualities, turnOffCompression);
       }
       return qualitySequence;
    }

    @Override
    public String getComment() {
        //default to null
        return null;
    }


    public String getEncodedQualities() {
        return encodedQualities;
    }

    public FastqQualityCodec getQualityCodec() {
        return qualityCodec;
    }
    
    
    
    @Override
    public double getAvgQuality() throws ArithmeticException {
        
        //this implementation gets the 
        //average ASCII value
        //and then computes the equivalent
        //value based on the quality codec offset.
        //this should be much faster since we 
        //still have the encoded quality string.
        long total =0;
        char[] chars = encodedQualities.toCharArray();
        if(chars.length ==0){
            throw new ArithmeticException("length of fastq record is 0");
        }
        for(int i=0; i< chars.length; i++){
            total+= chars[i];
        }
        double avg = total/chars.length;
        
        return avg - qualityCodec.getOffset();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;         
        result = prime * result + id.hashCode();
        result = prime * result
                + getNucleotideSequence().hashCode();
        result = prime * result
                + getQualitySequence().hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FastqRecord)) {
            return false;
        }
        FastqRecord other = (FastqRecord) obj;         
        if (!id.equals(other.getId())) {
            return false;
        }
        if (!getNucleotideSequence().equals(other.getNucleotideSequence())) {
            return false;
        }
        if (!getQualitySequence().equals(other.getQualitySequence())) {
            return false;
        }
        return true;
    }

	public String getNucleotideString() {
		return nucleotideSequenceString;
	}
	
	
	@Override
        public String toString() {
                return "UncommentedFastqRecord [id=" + id + ", nucleotides="
                                + nucleotideSequenceString + ", qualities=" + getQualitySequence() + "]";
        }

}
