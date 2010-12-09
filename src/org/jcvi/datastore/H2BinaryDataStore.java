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

package org.jcvi.datastore;

import java.sql.SQLException;
import java.nio.ByteBuffer;
/**
 * @author dkatzel
 *
 *
 */
public class H2BinaryDataStore extends AbstractH2BinaryDataStore<ByteBuffer>{

    /**
     * @throws DataStoreException
     */
    public H2BinaryDataStore() throws DataStoreException {
        super();
    }

    /**
     * @param fileDatabasePath
     * @throws DataStoreException
     */
    public H2BinaryDataStore(String fileDatabasePath)
            throws DataStoreException {
        super(fileDatabasePath);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public ByteBuffer get(String id) throws DataStoreException {
        byte[] data;
        try {
            data = this.getData(id);
            
            if(data==null){
                return null;
            }
            return ByteBuffer.wrap(data);
        } catch (SQLException e) {
            throw new DataStoreException("error getting data for "+id,e);
        }
    }

    @Override
    public void insertRecord(String id, byte[] data) throws SQLException {
        super.insertRecord(id, data);
    }
    

}
