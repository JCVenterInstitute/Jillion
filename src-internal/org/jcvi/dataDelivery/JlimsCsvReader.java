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

package org.jcvi.dataDelivery;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.common.core.io.TextLineParser;
import org.jcvi.io.CSVReader;
import org.jcvi.io.SpreadSheetReader;

/**
 * @author dkatzel
 *
 *
 */
public class JlimsCsvReader implements SpreadSheetReader{
    private final SpreadSheetReader reader;
    /**
     * @param in
     * @throws IOException 
     */
    public JlimsCsvReader(InputStream in) throws IOException {
        
        TextLineParser r = new TextLineParser(in);
        //skip useless first line
        String skipped =r.nextLine();
        try{
            reader = new CSVReader(r, true);
        }catch(NullPointerException e){
            //if the barcode doesn't exist, then reading the header
            //will throw a NPE
            throw new IOException("error trying to read barcode manifest, barocde probably doesn't exist",e);
        }
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        reader.close();
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public String[] getColumnNames() {
        return reader.getColumnNames();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Iterator<Map<String, String>> getRowIterator() {
        return reader.getRowIterator();
    }
    
    

}
