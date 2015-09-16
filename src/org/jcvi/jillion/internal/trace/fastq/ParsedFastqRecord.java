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
    
    private final NucleotideSequence nucleotideSequence;
    private QualitySequence qualitySequence;
    
    private final boolean turnOffCompression;
    
    
    public ParsedFastqRecord(String id, NucleotideSequence nucleotideSequence,
            String encodedQualities, FastqQualityCodec qualityCodec,
            boolean turnOffCompression) {
        this.id = id;
        this.nucleotideSequence = nucleotideSequence;
        this.encodedQualities = encodedQualities;
        this.qualityCodec = qualityCodec;
        this.turnOffCompression = turnOffCompression;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public NucleotideSequence getNucleotideSequence() {
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;         
        result = prime * result + id.hashCode();
        result = prime * result
                + nucleotideSequence.hashCode();
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
        if (!nucleotideSequence.equals(other.getNucleotideSequence())) {
            return false;
        }
        if (!getQualitySequence().equals(other.getQualitySequence())) {
            return false;
        }
        return true;
    }

}
