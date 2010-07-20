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
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Scanner;

public class DefaultFileIdReader<T> implements IdReader<T> {
    private final File file;
    private final IdParser<T> idParser;
    public DefaultFileIdReader(File file, IdParser<T> idParser){
        this.file =file;
        this.idParser = idParser;
    }

    @Override
    public Iterator<T> getIds() throws IdReaderException{
        try {
            return new FileIdIterator(new FileInputStream(file), idParser);
        } catch (FileNotFoundException e) {
           throw new IdReaderException("error trying to read file", e);
        }
    }

    @Override
    public void close() throws IOException {
        
    }
    
    private static class FileIdIterator<T> implements Iterator<T>{
        private Scanner scanner;
        private IdParser<T> idParser;
        private String nextValidString;
        boolean needToLookAhead =true;
        boolean hasNext;
        FileIdIterator(InputStream in, IdParser<T> idParser){
            scanner = new Scanner(in);
            this.idParser = idParser;
        }
        private synchronized void getNextValidString(){
            needToLookAhead=false;
            boolean done = false;
            hasNext=false;
            while(!done && scanner.hasNextLine()){
                final String line = scanner.nextLine();
                if(idParser.isValidId(line)){
                    hasNext=true;
                    nextValidString = line;
                    done=true;
                }
            }
            
           
        }
        @Override
        public synchronized boolean hasNext() {
            if(needToLookAhead){
                getNextValidString();
            }
            if(!hasNext){
                scanner.close();
            }
            return hasNext;
        }

        @Override
        public synchronized T next() {
            if(needToLookAhead){
                getNextValidString();
            }
            T id= idParser.parseIdFrom(nextValidString);
            needToLookAhead=true;
            return id;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove() not allowed");            
        }
        
    }

    @Override
    public Iterator<T> iterator() {
        try {
            return getIds();
        } catch (IdReaderException e) {
            throw new IllegalStateException("could not create iterator over ids",e);
        }
    }
}
