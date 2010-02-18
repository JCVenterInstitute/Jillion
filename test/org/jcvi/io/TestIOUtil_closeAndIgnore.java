/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestIOUtil_closeAndIgnore {
    @Test
    public void closeNullInputStreamShouldDoNothing(){
        IOUtil.closeAndIgnoreErrors((Closeable)null);
        IOUtil.closeAndIgnoreErrors((Scanner)null);
    }

    @Test
    public void closeCloseable() throws IOException{
        Closeable in = createMock(Closeable.class);
        in.close();

        replay(in);
        IOUtil.closeAndIgnoreErrors(in);
        verify(in);
    }
    @Test
    public void closeScanner(){
        Scanner scanner = new Scanner("example");

        IOUtil.closeAndIgnoreErrors(scanner);
        try{
            scanner.next();
            fail("should throw IllegalState If already closed");
        }
        catch(IllegalStateException e){
            //expected
        }
        
    }
    @Test
    public void closingCloseableThrowingIOExceptionShouldBeIgnored()throws IOException{
        IOException expectedException = new IOException("expected");
        Closeable in = createMock(Closeable.class);
        in.close();
        expectLastCall().andThrow(expectedException);
        replay(in);
        IOUtil.closeAndIgnoreErrors(in);
        verify(in);
    }
}
