package org.jcvi.jillion.internal.trace.fastq;

import org.jcvi.jillion.trace.fastq.FastqQualityCodec;

public class CommentedParsedFastqRecord extends ParsedFastqRecord {

    private final String comment;
  
    public CommentedParsedFastqRecord(String id, String nucleotideSequence,
            String encodedQualities, FastqQualityCodec qualityCodec,
            boolean turnOffCompression, String optionalComment) {
        super(id, nucleotideSequence, encodedQualities, qualityCodec, turnOffCompression);
        this.comment = optionalComment;
    }
    
    @Override
    public String getComment() {
        return comment;
    }
    

}
