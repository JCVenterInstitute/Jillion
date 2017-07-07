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
import java.io.StringReader;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestPushBackBufferedReader {

    private String input = "line one\nline two\nline three";
    
    @Test
    public void noPushBackWorksLikeNormalReader() throws IOException{
        try(PushBackBufferedReader sut = createSUT()){
            assertEquals("line one", sut.readLine());
            assertEquals("line two", sut.readLine());
            assertEquals("line three", sut.readLine());
            assertNull(sut.readLine());
        }
    }
    
    @Test
    public void pushBackFirstLine() throws IOException{
        try(PushBackBufferedReader sut = createSUT()){
            assertEquals("line one", sut.readLine());
            
            sut.pushBack("line one");
            
            assertEquals("line one", sut.readLine());
            assertEquals("line two", sut.readLine());
            assertEquals("line three", sut.readLine());
            assertNull(sut.readLine());
        }
    }
    
    @Test
    public void closeBeforeDone() throws IOException{
        try(PushBackBufferedReader sut = createSUT()){
            assertEquals("line one", sut.readLine());
            
            sut.close();            
          
            assertNull(sut.readLine());
        }
    }
    
    @Test
    public void closeBeforeDoneWithPushBack() throws IOException{
        try(PushBackBufferedReader sut = createSUT()){
            assertEquals("line one", sut.readLine());
            sut.pushBack("not gonna read this");
            sut.close();            
          
            assertNull(sut.readLine());
        }
    }

    private PushBackBufferedReader createSUT() {
        return new PushBackBufferedReader(new BufferedReader(new StringReader(input)));
    }
    
    @Test
    public void pushBackMultipleLine() throws IOException{
        try(PushBackBufferedReader sut = createSUT()){
            assertEquals("line one", sut.readLine());
            
            sut.pushBack("line one");
            sut.pushBack("extra line");
            
            assertEquals("extra line", sut.readLine());
            assertEquals("line one", sut.readLine());
            assertEquals("line two", sut.readLine());
            assertEquals("line three", sut.readLine());
            assertNull(sut.readLine());
        }
    }
}
