/**
 * 
 */
package org.jcvi.fasta;

import org.jcvi.CommonUtil;


/**
 * 
 * 
 * @author jsitz@jcvi.org
 */
public abstract class AbstractFastaRecord<T> implements FastaRecord<T>
{
    protected static final String CR = "\n";

    private static final char HEADER_PREFIX = '>';
    
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
    public String getIdentifier()
    {
        return this.identifier;
    }

    /**
     * @return A <code>String</code>.
     */
    public String getComments()
    {
        return this.comments;
    }
    
    public CharSequence getStringRecord()
    {
        final StringBuilder record = new StringBuilder();
        
        record.append(this.getRecordHeader());
        appendCarriageReturnAndLineFeed(record)
        .append(this.getRecordBody());
        appendCarriageReturnAndLineFeed(record);
        
        return record;
    }
    
    protected abstract CharSequence getRecordBody();
    
    protected CharSequence getRecordHeader()
    {
        final StringBuilder result = new StringBuilder();
        result.append(AbstractFastaRecord.HEADER_PREFIX).append(
                this.getIdentifier());
        if (this.getComments() != null) {
            result.append(' ').append(this.getComments());
        }
        return result;
    }
    
    protected StringBuilder appendCarriageReturnAndLineFeed(StringBuilder s){
        return s.append(CR);
        
    }
    
    @Override
    public long getChecksum() {
        return 0;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.getStringRecord().toString();
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int)(this.getChecksum() ^ (this.getChecksum() >>> 32));
        result = prime * result + ((this.identifier == null) ? 0 : this.identifier.hashCode());
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
        AbstractFastaRecord other = (AbstractFastaRecord)obj;
        final long checksum = getChecksum();
        final long checksum2 = other.getChecksum();
        return 
        //CommonUtil.similarTo(getRecordBody(), other.getRecordBody())
        CommonUtil.similarTo(checksum, checksum2)
        && CommonUtil.similarTo(getIdentifier(), other.getIdentifier());
    }   
}