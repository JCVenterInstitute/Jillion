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

package org.jcvi.common.command;
import static org.easymock.EasyMock.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestCommand extends EasyMockSupport{

    private File exeFile;
    String path = "path/to/file";
    private Command sut;
    
    @Before
    public void setup(){
        exeFile = EasyMock.createMock(File.class);
        expect(exeFile.canExecute()).andStubReturn(true);
        
        expect(exeFile.getAbsolutePath()).andStubReturn(path);
        EasyMock.replay(exeFile);
        sut = new Command(exeFile);
    }
    @Test(expected = NullPointerException.class)
    public void nullFileShouldThrowNPE(){
        new Command((File)null);
    }
    
    @Test
    public void unExecutableFileShouldThrowIllegalArgumentException(){
        File mockFile = createMock(File.class);
        expect(mockFile.canExecute()).andReturn(false);
        expect(mockFile.getAbsolutePath()).andStubReturn(path);
        replayAll();
        try{
            new Command(mockFile);
            fail("should throw exception if file not executable");
        }catch(IllegalArgumentException e){
            assertEquals(path + " can not be executed", e.getMessage());
        }
        verifyAll();
    }
    
    @Test
    public void addOneFlag(){
        sut.addFlag("-f");
        assertArguments("-f");
        
    }
    
    @Test
    public void addMultipleFlagsInDifferentCalls(){
        sut.addFlag("-f");
        sut.addFlag("-v");
        assertArguments("-f","-v");
        
    }
    @Test
    public void addMultipleFlagsInSameCall(){
        sut.addFlag("-f", "-v");
        assertArguments("-f","-v");
    }
    
    @Test
    public void removeOnlyFlag(){
        sut.addFlag("-f");
        sut.removeFlag("-f");
        assertArguments();
    }
    
    @Test
    public void removeFlag(){
        sut.addFlag("-f", "-v");
        sut.removeFlag("-f");
        assertArguments("-v");
    }
    
    @Test
    public void removeAllFlags(){
        sut.addFlag("-f", "-v");
        sut.removeAllFlags();
        assertArguments();
    }
    
    @Test
    public void setOption(){
        sut.setOption("-key", "value");
        assertArguments("-key","value");
    }
    @Test
    public void overrideOption(){
        sut.setOption("-key", "value");
        sut.setOption("-key", "new_value");
        assertArguments("-key","new_value");
    }
    
    @Test
    public void clearOption(){
        sut.setOption("-key", "value");
        sut.clearOption("-key");
        assertArguments();
    }
    
    @Test
    public void multipleOptions(){
        sut.setOption("-key", "value");
        sut.setOption("-diff_key", "diff_value");
        assertArguments("-key","value",
                        "-diff_key", "diff_value");
    }
    
    @Test
    public void clearAllOptions(){
        sut.setOption("-key", "value");
        sut.setOption("-diff_key", "diff_value");
        sut.clearAllOptions();
        assertArguments();
    }
    
    @Test
    public void addTarget(){
        sut.addTarget("target");
        assertArguments("target");
    }
    @Test
    public void addMultipleTargetsInDifferentCalls(){
        sut.addTarget("target");
        sut.addTarget("target2");
        assertArguments("target", "target2");
    }
    @Test
    public void addMultipleTargets(){
        sut.addTargets("target","target2");
        assertArguments("target", "target2");
    }
    
    @Test
    public void removeTargets(){
        sut.addTargets("target","target2");
        sut.removeTarget("target");
        assertArguments("target2");
    }
    @Test
    public void removeAllTargets(){
        sut.addTargets("target","target2");
        sut.removeAllTargets();
        assertArguments();
    }
    
    @Test
    public void flagsOptionsAndTargets(){
        sut.addFlag("-f");
        sut.setOption("-key","value");
        sut.addTarget("target");
        assertArguments("-key","value","-f","target");
    }
    
    private void assertArguments(String... expectedArgs){
        List<String> actualArgs= sut.getArguments();
        assertEquals(Arrays.asList(expectedArgs), actualArgs);
    }
}
