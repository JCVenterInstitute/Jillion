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
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.util.iter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.jcvi.common.core.io.IOUtil;

public abstract class AbstractLargeIdIterator implements CloseableIterator<String>{

    private final Scanner scanner;
    private Object nextObject;
    private final Object endOfIterating = new Object();
    protected AbstractLargeIdIterator(File file) throws FileNotFoundException{
        scanner = createScannerFor(file);
        
        updateNextObject();
    }
    protected Scanner createScannerFor(File file) throws FileNotFoundException{
        return new Scanner(file, IOUtil.UTF_8_NAME);
    }
    private void updateNextObject(){
        if(scanner.hasNextLine()){
            nextObject = getNextId(scanner);
            advanceToNextId(scanner);
        }
        else{
            nextObject =endOfIterating;
            scanner.close();
        }
    }
    protected abstract Object getNextId(Scanner scanner);
    protected abstract void advanceToNextId(Scanner scanner);
    
    
    protected final Object getEndOfIterating() {
        return endOfIterating;
    }
    @Override
    public boolean hasNext() {
        return nextObject != endOfIterating;
    }

    @Override
    public String next() {
        if(hasNext()){
            String next = (String)nextObject;
            updateNextObject();
            return next;
        }
        IOUtil.closeAndIgnoreErrors(this);
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub
        
    }
    /**
     * Closes the scanner used to iterate over the ids
     * and set hasNext() to false.
     */
    @Override
    public void close() throws IOException {
        scanner.close();
        nextObject = endOfIterating;
    }
    
}
