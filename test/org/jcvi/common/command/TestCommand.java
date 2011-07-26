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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.jcvi.common.command.Command.ProcessBuilderWrapper;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestCommand extends EasyMockSupport{

    private static class CommandTestDouble extends Command{
        private final ProcessBuilderWrapper mockBuilder;
        public CommandTestDouble(File executable,ProcessBuilderWrapper mockBuilder) {
            super(executable);
            this.mockBuilder = mockBuilder;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected ProcessBuilderWrapper createNewProcessBuilderWrapper() {
            return mockBuilder;
        }
        
    }
    private File exeFile;
    String path = "path/to/file";
    private Command sut;
    private ProcessBuilderWrapper mockBuilderWrapper;
    @Before
    public void setup(){
        exeFile = EasyMock.createMock(File.class);
        expect(exeFile.canExecute()).andStubReturn(true);
        
        expect(exeFile.getAbsolutePath()).andStubReturn(path);
        EasyMock.replay(exeFile);
        mockBuilderWrapper = createMock(ProcessBuilderWrapper.class);
        
        sut = new CommandTestDouble(exeFile,mockBuilderWrapper);
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
        assertCommandLineIsCorrect("-f");
        
    }
    
    @Test
    public void addMultipleFlagsInDifferentCalls(){
        sut.addFlag("-f");
        sut.addFlag("-v");
        assertCommandLineIsCorrect("-f","-v");
        
    }
    @Test
    public void addMultipleFlagsInSameCall(){
        sut.addFlag("-f", "-v");
        assertCommandLineIsCorrect("-f","-v");
    }
    
    @Test
    public void removeOnlyFlag(){
        sut.addFlag("-f");
        sut.removeFlag("-f");
        assertCommandLineIsCorrect();
    }
    
    @Test
    public void removeFlag(){
        sut.addFlag("-f", "-v");
        sut.removeFlag("-f");
        assertCommandLineIsCorrect("-v");
    }
    
    @Test
    public void removeAllFlags(){
        sut.addFlag("-f", "-v");
        sut.removeAllFlags();
        assertCommandLineIsCorrect();
    }
    
    @Test
    public void setOption(){
        sut.setOption("-key", "value");
        assertCommandLineIsCorrect("-key","value");
    }
    @Test(expected = NullPointerException.class)
    public void setOptionWithNullValueShouldThrowNPE(){
        sut.setOption("-key", null);
    }
    @Test
    public void overrideOption(){
        sut.setOption("-key", "value");
        sut.setOption("-key", "new_value");
        assertCommandLineIsCorrect("-key","new_value");
    }
    
    @Test
    public void clearOption(){
        sut.setOption("-key", "value");
        sut.clearOption("-key");
        assertCommandLineIsCorrect();
    }
    
    @Test
    public void multipleOptions(){
        sut.setOption("-key", "value");
        sut.setOption("-diff_key", "diff_value");
        assertCommandLineIsCorrect("-key","value",
                        "-diff_key", "diff_value");
    }
    
    @Test
    public void clearAllOptions(){
        sut.setOption("-key", "value");
        sut.setOption("-diff_key", "diff_value");
        sut.clearAllOptions();
        assertCommandLineIsCorrect();
    }
    
    @Test
    public void addTarget(){
        sut.addTarget("target");
        assertCommandLineIsCorrect("target");
    }
    @Test
    public void addMultipleTargetsInDifferentCalls(){
        sut.addTarget("target");
        sut.addTarget("target2");
        assertCommandLineIsCorrect("target", "target2");
    }
    @Test
    public void addMultipleTargets(){
        sut.addTargets("target","target2");
        assertCommandLineIsCorrect("target", "target2");
    }
    
    @Test
    public void removeTargets(){
        sut.addTargets("target","target2");
        sut.removeTarget("target");
        assertCommandLineIsCorrect("target2");
    }
    @Test
    public void removeAllTargets(){
        sut.addTargets("target","target2");
        sut.removeAllTargets();
        assertCommandLineIsCorrect();
    }
    
    @Test
    public void flagsOptionsAndTargets(){
        sut.addFlag("-f");
        sut.setOption("-key","value");
        sut.addTarget("target");
        assertCommandLineIsCorrect("-key","value","-f","target");
    }
    
    private void assertCommandLineIsCorrect(String... expectedArgs){
        List<String> actualArgs= sut.getCommandLine();
        List<String> expectedCommandLine = createExpectedArguments(expectedArgs);
        assertEquals(expectedCommandLine, actualArgs);
    }
    private List<String> createExpectedArguments(String... expectedArgs) {
        List<String> expectedCommandLine = new ArrayList<String>(1+ expectedArgs.length);
        expectedCommandLine.add(path);
        expectedCommandLine.addAll(Arrays.asList(expectedArgs));
        return expectedCommandLine;
    }
    
    @Test
    public void execute() throws IOException{
        sut.addFlag("-f");
        sut.setOption("-key","value");
        sut.addTarget("target");
        
        expect(mockBuilderWrapper.command(
                    createExpectedArguments("-key","value","-f","target")))
                .andReturn(mockBuilderWrapper);
        
        expect(mockBuilderWrapper.directory(null)).andReturn(mockBuilderWrapper);
        Process mockProcess = createMock(Process.class);
        expect(mockBuilderWrapper.start()).andReturn(mockProcess);
        replayAll();
        assertEquals(mockProcess, sut.execute());
        verifyAll();
    }
    @Test
    public void executeFromDifferentWorkingDir() throws IOException{
        sut.addFlag("-f");
        sut.setOption("-key","value");
        sut.addTarget("target");
        File workingDir = new File("working/dir");
        sut.setWorkingDir(workingDir);
        
        expect(mockBuilderWrapper.command(
                    createExpectedArguments("-key","value","-f","target")))
                .andReturn(mockBuilderWrapper);
        
        expect(mockBuilderWrapper.directory(workingDir)).andReturn(mockBuilderWrapper);
        Process mockProcess = createMock(Process.class);
        expect(mockBuilderWrapper.start()).andReturn(mockProcess);
        replayAll();
        assertEquals(mockProcess, sut.execute());
        verifyAll();
    }
    
    @Test
    public void executeAndWait() throws IOException, InterruptedException{
        sut.addFlag("-f");
        sut.setOption("-key","value");
        sut.addTarget("target");
        
        expect(mockBuilderWrapper.command(
                    createExpectedArguments("-key","value","-f","target")))
                .andReturn(mockBuilderWrapper);
        
        expect(mockBuilderWrapper.directory(null)).andReturn(mockBuilderWrapper);
        Process mockProcess = createMock(Process.class);
        expect(mockBuilderWrapper.start()).andReturn(mockProcess);
        int returnValue = 1234;
        expect(mockProcess.waitFor()).andReturn(returnValue);
        replayAll();
        assertEquals(returnValue, sut.executeAndWait());
        verifyAll();
    }
}
