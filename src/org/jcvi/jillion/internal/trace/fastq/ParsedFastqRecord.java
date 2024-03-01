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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
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
	
	private static class StringProcessingNucleotideSequence implements NucleotideSequence{
		
		
		private final String ntString;

		public StringProcessingNucleotideSequence(String ntString) {
			this.ntString = ntString;
		}

		

		@Override
		public Stream<Range> findMatches(Pattern pattern) {
			
			return findHitsInString(pattern, ntString,0);
		}

		


		@Override
		public Nucleotide get(long offset) {
			return Nucleotide.parse(ntString.charAt((int) offset));
		}



		@Override
		public long getLength() {
			return ntString.length();
		}



		@Override
		public Iterator<Nucleotide> iterator() {
			return new NucIter();
		}



		@Override
		public NucleotideSequence toNucleotideSequence() {
			return this;
		}



		@Override
		public NucleotideSequence trim(Range trimRange) {
			return new StringProcessingNucleotideSequence(ntString.substring((int)trimRange.getBegin(), (int)trimRange.getEnd()+1));
		}



		private Stream<Range> findHitsInString(Pattern pattern, String s, int shiftAmount) {
			Matcher m = pattern.matcher(s);
			List<Range> hits = new ArrayList<>();
			while(m.find()) {
				hits.add(Range.of(m.start()+shiftAmount, m.end()-1+shiftAmount));
			}
			return hits.stream();
		}

		@Override
		public Stream<Range> findMatches(Pattern pattern, Range subSequenceRange) {
			int shift = (int) subSequenceRange.getBegin();
			String sub = ntString.substring(shift,(int) subSequenceRange.getEnd()+1);
			return findHitsInString(pattern, sub, shift);
			
		}
 

		private class NucIter implements Iterator<Nucleotide>{
			private int offset;

			@Override
			public boolean hasNext() {
				return offset< StringProcessingNucleotideSequence.this.ntString.length();
			}

			@Override
			public Nucleotide next() {
				if(!hasNext()) {
					throw new NoSuchElementException();
				}
				return get( ++offset);
			}
			
		}


		@Override
		public NucleotideSequenceBuilder toBuilder(List<Range> ranges) {
			
			return new NucleotideSequenceBuilder(this, ranges);
		}



		@Override
		public boolean isDna() {
			// TODO assume DNA
			return true;
		}



		@Override
		public List<Range> getRangesOfNs() {
			int firstN = ntString.indexOf('N');
			if(firstN <0) {
				return Collections.emptyList();
			}
			BitSet bs = new BitSet();
			bs.set(firstN);
			for(int i = firstN+1; i>=0; i=ntString.indexOf('N', i)) {
				bs.set(i);
			}
			
			return Ranges.asRanges(bs);
		}



		@Override
		public List<Integer> getGapOffsets() {
			// TODO for now assume no gaps
			return Collections.emptyList();
		}



		@Override
		public List<Range> getRangesOfGaps() {
			// TODO for now assume no gaps
			return Collections.emptyList();
		}



		@Override
		public int getNumberOfGaps() {
			// TODO assume no gaps
			return 0;
		}



		@Override
		public boolean isGap(int gappedOffset) {
			// TODO assume no gaps
			return false;
		}



		@Override
		public long getUngappedLength() {
			return ntString.length();
		}



		@Override
		public int getNumberOfGapsUntil(int gappedOffset) {
			// TODO assume no gaps
			return 0;
		}



		@Override
		public int getUngappedOffsetFor(int gappedOffset) {
			// TODO assume no gaps
			return gappedOffset;
		}



		@Override
		public int getGappedOffsetFor(int ungappedOffset) {
			// TODO assume no gaps
			return ungappedOffset;
		}



		@Override
		public NucleotideSequenceBuilder toBuilder(Range range) {
			return new NucleotideSequenceBuilder(ntString.substring((int) range.getBegin(), (int) range.getEnd()+1));
		}



		@Override
		public NucleotideSequence asSubtype() {
			return this;
		}



		@Override
		public Iterator<Nucleotide> iterator(Range range) {
			// since we're just doing strings this should be the fastest...
			return trim(range).iterator();
		}



		@Override
		public NucleotideSequenceBuilder toBuilder() {
			return new NucleotideSequenceBuilder(ntString);
		}



		@Override
		public int getUngappedOffsetForSafe(int gappedOffset) {
			// TODO assume no gaps
			return gappedOffset;
		}
		
		
	}
}
