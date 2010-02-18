/*
 * Created on Jun 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public abstract class AbstractSpreadSheetReader implements SpreadSheetReader {

    private final Scanner scanner;
    private final String[] columns;
    private final boolean hasHeaders;
    public AbstractSpreadSheetReader(InputStream in){
        this(in, false);
    }
    public AbstractSpreadSheetReader(InputStream in, boolean hasHeaders){
        this.scanner = new Scanner(in);
        this.hasHeaders = hasHeaders;
        if(hasHeaders){
            columns =scanner.nextLine().split(getColumnSeparator());
        }
        else{
            columns =null;
        }
        
    }

    protected abstract String getColumnSeparator();
    
    protected boolean hasHeaders() {
        return hasHeaders;
    }
    @Override
    public String[] getColumnNames() {
        if(!hasHeaders){
            return null;
        }
        //defensive copy
        return Arrays.copyOf(columns, columns.length);
    }

    @Override
    public Iterator<Map<String, String>> getRowIterator() {
       
        return new Iterator<Map<String, String>>(){
            @Override
            public boolean hasNext() {
                return scanner.hasNextLine();
            }

            @Override
            public Map<String, String> next() {
                String line = scanner.nextLine();
                Map<String,String> rowData = new HashMap<String, String>();
                String[] rows = line.split(getColumnSeparator());
                
                for(int i=0; i<rows.length; i++){
                    String columnName;
                    if(hasHeaders){
                        columnName = columns[i];
                    }
                    else{
                        columnName = Integer.valueOf(i).toString();
                    }
                    rowData.put(columnName, rows[i]);
                }
                return Collections.unmodifiableMap(rowData);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("can not remove from spreadsheet file");
                
            }
        };
    }

    @Override
    public void close() throws IOException {
        scanner.close();
        
    }
    
    
    

}

