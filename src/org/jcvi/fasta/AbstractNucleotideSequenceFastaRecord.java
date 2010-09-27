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
package org.jcvi.fasta;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;


/**
 *
 *
 * @author jsitz@jcvi.org
 */
public abstract class AbstractNucleotideSequenceFastaRecord extends AbstractFastaRecord<NucleotideEncodedGlyphs> implements NucleotideSequenceFastaRecord
{
    private final NucleotideEncodedGlyphs sequence;
    private final long checksum;
    
    /**
     * Creates a new <code>SequenceFastaRecord</code>.
     */
    public AbstractNucleotideSequenceFastaRecord(String identifier, String comments, CharSequence sequence)
    {
        super(identifier, comments);
        String nonWhiteSpaceSequence = sequence.toString().replaceAll("\\s+", "");
        this.checksum = this.calculateCheckSum(nonWhiteSpaceSequence);
        this.sequence = encodeNucleotides(nonWhiteSpaceSequence);
        
    }

    protected abstract NucleotideEncodedGlyphs encodeNucleotides(CharSequence sequence2);

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
    public NucleotideEncodedGlyphs getValues() 
    {
        return this.sequence;
    }
    
    
    
    protected CharSequence getRecordBody()
    {
        return this.decodeNucleotides().toString().replaceAll("(.{60})", "$1"+CR);
    }
    
    protected long calculateCheckSum(CharSequence data)
    {
        final Checksum checksummer = new CRC32();
        for (int i = 0; i < data.length(); i++)
        {
            checksummer.update(data.charAt(i));
        }
        return checksummer.getValue();
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
