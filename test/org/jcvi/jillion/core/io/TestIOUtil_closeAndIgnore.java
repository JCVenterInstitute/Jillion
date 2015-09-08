/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;

import org.jcvi.jillion.core.io.IOUtil;
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
