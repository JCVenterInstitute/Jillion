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
package org.jcvi.jillion.internal.core.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.io.InputStreamSupplier;

public class PositionlessLineParser implements LineParser{

    private final BufferedReader reader;
    
    private String nextLine;
    private boolean isClosed;
    
    public PositionlessLineParser(File inputFile) throws IOException{
        this(InputStreamSupplier.forFile(inputFile));
    }
    public PositionlessLineParser(InputStream in) throws IOException{
        reader = new BufferedReader(new InputStreamReader(in,IOUtil.UTF_8));
        updateNextLine();
    }
    public PositionlessLineParser(InputStreamSupplier inputStreamSupplier) throws IOException{
        reader = new BufferedReader(new InputStreamReader(inputStreamSupplier.get(),IOUtil.UTF_8));
        updateNextLine();
    }
    
    private void updateNextLine() throws IOException{
        nextLine = reader.readLine();
    }
    
    
    
    @Override
    public boolean hasNextLine() {

        return nextLine!=null;
    }

    @Override
    public String peekLine() {
        return nextLine;
    }

    @Override
    public long getPosition() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tracksPosition() {
        return false;
    }

    @Override
    public String nextLine() throws IOException {
        if(isClosed){
            throw new IOException("closed");
        }
        String oldLine = nextLine;
        updateNextLine();
        return oldLine;
    }

    @Override
    public void close() throws IOException {
        reader.close();
        nextLine=null;
        isClosed = true;
    }

}
