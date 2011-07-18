/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/**
 * SequenceFastaRecord.java
 *
 * Created: Sep 30, 2008 - 2:52:43 PM (jsitz)
 *
 * Copyright 2008 J. Craig Venter Institute
 */
package org.jcvi.common.core.seq.nuc.fasta;

import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;


/**
 *{@code AbstractNucleotideSequenceFastaRecord} is an abstract
 *implementation of {@link NucleotideSequenceFastaRecord}
 *that handles common nucleotide sequence fasta record
 *methods like formatting and check summing the sequence data.
 *
 * @author jsitz@jcvi.org
 * @author dkatzel
 */
public abstract class AbstractNucleotideSequenceFastaRecord extends AbstractFastaRecord<NucleotideSequence> implements NucleotideSequenceFastaRecord
{
    private final NucleotideSequence sequence;
    private final long checksum;
    
    /**
     * Creates a new <code>SequenceFastaRecord</code>.
     */
    public AbstractNucleotideSequenceFastaRecord(String identifier, String comments, CharSequence sequence)
    {
        super(identifier, comments);
        String nonWhiteSpaceSequence = sequence.toString().replaceAll("\\s+", "");
        this.checksum = FastaUtil.calculateCheckSum(nonWhiteSpaceSequence);
        this.sequence = encodeNucleotides(nonWhiteSpaceSequence);
        
    }

    protected abstract NucleotideSequence encodeNucleotides(CharSequence sequence2);

    protected abstract CharSequence decodeNucleotides();
    /**
     * Creates a new <code>SequenceFastaRecord</code>.
     */
    public AbstractNucleotideSequenceFastaRecord(String identifier, CharSequence sequence)
    {
        this(identifier, null, sequence);
    }

    /**
     * Creates a new <code>SequenceFastaRecord</code>.
     */
    public AbstractNucleotideSequenceFastaRecord(int identifier, String comments, CharSequence sequence)
    {
        this(Integer.toString(identifier), comments, sequence);
    }

    /**
     * Creates a new <code>SequenceFastaRecord</code>.
     */
    public AbstractNucleotideSequenceFastaRecord(int identifier, CharSequence sequence)
    {
        this(Integer.toString(identifier), sequence);
    }
    @Override
    public long getChecksum()
    {
        return this.checksum;
    }

    @Override
    public NucleotideSequence getValue() 
    {
        return this.sequence;
    }
    
    
    
    protected CharSequence getRecordBody()
    {
        String result= this.decodeNucleotides().toString().replaceAll("(.{60})", "$1"+FastaUtil.CR);
        //some fasta parsers such as blast's formatdb
        //break if there is an extra blank line between records
        //this can happen if the sequence ends at the exact lenght of 1 line
        //(60 characters)
        if(sequence.getLength() >0 && sequence.getLength()%60==0){
            return result.substring(0, result.length()-1);
        }
        return result;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof NucleotideSequenceFastaRecord)){
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    
}
