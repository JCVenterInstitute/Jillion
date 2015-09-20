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
