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
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc.datastore;

import java.io.File;
import java.sql.SQLException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultNucleotideGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class H2NucleotideDataStore extends AbstractH2EncodedGlyphDataStore<NucleotideGlyph, NucleotideEncodedGlyphs> implements NucleotideDataStore{

    /**
     * @throws DataStoreException
     */
    public H2NucleotideDataStore() throws DataStoreException {
        super();
    }

    /**
     * @param database
     * @throws DataStoreException
     */
    public H2NucleotideDataStore(File database) throws DataStoreException {
        super(database);
    }

    private static final DefaultNucleotideGlyphCodec CODEC = DefaultNucleotideGlyphCodec.getInstance();
    @Override
    public void insertRecord(String id, String basecalls) throws DataStoreException{
        try {
           this.insertRecord(id,CODEC.encode(NucleotideGlyph.getGlyphsFor(basecalls)));          
        } catch (SQLException e) {
            throw new DataStoreException("could not insert "+ id, e);
        }
        
    }
   
    @Override
    public NucleotideEncodedGlyphs get(String id) throws DataStoreException {
        try {
            byte[] data = this.getData(id);
            if(data!=null){
                return new DefaultNucleotideEncodedGlyphs(CODEC.decode(data));
            }
            return null;
        } catch (SQLException e) {
            throw new DataStoreException("error reading DataStore", e);
        }
    }

    
    
}
