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

package org.jcvi.common.command.grid;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;
import org.jcvi.common.command.Command;
import org.jcvi.common.command.grid.GridJob.MemoryUnit;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestSimpleGridJobBuilder extends EasyMockSupport{

    Session gridSession;
    Command command;
    String projectCode = "projectCode";
    GridJobBuilder<SimpleGridJob> sut;
    JobTemplate jobTemplate;
    File mockExe;
    String jobName = "jobName";
    String gridJobId= "1234";
    String architecture = "myArchitecture";
    String pathToCommand = "/path/to/file";
    List<String> commandArguments = Arrays.asList("-key","value");
    @Before
    public void setup() throws SecurityException{
        gridSession = createMock(Session.class);
        //handle mockExe separately from Support class
        mockExe = EasyMock.createMock(File.class);
        expect(mockExe.canExecute()).andReturn(true);
        expect(mockExe.getAbsolutePath()).andReturn(pathToCommand);
        
        EasyMock.replay(mockExe);
        command = new Command(mockExe);
        command.setOption("-key", "value");
        jobTemplate = createMock(JobTemplate.class);
         sut = GridJobBuilders.createSimpleGridJobBuilder(gridSession, command, projectCode);
    }
    
    @Test
    public void setupAndRun() throws DrmaaException{
        
        sut.setName(jobName);
        sut.setProjectCode(projectCode);

        jobTemplate.setJobName(jobName);
        jobTemplate.setRemoteCommand(pathToCommand);
        jobTemplate.setArgs(commandArguments);
        jobTemplate.setNativeSpecification("-b y -P " + projectCode);
        jobTemplate.setJobEnvironment(Collections.emptyMap());
        
        expect(gridSession.createJobTemplate()).andReturn(jobTemplate);
        expect(gridSession.runJob(jobTemplate)).andReturn(gridJobId);
        gridSession.deleteJobTemplate(jobTemplate);
        replayAll();
        SimpleGridJob actual =sut.build();
        assertEquals(command,actual.getCommand());
        actual.runGridCommand();
        assertEquals(gridJobId, actual.getJobID());
        assertEquals(Arrays.asList(gridJobId), actual.getJobIDList());
        verifyAll();        
    }
    
    @Test
    public void setArchitecture() throws DrmaaException{
        
        sut.setName(jobName);
        sut.setProjectCode(projectCode);
        sut.setArchitecture(architecture);

        jobTemplate.setJobName(jobName);
        jobTemplate.setRemoteCommand(pathToCommand);
        jobTemplate.setArgs(commandArguments);
        jobTemplate.setNativeSpecification("-b y -l arch='" + architecture + "' -P " + projectCode);
        jobTemplate.setJobEnvironment(Collections.emptyMap());
        
        expect(gridSession.createJobTemplate()).andReturn(jobTemplate);
        expect(gridSession.runJob(jobTemplate)).andReturn(gridJobId);
        gridSession.deleteJobTemplate(jobTemplate);
        replayAll();
        SimpleGridJob actual =sut.build();
        actual.runGridCommand();
        verifyAll();        
    }
    @Test
    public void notBinaryMode() throws DrmaaException{
        
        sut.setName(jobName);
        sut.setProjectCode(projectCode);
        sut.setBinaryMode(false);
        
        jobTemplate.setJobName(jobName);
        jobTemplate.setRemoteCommand(pathToCommand);
        jobTemplate.setArgs(commandArguments);
        jobTemplate.setNativeSpecification("-b n -P " + projectCode);
        jobTemplate.setJobEnvironment(Collections.emptyMap());
        
        expect(gridSession.createJobTemplate()).andReturn(jobTemplate);
        expect(gridSession.runJob(jobTemplate)).andReturn(gridJobId);
        gridSession.deleteJobTemplate(jobTemplate);
        replayAll();
        SimpleGridJob actual =sut.build();
        actual.runGridCommand();
        verifyAll();           
    }
    
    @Test
    public void setQueue() throws DrmaaException{
        
        sut.setName(jobName);
        sut.setProjectCode(projectCode);
        
        sut.setQueue("AQueue");
        jobTemplate.setJobName(jobName);
        jobTemplate.setRemoteCommand(pathToCommand);
        jobTemplate.setArgs(commandArguments);
        jobTemplate.setNativeSpecification("-b y -l AQueue -P " + projectCode);
        jobTemplate.setJobEnvironment(Collections.emptyMap());
        
        expect(gridSession.createJobTemplate()).andReturn(jobTemplate);
        expect(gridSession.runJob(jobTemplate)).andReturn(gridJobId);
        gridSession.deleteJobTemplate(jobTemplate);
        replayAll();
        SimpleGridJob actual =sut.build();
        actual.runGridCommand();
        verifyAll();        
    }
    
    @Test
    public void setMemory() throws DrmaaException{
        
        sut.setName(jobName);
        sut.setProjectCode(projectCode);
        sut.setMemory(16, MemoryUnit.GB);
        jobTemplate.setJobName(jobName);
        jobTemplate.setRemoteCommand(pathToCommand);
        jobTemplate.setArgs(commandArguments);
        jobTemplate.setNativeSpecification("-b y -P " + projectCode + " -l memory='16G'");
        jobTemplate.setJobEnvironment(Collections.emptyMap());
        
        expect(gridSession.createJobTemplate()).andReturn(jobTemplate);
        expect(gridSession.runJob(jobTemplate)).andReturn(gridJobId);
        gridSession.deleteJobTemplate(jobTemplate);
        replayAll();
        SimpleGridJob actual =sut.build();
        actual.runGridCommand();
        verifyAll();        
    }
    
    @Test
    public void setMinCPU() throws DrmaaException{
        
        sut.setName(jobName);
        sut.setProjectCode(projectCode);
        sut.setMinCPUs(4);
        
        jobTemplate.setJobName(jobName);
        jobTemplate.setRemoteCommand(pathToCommand);
        jobTemplate.setArgs(commandArguments);
        jobTemplate.setNativeSpecification("-b y -P " + projectCode + " -pe threaded 4");
        jobTemplate.setJobEnvironment(Collections.emptyMap());
        
        expect(gridSession.createJobTemplate()).andReturn(jobTemplate);
        expect(gridSession.runJob(jobTemplate)).andReturn(gridJobId);
        gridSession.deleteJobTemplate(jobTemplate);
        replayAll();
        SimpleGridJob actual =sut.build();
        actual.runGridCommand();
        verifyAll();        
    }
    
    @Test
    public void optionalSettings() throws DrmaaException{
        Set<String> emailAddresses = new LinkedHashSet<String>();
        emailAddresses.add("test@example.com");
        emailAddresses.add("bob@example.com");
        
        File workingDir = new File("myWorkingDir");
        File inputFile = new File("inputFile");
        File outputFile = new File("outputFile");
        File errorFile = new File("errorFile");
        
        
        sut.setName(jobName);
        sut.setProjectCode(projectCode);
        for(String addr: emailAddresses){
            sut.addEmailRecipient(addr);
        }
        sut.setWorkingDirectory(workingDir);
        sut.setInputFile(inputFile);
        sut.setErrorFile(errorFile);
        sut.setOutputFile(outputFile);
        
        jobTemplate.setJobName(jobName);
        jobTemplate.setRemoteCommand(pathToCommand);
        jobTemplate.setArgs(commandArguments);
        jobTemplate.setNativeSpecification("-b y -P " + projectCode);
        jobTemplate.setJobEnvironment(Collections.emptyMap());
        jobTemplate.setEmail(emailAddresses);
        jobTemplate.setWorkingDirectory(workingDir.getAbsolutePath());
        jobTemplate.setInputPath(":"+inputFile.getAbsolutePath());
        jobTemplate.setOutputPath(":"+outputFile.getAbsolutePath());
        jobTemplate.setErrorPath(":"+errorFile.getAbsolutePath());
        
        expect(gridSession.createJobTemplate()).andReturn(jobTemplate);
        expect(gridSession.runJob(jobTemplate)).andReturn(gridJobId);
        gridSession.deleteJobTemplate(jobTemplate);
        replayAll();
        SimpleGridJob actual =sut.build();
        actual.runGridCommand();
        verifyAll();        
    }
}
