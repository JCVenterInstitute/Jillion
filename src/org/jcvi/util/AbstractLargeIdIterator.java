/*
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

public abstract class AbstractLargeIdIterator implements Iterator<String>{

    private final Scanner scanner;
    private Object nextObject;
    private final Object endOfIterating = new Object();
    
    protected AbstractLargeIdIterator(File file) throws FileNotFoundException{
        scanner = new Scanner(file);
        
        updateNextObject();
    }
    private void updateNextObject(){
        if(scanner.hasNextLine()){
            nextObject = getNextId(scanner);
            advanceToNextId(scanner);
        }
        else{
            nextObject =endOfIterating;
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
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub
        
    }
}
