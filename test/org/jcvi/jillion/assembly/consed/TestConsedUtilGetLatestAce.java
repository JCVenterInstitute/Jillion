/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
package org.jcvi.jillion.assembly.consed;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsedUtilGetLatestAce {

    File mockEditDir;
    
    @Before
    public void setupMockEditDir() throws SecurityException, NoSuchMethodException{
        //creates a mock File (directory) object which thinks its located at root
        //and allows us to mock the list() method so we can
        //change which files are under it.
        mockEditDir = EasyMock.<File>createMockBuilder(File.class)
        .withConstructor(String.class)
        .withArgs("/")
        .addMockedMethod(File.class.getMethod("listFiles", FileFilter.class))
        .addMockedMethod(File.class.getMethod("exists"))
                .createMock();
    }
    
    @Test
    public void noAceFilesShouldReturnNull(){
    	expect(mockEditDir.exists()).andReturn(true);
        expect(mockEditDir.listFiles(isA(FileFilter.class)))
        
        .andReturn(new File[]{});
        replay(mockEditDir);
        assertNull(ConsedUtil.getLatestAceFile(mockEditDir, "prefix"));
        verify(mockEditDir);
    }
    
    @Test
    public void oneAceShouldReturnIt(){
    	expect(mockEditDir.exists()).andReturn(true);
        expect(mockEditDir.listFiles(isA(FileFilter.class)))
        .andAnswer(filterInputList(
            "prefix.ace.1"));
        
        replay(mockEditDir);
        assertEquals("prefix.ace.1",
                ConsedUtil.getLatestAceFile(mockEditDir, "prefix").getName());
        verify(mockEditDir);
    }
    @Test
    public void multipleAcesShouldReturnHighestVersion(){

    	expect(mockEditDir.exists()).andReturn(true);
        expect(mockEditDir.listFiles(isA(FileFilter.class)))
        .andAnswer(filterInputList(
            "prefix.ace.1",
           "prefix.ace.2",
            "prefix.ace.3"));
        
        replay(mockEditDir);
        assertEquals("prefix.ace.3",ConsedUtil.getLatestAceFile(mockEditDir, "prefix").getName());
        verify(mockEditDir);
    }
   
    @Test
    public void otherPrefixedAceFilesShouldBeIgnored(){
    	expect(mockEditDir.exists()).andReturn(true);
        expect(mockEditDir.listFiles(isA(FileFilter.class)))
        .andAnswer(filterInputList(
                            "prefix.ace.1",
                            "prefix.ace.2",
                            "otherPrefix.ace.3"));
        
        replay(mockEditDir);
        assertEquals("prefix.ace.2",ConsedUtil.getLatestAceFile(mockEditDir, "prefix").getName());
        verify(mockEditDir);
    }
    private static IAnswer<File[]> filterInputList(String...filenames){
    	return ()->{
        	FileFilter filter = EasyMock.getCurrentArgument(0);
        	
        	return Arrays.stream(filenames).map(s-> new File(s))
        			.filter(p-> filter.accept(p))
        			.toArray(size -> new File[size]);
    	};
    }
    @Test
    public void otherSuffixedFilesButWhichContainAceVersionPrefixInNameShouldBeIgnored(){
    	expect(mockEditDir.exists()).andReturn(true);
        expect(mockEditDir.listFiles(isA(FileFilter.class)))
        .andAnswer(filterInputList(
     	           "prefix.ace.1",
     	            "prefix.ace.2",
     	            "prefix.ace.2.consensus.fasta"));
        
       
        
        replay(mockEditDir);
        assertEquals("prefix.ace.2",ConsedUtil.getLatestAceFile(mockEditDir, "prefix").getName());
        verify(mockEditDir);
    }
}
