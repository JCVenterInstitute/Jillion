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
package org.jcvi.common.io.idReader;

import java.io.IOException;
import java.util.Iterator;
/**
 * {@code CommaSeparatedIdReader} is an {@link IdReader}
 * that reads ids separated by ",".
 * @author dkatzel
 *
 *
 */
public final class CommaSeparatedIdReader<T> implements IdReader<T> {
    private final String ids;
    private final IdParser<T> idParser;
    
    public CommaSeparatedIdReader(String commaSeparatedIds, IdParser<T> idParser){
        this.ids = commaSeparatedIds;
        this.idParser = idParser;
    }
    @Override
    public Iterator<T> getIds() {
        return new ArrayIterator<T>(ids.split(","),idParser);
    }

    @Override
    public void close() throws IOException {
        //no-op
    }
    @Override
    public Iterator<T> iterator() {
       return getIds();
    }
    
    private static final class ArrayIterator<T> implements Iterator<T>{
        private final String[] array;
        private int currentPosition;
        private final IdParser<T> idParser;
        private ArrayIterator(String[] array,IdParser<T> idParser){
            this.array = array;
            this.idParser = idParser;
        }
        @Override
        public synchronized boolean hasNext() {
            return currentPosition< array.length;
        }

        @Override
        public synchronized T next() {            
            return idParser.parseIdFrom(array[currentPosition++]);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove() not allowed");            
        }
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfIds() throws IdReaderException {
        return ids.split(",").length;
    }
}
