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

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordBuilder;

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
           qualitySequence = new ParsedQualitySequence(qualityCodec, encodedQualities);
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
    public FastqRecordBuilder toBuilder() {
        return new ParsedFastqRecordBuilder(this);
    }

    @Override
    public OptionalDouble getAvgQuality() throws ArithmeticException {
        
        //this implementation gets the 
        //average ASCII value
        //and then computes the equivalent
        //value based on the quality codec offset.
        //this should be much faster since we 
        //still have the encoded quality string.
        long total =0;
        char[] chars = encodedQualities.toCharArray();
        if(chars.length ==0){
            return OptionalDouble.empty();
        }
        for(int i=0; i< chars.length; i++){
            total+= chars[i];
        }
        double avg = total/chars.length;
        
        return OptionalDouble.of(avg - qualityCodec.getOffset());
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

	
	private static final class ParsedFastqRecordBuilder implements FastqRecordBuilder{
	    private String id;
	    private String encodedQualities;
	    private final FastqQualityCodec qualityCodec;
	    
	    private String nucleotideSequenceString;
	    private final boolean turnOffCompression;
	    
	    private String comment;
	    
	    private boolean lengthsModified=false;
	    
        public ParsedFastqRecordBuilder(FastqRecord record) {
            ParsedFastqRecord r = (ParsedFastqRecord) record;
            this.id = r.id;
            this.encodedQualities = r.encodedQualities;
            this.qualityCodec = r.qualityCodec;
            this.nucleotideSequenceString = r.nucleotideSequenceString;
            this.turnOffCompression = r.turnOffCompression;           
            this.comment = r.getComment();
            
        }

        @Override
        public FastqRecordBuilder comment(String comments) {
            this.comment = comments;
            return this;
        }

        @Override
        public FastqRecord build() {
            if(lengthsModified){
                assertValidLength();
            }
            if(comment ==null){
                return new ParsedFastqRecord(id, nucleotideSequenceString, encodedQualities, qualityCodec, turnOffCompression);
            }
            return new CommentedParsedFastqRecord(id, nucleotideSequenceString, encodedQualities, qualityCodec, turnOffCompression,comment);
        }

        @Override
        public Optional<String> comment() {
            return Optional.ofNullable(comment);
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public FastqRecordBuilder id(String id) {
            this.id = Objects.requireNonNull(id);
            return this;
            
        }

        @Override
        public NucleotideSequence basecalls() {
            return new NucleotideSequenceBuilder(nucleotideSequenceString).build();
        }

        @Override
        public FastqRecordBuilder basecalls(NucleotideSequence basecalls) {
            this.nucleotideSequenceString = basecalls.toString();
            lengthsModified=true;
            return this;
        }

        @Override
        public QualitySequence qualities() {
            return qualityCodec.decode(encodedQualities);
        }

        @Override
        public FastqRecordBuilder qualities(QualitySequence qualities) {
            this.encodedQualities = qualityCodec.encode(qualities);
            lengthsModified=true;
            return this;
        }

        @Override
        public FastqRecordBuilder trim(Range trimRange) {
            this.encodedQualities =  this.encodedQualities.substring((int) trimRange.getBegin(), (int) trimRange.getEnd()+1);
            this.nucleotideSequenceString = this.nucleotideSequenceString.substring((int) trimRange.getBegin(), (int) trimRange.getEnd()+1);
            lengthsModified=true;
            return this;
        }
        
        private void assertValidLength() {
            long basecallLength = nucleotideSequenceString.length();
            long qualityLength = encodedQualities.length();
            
            if (basecallLength != qualityLength) {
                throw new IllegalArgumentException(String.format(
                        "basecalls and qualities must have the same length! %d vs %d",
                        basecallLength, qualityLength));
            }
        }
	    
	}
}
