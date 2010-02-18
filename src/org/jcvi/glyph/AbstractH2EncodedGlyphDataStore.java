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
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;

import org.jcvi.io.IOUtil;

public abstract class AbstractH2EncodedGlyphDataStore<G extends Glyph, E extends EncodedGlyphs<G>> implements DataStore<E>{
    static final String DRIVER_STRING = "org.h2.Driver";
    static final String CONNECTION_SUBSTRING = "jdbc:h2:";
    static final String CREATE_TABLE = "CREATE TABLE DATA(id VARCHAR, data VARBINARY)";

    
    private PreparedStatement INSERT_STATEMENT;
    private PreparedStatement GET_STATEMENT;
    private PreparedStatement CONTAINS_STATEMENT;
    private PreparedStatement SIZE_STATEMENT;
    private PreparedStatement IDS_STATEMENT;
    
    private final Connection connection;
    public AbstractH2EncodedGlyphDataStore(File database) throws DataStoreException{
        StringBuilder connectionBuilder = new StringBuilder(CONNECTION_SUBSTRING)
                                            .append("file:")
                                            .append(database.getAbsolutePath());
        connection = connect(connectionBuilder.toString());
        initialize();
    }
    
    public AbstractH2EncodedGlyphDataStore() throws DataStoreException{
        StringBuilder connectionBuilder = new StringBuilder(CONNECTION_SUBSTRING)
                                            .append("mem:");
        connection = connect(connectionBuilder.toString());
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
            INSERT_STATEMENT = connection.prepareStatement("INSERT INTO DATA(id, data) VALUES(?, ?)");
            GET_STATEMENT = connection.prepareStatement("select data from DATA where id=?");
            CONTAINS_STATEMENT = connection.prepareStatement("select 1 from DATA where id=?");
            SIZE_STATEMENT = connection.prepareStatement("select COUNT(id) from DATA");
            IDS_STATEMENT = connection.prepareStatement("select id from DATA");
        }
        catch(SQLException e){
            throw new DataStoreException("error initializing H2 datastore",e);
        }finally{
            IOUtil.closeAndIgnoreErrors(statement);
        }
    }

    protected void insertRecord(String id, byte[] data) throws SQLException{

                INSERT_STATEMENT.setString(1, id);
                INSERT_STATEMENT.setBytes(2, data);
                INSERT_STATEMENT.execute();

    }
    
    
    @Override
    public boolean contains(String id) throws DataStoreException {
        ResultSet resultSet=null;
        try {
            CONTAINS_STATEMENT.setString(1, id);
            resultSet =CONTAINS_STATEMENT.executeQuery();
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
            GET_STATEMENT.setString(1, id);
            resultSet =GET_STATEMENT.executeQuery();
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
    public Iterator<String> getIds() throws DataStoreException {
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
            resultSet =SIZE_STATEMENT.executeQuery();
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
        IOUtil.closeAndIgnoreErrors(CONTAINS_STATEMENT);
        IOUtil.closeAndIgnoreErrors(IDS_STATEMENT);
        IOUtil.closeAndIgnoreErrors(GET_STATEMENT);
        IOUtil.closeAndIgnoreErrors(SIZE_STATEMENT);
        IOUtil.closeAndIgnoreErrors(INSERT_STATEMENT);
        IOUtil.closeAndIgnoreErrors(connection);
    }

    @Override
    public Iterator<E> iterator() {
        return new DataStoreIterator<E>(this);
    }
    
    private class IdIterator implements Iterator<String>{

        private final ResultSet resultSet;
        private final Object endOfIterator = new Object();
        private Object nextObject;
        private IdIterator() throws SQLException{
            resultSet = IDS_STATEMENT.executeQuery();
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
                IOUtil.closeAndIgnoreErrors(resultSet);
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
        
    }
}
