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
 * 
 */
package org.jcvi.common.core.seq.fastx.fasta;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;
import org.jcvi.common.core.util.ObjectsUtil;


/**
 * {@code AbstractFastaRecord} is an abstract
 * implementation of {@link FastaRecord}
 * to handle common functionality like
 * getting comments, formating etc.
 * 
 * @author jsitz
 * @author dkatzel
 */
public abstract class AbstractFastaRecord<S extends Symbol, T extends Sequence<S>> implements FastaRecord<S,T>
{
    
    private final String identifier;
    private final String comments;

    /**
     * Creates a new <code>SequenceFastaRecord</code>.
     */
    public AbstractFastaRecord(String identifier, String comments)
    {
        super();
        if(identifier == null){
            throw new IllegalArgumentException("identifier can not be null");
        }
        this.identifier = identifier;
        this.comments = comments;
    }

    /**
     * Creates a new <code>SequenceFastaRecord</code>.
     */
    public AbstractFastaRecord(String identifier)
    {
        this(identifier, null);
    }

    /**
     * Creates a new <code>SequenceFastaRecord</code>.
     */
    public AbstractFastaRecord(int identifier, String comments)
    {
        this(Integer.toString(identifier), comments);
    }

    /**
     * Creates a new <code>SequenceFastaRecord</code>.
     */
    public AbstractFastaRecord(int identifier)
    {
        this(Integer.toString(identifier));
    }

    /**
     * @return A <code>String</code>.
     */
    public String getId()
    {
        return this.identifier;
    }

    /**
     * @return A <code>String</code>.
     */
    public String getComment()
    {
        return this.comments;
    }
  
    public String toFormattedString()
    {
        final StringBuilder record = new StringBuilder();
        
        record.append(this.getRecordHeader());
        appendCarriageReturnAndLineFeed(record)
        .append(this.getRecordBody());
        appendCarriageReturnAndLineFeed(record);
        
        return record.toString();
    }
    
    protected abstract CharSequence getRecordBody();
    
    protected CharSequence getRecordHeader()
    {
        final StringBuilder result = new StringBuilder();
        result.append(FastaUtil.HEADER_PREFIX).append(
                this.getId());
        if (this.getComment() != null) {
            result.append(' ').append(this.getComment());
        }
        return result;
    }
    
    protected StringBuilder appendCarriageReturnAndLineFeed(StringBuilder s){
        return s.append(FastaUtil.LINE_SEPARATOR);
        
    }
    
    /**
     * 
    * Gets the entire formatted fasta record as a String,
    * same as {@link #toFormattedString()}.
    * @see #toFormattedString()
     */
    @Override
    public String toString()
    {
        return this.toFormattedString();
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.identifier.hashCode();
        result = prime * result + this.getSequence().hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof FastaRecord)){
            return false;
        }
        AbstractFastaRecord<?,?> other = (AbstractFastaRecord<?,?>)obj;
		return 
        ObjectsUtil.nullSafeEquals(getSequence(), other.getSequence()) 
        && ObjectsUtil.nullSafeEquals(getId(), other.getId());
    }   
}
