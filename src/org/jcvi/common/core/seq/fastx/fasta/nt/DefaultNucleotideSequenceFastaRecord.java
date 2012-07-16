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
/*
 * Created on Jan 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.util.List;

import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

public class DefaultNucleotideSequenceFastaRecord extends AbstractFastaRecord<Nucleotide,NucleotideSequence> implements NucleotideSequenceFastaRecord{

	private final NucleotideSequence sequence;
   
    public DefaultNucleotideSequenceFastaRecord(String identifier, NucleotideSequence sequence){
        this(identifier, null, sequence);
    }
    public DefaultNucleotideSequenceFastaRecord(String identifier, String comments, NucleotideSequence sequence){
    	 super(identifier, comments);
         if(sequence ==null){
         	throw new NullPointerException("sequence can not be null");
         }
         this.sequence = sequence;
    }
    public DefaultNucleotideSequenceFastaRecord(String identifier, String comments, List<Nucleotide> sequence){
    	 this(identifier, comments, new NucleotideSequenceBuilder(sequence).build());
    }
    public DefaultNucleotideSequenceFastaRecord(String identifier,  List<Nucleotide> sequence){
        this(identifier, null,sequence);
    }
    
    /**
     * @param identifier
     * @param sequence
     */
    public DefaultNucleotideSequenceFastaRecord(String identifier, CharSequence sequence) {
        this(identifier, null, sequence);
    }

    /**
     * @param identifier
     * @param comments
     * @param sequence
     */
    public DefaultNucleotideSequenceFastaRecord(String identifier, String comments,
            CharSequence sequence) {
    	super(identifier, comments);
        String nonWhiteSpaceSequence = sequence.toString().replaceAll("\\s+", "");
        this.sequence = new NucleotideSequenceBuilder(nonWhiteSpaceSequence).build();
    }

    @Override
    public NucleotideSequence getSequence() 
    {
        return this.sequence;
    }
    
    
    @Override
    protected CharSequence getRecordBody()
    {
        String result= this.sequence.toString().replaceAll("(.{60})", "$1"+FastaUtil.LINE_SEPARATOR);
        //some fasta parsers such as blast's formatdb
        //break if there is an extra blank line between records
        //this can happen if the sequence ends at the exact length of 1 line
        //(60 characters)
        long length = sequence.getLength();
        if(length >0 && length%60==0){
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
