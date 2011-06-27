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
package org.jcvi.glyph;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;

import org.jcvi.io.IOUtil;
import org.jcvi.util.CloseableIterator;
/**
 * {@code AbstractH2EncodedGlyphDataStore} is an {@link DataStore} of
 * {@link EncodedGlyphs}.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractH2EncodedGlyphDataStore<G extends Glyph, E extends EncodedGlyphs<G>> implements DataStore<E>{
    static final String DRIVER_STRING = "org.h2.Driver";
    static final String CONNECTION_SUBSTRING = "jdbc:h2:";
    static final String CREATE_TABLE = "CREATE TABLE DATA(id VARCHAR, data VARBINARY)";

    
    private PreparedStatement insertStatement;
    private PreparedStatement getStatement;
    private PreparedStatement containsStatement;
    private PreparedStatement sizeStatement;
    private PreparedStatement idsStatement;
    
    private final Connection connection;
    private final File dataStoreFile;
    public AbstractH2EncodedGlyphDataStore(File database) throws DataStoreException{
        StringBuilder connectionBuilder = new StringBuilder(CONNECTION_SUBSTRING)
                                            .append("file:")
                                            .append(database.getAbsolutePath());
        connection = connect(connectionBuilder.toString());
        dataStoreFile = database;
        initialize();
    }
    
    public AbstractH2EncodedGlyphDataStore() throws DataStoreException{
        StringBuilder connectionBuilder = new StringBuilder(CONNECTION_SUBSTRING)
                                            .append("mem:");
        connection = connect(connectionBuilder.toString());
        dataStoreFile =null;
        initialize();
    }
    private final Connection connect(String connectionString) throws DataStoreException{
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection(connectionString);
        } catch (Exception e) {
           throw new DataStoreException("could not initialize H2 database", e);
        }
        

    }
    
    private final void initialize() throws DataStoreException{
        Statement statement=null;
        try {
            statement =connection.createStatement();
            statement.executeUpdate(CREATE_TABLE);
            insertStatement = connection.prepareStatement("INSERT INTO DATA(id, data) VALUES(?, ?)");
            getStatement = connection.prepareStatement("select data from DATA where id=?");
            containsStatement = connection.prepareStatement("select 1 from DATA where id=?");
            sizeStatement = connection.prepareStatement("select COUNT(id) from DATA");
            idsStatement = connection.prepareStatement("select id from DATA");
        }
        catch(SQLException e){
            throw new DataStoreException("error initializing H2 datastore",e);
        }finally{
            IOUtil.closeAndIgnoreErrors(statement);
        }
    }

    protected void insertRecord(String id, byte[] data) throws SQLException{

                insertStatement.setString(1, id);
                insertStatement.setBytes(2, data);
                insertStatement.execute();

    }
    
    
    @Override
    public boolean contains(String id) throws DataStoreException {
        ResultSet resultSet=null;
        try {
            containsStatement.setString(1, id);
            resultSet =containsStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new DataStoreException("error reading DataStore", e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(resultSet);
        }
    }

    public abstract void insertRecord(String id, String basecalls) throws DataStoreException;
    
    protected byte[] getData(String id) throws SQLException{
        ResultSet resultSet=null;
        try {
            getStatement.setString(1, id);
            resultSet =getStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getBytes(1);
            }
            return null;
        }
        finally{
            IOUtil.closeAndIgnoreErrors(resultSet);
        }
    }
    

    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        try {
            return new IdIterator();
        } catch (SQLException e) {
            throw new DataStoreException("could not create id iterator", e);
        }
    }

    @Override
    public int size() throws DataStoreException {
        ResultSet resultSet=null;
        try {
            resultSet =sizeStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DataStoreException("error reading DataStore", e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(resultSet);
        }
    }

    @Override
    public void close() throws IOException {
        IOUtil.closeAndIgnoreErrors(containsStatement);
        IOUtil.closeAndIgnoreErrors(idsStatement);
        IOUtil.closeAndIgnoreErrors(getStatement);
        IOUtil.closeAndIgnoreErrors(sizeStatement);
        IOUtil.closeAndIgnoreErrors(insertStatement);
        IOUtil.closeAndIgnoreErrors(connection);
        if(dataStoreFile !=null){
            //try to delete. ignore failures...
            
            deleteH2DatabaseFiles();
        }
    }

    
    
    
    @Override
    public boolean isClosed() throws DataStoreException {
        try {
            return containsStatement.isClosed();
        } catch (SQLException e) {
            throw new DataStoreException("error checking if closed",e);
        }
    }

    private void deleteH2DatabaseFiles(){
      //containing logs, index and data for all tables
        IOUtil.deleteIgnoreError(new File(dataStoreFile+".h2.db"));
        IOUtil.deleteIgnoreError(new File(dataStoreFile+".lock.db"));
        IOUtil.deleteIgnoreError(new File(dataStoreFile+".trace.db"));
        
        IOUtil.deleteIgnoreError(dataStoreFile);
    }

    @Override
    public CloseableIterator<E> iterator() {
        return new DataStoreIterator<E>(this);
    }
    
    private class IdIterator implements CloseableIterator<String>{

        private final ResultSet resultSet;
        private final Object endOfIterator = new Object();
        private Object nextObject;
        private IdIterator() throws SQLException{
            resultSet = idsStatement.executeQuery();
            getNextObject();
        }
        
        private void getNextObject() throws SQLException{
            if(resultSet.next()){
                nextObject = resultSet.getString(1);
            }
            else{
                nextObject = endOfIterator;
            }
        }
        @Override
        public boolean hasNext() {
            boolean hasNext= nextObject !=endOfIterator;
            if(!hasNext){
                IOUtil.closeAndIgnoreErrors(this);
            }
            return hasNext;
        }

        @Override
        public String next() {
            String ret = (String) nextObject;
            try {
                getNextObject();
            } catch (SQLException e) {
                throw new IllegalStateException("could not fetch next id",e);
            }
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("can not remove");
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            try {
                resultSet.close();
            } catch (SQLException e) {
               throw new IOException("error closing result set",e);
            }
            
        }
        
    }
}
