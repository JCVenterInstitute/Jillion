/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
/**
 * A {@link BufferedReader} that allows lines to be pushed back to be read again.
 * 
 * This class should only be used when the only operation on the reader 
 * is {@link BufferedReader#readLine()}.  Calling other methods such as {@link #read()}
 * could lead to an invalid state since end of line characters are lost when reading lines.
 * 
 * @author dkatzel
 *
 * @since 5.3
 */
public class PushBackBufferedReader extends BufferedReader{

    private final LinkedList<String> pushBackStack = new LinkedList<>();
    private volatile boolean isClosed;
    
    public PushBackBufferedReader(BufferedReader in) {
        super(in);
    }
    
    public void pushBack(String line){
        pushBackStack.push(Objects.requireNonNull(line));
    }

    @Override
    public String readLine() throws IOException {
        if(isClosed){
            return null;
        }
       if(!pushBackStack.isEmpty()){
           return pushBackStack.pop();
       }
        return super.readLine();
    }

    @Override
    public void close() throws IOException {
        isClosed = true;
        super.close();
    }

    
    
    
    

}
