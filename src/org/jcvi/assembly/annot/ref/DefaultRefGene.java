/*
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;

import org.jcvi.CommonUtil;
import org.jcvi.Range;
import org.jcvi.assembly.annot.Strand;

public class DefaultRefGene implements RefGene{

    private Strand strand;
    private int id, bin;
    private String name, alternateName;
    private String referenceName;
    private Range transcriptionRange;
    private CodingRegion codingRegion;
    
    public DefaultRefGene(String name, String referenceName,
            Strand strand,  Range transcriptionRange, CodingRegion codingRegion){
        this(0,0,name, null, referenceName,strand, transcriptionRange, codingRegion);
    }
    public DefaultRefGene(int id,String name, String referenceName,
            Strand strand,  Range transcriptionRange, CodingRegion codingRegion){
        this(id,0,name, null, referenceName,strand, transcriptionRange, codingRegion);
    }
    public DefaultRefGene(int id, int bin,
            String name,String alternateName,String referenceName, 
            Strand strand, Range transcriptionRange, CodingRegion codingRegion){
        canNotBeNull(name, referenceName, strand, transcriptionRange,
                codingRegion);
        this.id = id;
        this.bin = bin;
        this.name = name;
        this.alternateName = alternateName;
        this.referenceName = referenceName;
        this.strand = strand;
        this.transcriptionRange = transcriptionRange;
        this.codingRegion = codingRegion;
    }
    private void canNotBeNull(String name, String referenceName, Strand strand,
            Range transcriptionRange, CodingRegion codingRegion) {
        if(name ==null){
            throw new IllegalArgumentException("name can not be null");
        }
        if(referenceName ==null){
            throw new IllegalArgumentException("reference name can not be null");
        }
        if(strand ==null){
            throw new IllegalArgumentException("strand can not be null");
        }
        if(transcriptionRange ==null){
            throw new IllegalArgumentException("transcriptionRange can not be null");
        }
        if(codingRegion == null){
            throw new IllegalArgumentException("codingRegion can not be null");
        }
    }
    @Override
    public String getAlternateName() {
        return alternateName;
    }

    @Override
    public int getBin() {
        return bin;
    }

    @Override
    public CodingRegion getCodingRegion() {
        return codingRegion;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getReferenceSequenceName() {
        return referenceName;
    }

    @Override
    public Strand getStrand() {
        return strand;
    }

    @Override
    public Range getTranscriptionRange() {
        return transcriptionRange;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        result = prime * result + referenceName.hashCode();
        result = prime * result + strand.hashCode();
        result = prime * result + transcriptionRange.hashCode();
        result = prime * result +  codingRegion.hashCode();

        
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if(!(obj instanceof DefaultRefGene)){
            return false;
        }
        DefaultRefGene other = (DefaultRefGene) obj;
        return CommonUtil.similarTo(getName(), other.getName())
        && CommonUtil.similarTo(getReferenceSequenceName(), other.getReferenceSequenceName())
        && CommonUtil.similarTo(getStrand(), other.getStrand())
        && CommonUtil.similarTo(getTranscriptionRange(), other.getTranscriptionRange())
        && CommonUtil.similarTo(getCodingRegion(), other.getCodingRegion());
        
    }
    @Override
    public String toString() {
       StringBuilder result = new StringBuilder();
       result.append(this.getClass().getName());
       result.append(" : name : ");
       result.append(getName());
       result.append(" : reference Name : ");
       result.append(getReferenceSequenceName());
       result.append(" strand : ");
       result.append(getStrand());
       result.append(" transcription range : ");
       result.append(getTranscriptionRange());
       result.append(" coding region : ");
       result.append(getCodingRegion());
       return result.toString();
    }

    
}
